package com.binay.booknow.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EtagNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public EtagNotFoundException(String message) {
		super(message);
	}
}
