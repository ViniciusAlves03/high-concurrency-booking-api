package com.hcb.highconcurrencybookingapi.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public Queue queue() {
        return new Queue("ticketQueue", true);
    }

    @Bean
    public MessageConverter jsonConverter() {
        return new JacksonJsonMessageConverter();
    }
}
