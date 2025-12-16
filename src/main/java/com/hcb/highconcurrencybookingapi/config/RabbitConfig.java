package com.hcb.highconcurrencybookingapi.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder; // Importante
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String QUEUE_NAME = "ticketQueue";
    public static final String DLQ_NAME = "ticketQueue.dlq";

    @Bean
    public Queue queue() {
        return QueueBuilder.durable(QUEUE_NAME)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", DLQ_NAME)
                .build();
    }

    @Bean
    public Queue dlq() {
        return QueueBuilder.durable(DLQ_NAME).build();
    }

    @Bean
    public MessageConverter jsonConverter() {
        return new JacksonJsonMessageConverter();
    }
}