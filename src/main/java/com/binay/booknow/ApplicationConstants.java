package com.binay.booknow;

import java.text.SimpleDateFormat;

public class ApplicationConstants {
	
	public static final int TRANSACTION_TIMEOUT_DURATION = 500;
	public static final int THREADPOOL_SIZE = 20;
	public static final String ACCEPTED_DATE_FORMAT = "yyyy-MM-dd";
	public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(ACCEPTED_DATE_FORMAT);
	
	public static final String URI_VI_CREATE_RESERVATION = "/v1/reservations";
	public static final String URI_V1_GET_AVAILABLE_RESERVATION_ON_DATE = "/v1/availableSlots/{date}";
	public static final String URI_VI_GET_RESERVATIONS_BY_ID = "/v1/reservations/{id}";
	public static final String URI_VI_GET_RESERVATIONS_BY_DATE = "/v1/reservation/{date}";
	public static final String URI_VI_UPDATE_RESERVATIONS_BY_ID = "/v1/reservations/{id}";
	public static final String URI_VI_DELETE_RESERVATIONS_BY_ID ="/v1/reservations/{id}";

}
