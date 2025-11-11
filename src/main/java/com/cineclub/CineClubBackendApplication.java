package com.cineclub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CineClubBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CineClubBackendApplication.class, args);
	}

}