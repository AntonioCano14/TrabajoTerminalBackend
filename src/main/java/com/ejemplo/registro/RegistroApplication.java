package com.ejemplo.registro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.ejemplo.registro.repository")
public class RegistroApplication {
	public static void main(String[] args) {
		SpringApplication.run(RegistroApplication.class, args);
	}
}

