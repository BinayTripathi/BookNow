package com.binay.booknow.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.binay.booknow.ApplicationConstants;

import lombok.Data;

@Data
@ConfigurationProperties(prefix="booknow")
public class BookNowProperties {
	
	private String dataFormat = ApplicationConstants.ACCEPTED_DATE_FORMAT;
	private int threadPoolSize = ApplicationConstants.THREADPOOL_SIZE;
	

}
