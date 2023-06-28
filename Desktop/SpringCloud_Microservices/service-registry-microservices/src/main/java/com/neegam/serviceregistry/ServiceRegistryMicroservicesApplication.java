package com.neegam.serviceregistry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class ServiceRegistryMicroservicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceRegistryMicroservicesApplication.class, args);
	}

}
