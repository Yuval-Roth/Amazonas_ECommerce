package com.amazonas.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@EntityScan(basePackages = {"com.amazonas.backend", "com.amazonas.common"})
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@EventListener
	public void handleApplicationStartedEvent(ApplicationStartedEvent event) {
		try {
			event.getApplicationContext().getBean(DataGenerator.class).generateData();
		} catch (Exception e) {
			System.out.println("Failed to generate data: " + e.getMessage());
		}
	}
}
