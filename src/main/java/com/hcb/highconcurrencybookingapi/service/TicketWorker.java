package com.hcb.highconcurrencybookingapi.service;

import com.hcb.highconcurrencybookingapi.dto.TicketRequest;
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
        } catch (RuntimeException e) {
            if ("Já vendido".equals(e.getMessage())) {
                log.info("Tentativa tardia de compra para assento vendido.");
                redisTemplate.opsForValue().set(statusKey, "FAILED_SEAT_TAKEN");
            } else {
                log.error("Erro técnico:", e);
                redisTemplate.opsForValue().set(statusKey, "ERROR_DB");
            }
            redisTemplate.delete(lockKey);
        }
    }
}