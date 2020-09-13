package com.binay.booknow.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.binay.booknow.ApplicationConstants;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix="booknow")
public class BookNowProperties {
	
	private String dataFormat = ApplicationConstants.ACCEPTED_DATE_FORMAT;	

}
