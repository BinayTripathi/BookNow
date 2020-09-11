package com.binay.booknow.configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.binay.booknow.configuration.properties.BookNowProperties;

@Configuration
@EnableConfigurationProperties(BookNowProperties.class)
public class ExecutorServiceConfiguration {

	//private BookNowProperties properties;
	
	//@Bean
	public ExecutorService threadPoolExecutorService() {
		
		ExecutorService executorService = Executors.newWorkStealingPool(100);
		return executorService;
	}
}
