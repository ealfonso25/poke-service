package com.bla_middleware.poke_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class PokeServiceApplication {

	static void main(String[] args) {
		SpringApplication.run(PokeServiceApplication.class, args);
	}

}