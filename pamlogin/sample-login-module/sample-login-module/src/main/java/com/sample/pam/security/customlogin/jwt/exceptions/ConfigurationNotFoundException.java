package com.sample.pam.security.customlogin.jwt.exceptions;

public class ConfigurationNotFoundException extends Exception {

	public ConfigurationNotFoundException(String message, Throwable cause) {
		super(message,cause);
	}

	public ConfigurationNotFoundException(String message) {
		super(message);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 8999737491526923172L;

}
