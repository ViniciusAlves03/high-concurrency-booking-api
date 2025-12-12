package com.hcb.highconcurrencybookingapi.controller;

import com.hcb.highconcurrencybookingapi.dto.TicketRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.UUID;

@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final RabbitTemplate rabbitTemplate;
    private final StringRedisTemplate redisTemplate;

    @PostMapping("/buy")
    public ResponseEntity<UUID> buyTicket(@RequestBody TicketRequest requestBody) {
        UUID requestId = UUID.randomUUID();

        TicketRequest fullRequest = new TicketRequest(requestId, requestBody.seatId(), requestBody.userId());

        redisTemplate.opsForValue().set("req:" + requestId, "PROCESSING", Duration.ofMinutes(5));

        rabbitTemplate.convertAndSend("ticketQueue", fullRequest);

        return ResponseEntity.accepted().body(requestId);
    }

    @GetMapping("/status/{requestId}")
    public ResponseEntity<String> checkStatus(@PathVariable UUID requestId) {
        String status = redisTemplate.opsForValue().get("req:" + requestId);
        if (status == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(status);
    }
}
