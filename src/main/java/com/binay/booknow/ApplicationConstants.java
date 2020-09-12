package com.binay.booknow;

import java.text.SimpleDateFormat;

public class ApplicationConstants {
	
	public static final int TRANSACTION_TIMEOUT_DURATION = 5;
	public static final int THREADPOOL_SIZE = 20;
	public static final String ACCEPTED_DATE_FORMAT = "yyyy-MM-dd";
	public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(ACCEPTED_DATE_FORMAT);

}
