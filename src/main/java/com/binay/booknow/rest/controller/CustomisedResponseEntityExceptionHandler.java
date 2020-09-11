package com.binay.booknow.rest.controller;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.binay.booknow.rest.exception.CommonExceptionResponse;
import com.binay.booknow.rest.exception.EtagMismatchException;
import com.binay.booknow.rest.exception.EtagNotFoundException;
import com.binay.booknow.rest.exception.ReservationNotFoundException;

@ControllerAdvice
@RestController
public class CustomisedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		CommonExceptionResponse commonExceptionResponse = new CommonExceptionResponse(new Date(), "Invalid  Arguement",
				ex.getBindingResult().toString());

		return new ResponseEntity(commonExceptionResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(EtagMismatchException.class)
	public final ResponseEntity<CommonExceptionResponse> handleEtagErrorException(EtagMismatchException ex,
			HttpServletResponse response, WebRequest request) throws Exception {
			CommonExceptionResponse commonExceptionResponse = new CommonExceptionResponse(new Date(), ex.getMessage(),
					request.getDescription(false));
    		return new ResponseEntity(commonExceptionResponse ,  HttpStatus.PRECONDITION_FAILED);
	}
	
	@ExceptionHandler(EtagNotFoundException.class)
	public final ResponseEntity<CommonExceptionResponse> handleEtagMissingException(EtagNotFoundException ex,
			HttpServletResponse response, WebRequest request) throws Exception {
			CommonExceptionResponse commonExceptionResponse = new CommonExceptionResponse(new Date(), ex.getMessage(),
					request.getDescription(false));
    		return new ResponseEntity(commonExceptionResponse ,  HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ReservationNotFoundException.class)
	public final ResponseEntity<CommonExceptionResponse> handleUserNotFoundException(ReservationNotFoundException ex,
			HttpServletResponse response, WebRequest request) throws Exception {
		CommonExceptionResponse commonExceptionResponse = new CommonExceptionResponse(new Date(), ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity(commonExceptionResponse ,  HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(ObjectOptimisticLockingFailureException.class)
	public final ResponseEntity<CommonExceptionResponse> handleConcurrentUpdateException(ObjectOptimisticLockingFailureException ex,
			HttpServletResponse response, WebRequest request) throws Exception {
		CommonExceptionResponse commonExceptionResponse = new CommonExceptionResponse(new Date(), ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity(commonExceptionResponse ,  HttpStatus.CONFLICT);
	}

	@ExceptionHandler(Exception.class)
	public final ResponseEntity<Object> handleAllException(Exception ex, WebRequest request) throws Exception {
		CommonExceptionResponse commonExceptionResponse = new CommonExceptionResponse(new Date(), ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity(commonExceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
