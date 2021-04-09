package com.sample.pam.security.customlogin.jwt;

import com.sample.pam.security.customlogin.jwt.configuration.ModuleConfig;
import com.sample.pam.security.customlogin.jwt.exceptions.ConfigurationNotFoundException;
import com.sample.pam.security.customlogin.jwt.exceptions.InvalidTokenException;

public abstract class AbstractJwtParser {
	public abstract String extractUsername(String accessToken, String issuer, ModuleConfig config) throws InvalidTokenException, ConfigurationNotFoundException;

	/*
	 * Method for extracting username from claim.
	 * Since the username is in "DOMAIN\\USERNAME@TENANT format, only the USERNAME part will be returned
	 */
	protected String processUsernameFromClaim(String claimValue) {
		// remove "@TENANT" from the token
		String userWDomain = claimValue.split("@", 2)[0];
		String username;
		// check if the domain exsists and remove it

		if (userWDomain.contains("\\")) {
			username = userWDomain.split("\\")[1];
		} else {
			username = userWDomain;
		}
		if (username.contains("/")) {
			username = username.split("/")[1];
		} 

		return username;
	}
}
