package com.sample.pam.security.customlogin.jwt;

import java.io.IOException;
import java.util.Base64;

import org.jboss.logging.Logger;

import com.sample.pam.security.customlogin.jwt.configuration.IDPConfiguration;
import com.sample.pam.security.customlogin.jwt.configuration.ModuleConfig;
import com.sample.pam.security.customlogin.jwt.exceptions.ConfigurationNotFoundException;
import com.sample.pam.security.customlogin.jwt.exceptions.InvalidTokenException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Non validating JWT Parser, extract username without checking the JWT
 * @author fceresol
 *
 */
public class NonValidatingJWTParser extends AbstractJwtParser {

	private static final Logger LOGGER = Logger.getLogger(NonValidatingJWTParser.class);
	@Override
	public String extractUsername(String jwtToken, String issuer, ModuleConfig config) throws InvalidTokenException, ConfigurationNotFoundException {
		IDPConfiguration idpConfig = null;
		//read json file data to String
		String[] jwtTokenSplitted = jwtToken.split("\\.",3); 
		
		LOGGER.debug("decoding token...");
		byte[] jwtTokenBodyData = Base64.getDecoder().decode(jwtTokenSplitted[1]);

		LOGGER.debug("extracting username...");
		//create ObjectMapper instance
		ObjectMapper objectMapper = new ObjectMapper();
		//read JSON like DOM Parser
		JsonNode rootNode;
		try {
			rootNode = objectMapper.readTree(jwtTokenBodyData);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new InvalidTokenException("failed to read token: " , e);
		}
		JsonNode usernameToken = rootNode.path("sub");

		return super.processUsernameFromClaim(usernameToken.asText());
		
	}
}
