package com.app.samyang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.app.samyang"})
@EnableJpaRepositories(basePackages = {"com.app.samyang.repository"})
@EntityScan(basePackages = {"com.app.samyang.entity"})
public class SamyangApplication {

	public static void main(String[] args) {
		SpringApplication.run(SamyangApplication.class, args);
	}
}