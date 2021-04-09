package com.sample.pam.security.customlogin.jwt;

/**
 * factory method, returns a JWTParser instance according to the isValidating configuration
 * @author fceresol
 *
 */

public class JwtParserFactory {
	public static final AbstractJwtParser VALIDATING_JWT_PARSER = new ValidatingJWTParser();
	public static final AbstractJwtParser NON_VALIDATING_JWT_PARSER = new NonValidatingJWTParser();
	
	public static AbstractJwtParser getJwtParser(boolean validationEnabled)
	{
		
		AbstractJwtParser ret;
		if (validationEnabled)
		{
			ret = VALIDATING_JWT_PARSER;
		}
		else
		{
			ret = NON_VALIDATING_JWT_PARSER;
		}
		return ret;
			
	}

}
