package com.hcb.highconcurrencybookingapi.background;

import com.hcb.highconcurrencybookingapi.dto.TicketRequest;
import com.hcb.highconcurrencybookingapi.exception.BusinessException;
import com.hcb.highconcurrencybookingapi.service.SeatService;
import com.hcb.highconcurrencybookingapi.utils.Messages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
public class TicketWorker {

    private final SeatService seatService;
    private final StringRedisTemplate redisTemplate;

    @RabbitListener(queues = "ticketQueue")
    public void processTicket(@NonNull TicketRequest request) {
        MDC.put("requestId", request.requestId() != null ? request.requestId().toString() : "NO-ID");

        try {
            log.info("Starting processing...");

            String lockKey = "lock:seat:" + request.seatId();
            String statusKey = "req:" + request.requestId();

            Boolean lockAcquired = redisTemplate.opsForValue()
                    .setIfAbsent(lockKey, request.userId(), Duration.ofSeconds(5));

            if (Boolean.FALSE.equals(lockAcquired)) {
                log.warn(Messages.Seat.LOCK_FAILED);
                redisTemplate.opsForValue().set(statusKey, "FAILED_SEAT_TAKEN");
                return;
            }

            try {
                seatService.reservarAssento(request);

                log.info(Messages.Ticket.PROCESSED_SUCCESS);
                redisTemplate.opsForValue().set(statusKey, "CONFIRMED");
                redisTemplate.delete(lockKey);
            } catch (BusinessException e) {
                log.info("Business rule: {}", e.getMessage());
                redisTemplate.opsForValue().set(statusKey, "FAILED_SEAT_TAKEN");
                redisTemplate.delete(lockKey);
            } catch (Exception e) {
                log.error(Messages.Ticket.PROCESSING_ERROR, e);
                redisTemplate.opsForValue().set(statusKey, "ERROR_RETRYING");
                redisTemplate.delete(lockKey);

                throw e;
            }

        } finally {
            MDC.clear();
        }
    }
}