package com.northcoders.recordshopAPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RecordshopApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(RecordshopApiApplication.class, args);
	}

}
