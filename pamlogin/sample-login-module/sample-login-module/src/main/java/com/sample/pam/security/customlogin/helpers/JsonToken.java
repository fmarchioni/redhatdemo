package com.sample.pam.security.customlogin.helpers;

/*
 * utility class for holding username and token informations
 */

public class JsonToken {
	private String username;
	private String jwtToken;
	private String bearerToken;
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return the jwtToken
	 */
	public String getJwtToken() {
		return jwtToken;
	}
	/**
	 * @param jwtToken the jwtToken to set
	 */
	public void setJwtToken(String jwtToken) {
		this.jwtToken = jwtToken;
	}

	/**
	 * @return the bearerToken
	 */
	public String getBearerToken() {
		return bearerToken;
	}

	/**
	 * @param bearerToken the bearerToken to set
	 */
	public void setBearerToken(String bearerToken) {
		this.bearerToken = bearerToken;
	}

	
	
}
