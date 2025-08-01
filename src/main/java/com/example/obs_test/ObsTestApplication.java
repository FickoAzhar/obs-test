package com.example.obs_test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport
@EnableJpaRepositories(basePackages = "com.example.obs_test.repository")
@EntityScan(basePackages = "com.example.obs_test.entity")
public class ObsTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(ObsTestApplication.class, args);
	}

}
