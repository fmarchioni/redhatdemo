package com.sample.pam.security.customlogin.helpers;

import java.io.IOException;
import java.util.Base64;

import com.sample.pam.security.customlogin.jwt.AbstractJwtParser;
import com.sample.pam.security.customlogin.jwt.JwtParserFactory;
import org.jboss.logging.Logger;

import com.sample.pam.security.customlogin.jwt.configuration.ModuleConfig;
import com.sample.pam.security.customlogin.jwt.exceptions.ConfigurationNotFoundException;
import com.sample.pam.security.customlogin.jwt.exceptions.InvalidTokenException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * Token Parser 
 * Extracts username from JWT
 * Calls the JWT (validating or non-validating) parser according to the configuration
 */

public class JsonTokenParser {
	private static final Logger logger = Logger.getLogger(JsonTokenParser.class);
	public static JsonToken parseTokens(String jwtToken,String bearerToken,ModuleConfig config) throws InvalidTokenException, ConfigurationNotFoundException
	{
		//read json file data to String
		logger.debug("decoding tokens...");
		String[] jwtTokenSplitted = jwtToken.split("\\.",3);
		logger.debug("extracting issuer step 1 ...");
		byte[] jwtTokenBodyData = Base64.getDecoder().decode(jwtTokenSplitted[1]);

		
		logger.debug("extracting issuer step 2...");
		//create ObjectMapper instance
		ObjectMapper objectMapper = null;

		objectMapper = new ObjectMapper();

		logger.debug("extracting issuer step 3...");
		//read JSON like DOM Parser
		JsonNode rootNode;
		
		try {
			logger.debug("extracting issuer step 4...");
			rootNode = objectMapper.readTree(jwtTokenBodyData);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("error while parsing token data:" + e.getMessage(), e);
			throw new InvalidTokenException("error while parsing token data:" + e.getMessage(), e);
		}
		logger.debug("extracting issuer step 5...");
		JsonNode issuerToken = rootNode.path("iss");
		logger.debug("extracting issuer step 6...");
		String issuer = issuerToken.asText();
		logger.debug("issuer: " + issuer);
		
		AbstractJwtParser parser = JwtParserFactory.getJwtParser(config.isEnableJWTValidation());
		
		String username = parser.extractUsername(jwtToken, issuer,config );
		
		JsonToken token = new JsonToken();
		token.setJwtToken(new String(jwtTokenBodyData));
		token.setBearerToken(bearerToken);
		token.setUsername(username);
		logger.debug("return token");
		return token;


	}
}
