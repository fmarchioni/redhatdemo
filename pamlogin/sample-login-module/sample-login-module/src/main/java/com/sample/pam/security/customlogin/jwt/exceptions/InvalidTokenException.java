package com.sample.pam.security.customlogin.jwt.exceptions;

public class InvalidTokenException extends Exception {

	public InvalidTokenException(String message, Throwable cause) {
		super(message,cause);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8999737491526933172L;

}
