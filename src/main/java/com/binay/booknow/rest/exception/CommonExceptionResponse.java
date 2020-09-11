package com.binay.booknow.rest.exception;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class CommonExceptionResponse {

	@JsonFormat(pattern="yyyy-MM-dd hh:mm:ss")
	private Date timestamp;
    private String message;
    private String details;
    
    public CommonExceptionResponse(Date timestamp, String message, String  details) {
           super();
           this.timestamp = timestamp;
           this.message = message;
           this.details = details;
    }
    
    public Date getTimestamp() {
           return timestamp;
    }
    
    public String getMessage() {
           return message;
    }
    
    public String getDetails() {
           return details;
    }
}
