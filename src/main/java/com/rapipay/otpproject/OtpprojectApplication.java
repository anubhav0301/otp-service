package com.rapipay.otpproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@OpenAPIDefinition
@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class })
@EnableMongoRepositories(basePackages = "com.rapipay.otpproject.dao")
@EnableAsync
public class OtpprojectApplication {

	public static void main(String[] args) {
		SpringApplication.run(OtpprojectApplication.class, args);
	}

}
