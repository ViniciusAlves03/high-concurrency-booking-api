package com.hcb.highconcurrencybookingapi.config;

import com.hcb.highconcurrencybookingapi.model.Seat;
import com.hcb.highconcurrencybookingapi.repository.SeatRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSetup {

    @Bean
    CommandLineRunner initDatabase(SeatRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                for (int i = 1; i <= 100; i++) {
                    Seat seat = new Seat();
                    seat.setSeatNumber("A" + i);
                    repository.save(seat);
                }
                System.out.println("✅ 100 Assentos criados no banco!");
            }
        };
    }
}
