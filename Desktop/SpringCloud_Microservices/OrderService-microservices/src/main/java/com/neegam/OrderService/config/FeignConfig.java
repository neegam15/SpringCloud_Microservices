package com.neegam.OrderService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.neegam.OrderService.external.decoder.CustomErrorDecoder;

import feign.codec.ErrorDecoder;

@Configuration
public class FeignConfig{

//	Whenever ErrorDecoder is required it will call the ErrorDecoder class
	
	@Bean
	ErrorDecoder erroDecoder() {
		return new CustomErrorDecoder();
	}
}