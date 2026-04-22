package com.orchex.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class OrchexApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrchexApplication.class, args);
	}
}
