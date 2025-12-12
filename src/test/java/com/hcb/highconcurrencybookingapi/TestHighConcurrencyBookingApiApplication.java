package com.hcb.highconcurrencybookingapi;

import org.springframework.boot.SpringApplication;

public class TestHighConcurrencyBookingApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(HighConcurrencyBookingApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
