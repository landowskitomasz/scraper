package com.tennizoom.scraper.exception;

public class ValidateException extends RuntimeException{

	private static final long serialVersionUID = 5438553465693215581L;

	public ValidateException(String message, Throwable e) {
		super(message, e);
	}
	
	public ValidateException(String message) {
		super(message);
	}
}
