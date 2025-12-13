package com.hcb.highconcurrencybookingapi.background;

import com.hcb.highconcurrencybookingapi.dto.TicketRequest;
import com.hcb.highconcurrencybookingapi.exception.BusinessException;
import com.hcb.highconcurrencybookingapi.service.SeatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public void processTicket(TicketRequest request) {
        String lockKey = "lock:seat:" + request.seatId();
        String statusKey = "req:" + request.requestId();

        Boolean lockAcquired = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, request.userId(), Duration.ofSeconds(5));

        if (Boolean.FALSE.equals(lockAcquired)) {
            redisTemplate.opsForValue().set(statusKey, "FAILED_SEAT_TAKEN");
            return;
        }

        try {
            seatService.reservarAssento(request);

            redisTemplate.opsForValue().set(statusKey, "CONFIRMED");
            redisTemplate.delete(lockKey);
        } catch (BusinessException e) {
            redisTemplate.opsForValue().set(statusKey, "FAILED_SEAT_TAKEN");
            redisTemplate.delete(lockKey);
        } catch (Exception e) {
            redisTemplate.opsForValue().set(statusKey, "ERROR_DB");
            redisTemplate.delete(lockKey);
        }
    }
}