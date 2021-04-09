/**
 * 
 */
package com.sample.pam.security.customlogin;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.jacc.PolicyContext;
import javax.security.jacc.PolicyContextException;
import javax.servlet.http.HttpServletRequest;

import org.jboss.logging.Logger;
import org.jboss.security.SimpleGroup;
import org.jboss.security.auth.spi.AbstractServerLoginModule;

import com.sample.pam.security.commons.ThreadLocalHolder;
import com.sample.pam.security.customlogin.helpers.JsonToken;
import com.sample.pam.security.customlogin.helpers.JsonTokenParser;
import com.sample.pam.security.customlogin.jwt.configuration.ModuleConfig;
import com.sample.pam.security.customlogin.jwt.exceptions.ConfigurationNotFoundException;
import com.sample.pam.security.customlogin.jwt.exceptions.InvalidTokenException;



/**
 * @author fceresol@redhat.com
 * 
 * This is the main implementation for the login module:
 * 1) extracts the JWT Token from Authorization HTTP header
 * 2) extracts the Bearer Token HTTP header
 * 3) validates (if enabled) the JWT Token
 * 4) extracts the username from JWT
 * 5) injects both the Bearer and the JWT body inside a ThreadLocal usable by BPM Process
 *
 */
public class CustomLoginModule extends AbstractServerLoginModule {
	private static final Logger logger = Logger.getLogger(CustomLoginModule.class);
	

	private static final String BEARER_TOKEN_HEADER_NAME_PROPERTY ="bearerTokenHeaderName";
	private static final String JWT_TOKEN_HEADER_NAME_PROPERTY ="jwtTokenHeaderName";
	private static final String IDP_CONFIGURATION_FOLDER_PROPERTY ="idpConfigurationFolder";
	private static final String ENABLE_JWT_VALIDATION_PROPERTY ="enableJWTValidation";
	private static final String SUPPORTED_SIGN_ALGS_PROPERTY ="supportedSignatures";
	/**
	 * The principal that will be created into the BPMS Jaas Context.
	 */
	private Principal identity;
	
	private ModuleConfig moduleConfig;


	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
			Map<String, ?> options) {
		this.moduleConfig = new ModuleConfig();
		this.moduleConfig.setJwtTokenHeaderName((String) options.get(JWT_TOKEN_HEADER_NAME_PROPERTY));
		this.moduleConfig.setBearerTokenHeaderName((String) options.get(BEARER_TOKEN_HEADER_NAME_PROPERTY));
		this.moduleConfig.setIdpConfigurationFolder((String) options.get(IDP_CONFIGURATION_FOLDER_PROPERTY));
		String enableJWTValidationString = (String) options.get(ENABLE_JWT_VALIDATION_PROPERTY);
		this.moduleConfig.setSupportedAlgs((String) options.get(SUPPORTED_SIGN_ALGS_PROPERTY));
		logger.debug("configuration loaded:");
		if(this.moduleConfig.getIdpConfigurationFolder() == null || this.moduleConfig.getIdpConfigurationFolder().isEmpty())
		{
			logger.error(IDP_CONFIGURATION_FOLDER_PROPERTY + " config property not set, the module will not work!");
		}
		else
		{
			logger.debug(IDP_CONFIGURATION_FOLDER_PROPERTY + ": " + this.moduleConfig.getIdpConfigurationFolder());
		}
		if(enableJWTValidationString == null || enableJWTValidationString.isEmpty())
		{
			logger.info(ENABLE_JWT_VALIDATION_PROPERTY + " config property not set, using default (false)");
			this.moduleConfig.setEnableJWTValidation(false);
		}
		else
		{
			logger.debug(ENABLE_JWT_VALIDATION_PROPERTY + ": " + enableJWTValidationString);
			this.moduleConfig.setEnableJWTValidation(new Boolean(enableJWTValidationString));
		}
		if(this.moduleConfig.getSupportedAlgs() == null || this.moduleConfig.getSupportedAlgs().isEmpty())
		{
			logger.info(SUPPORTED_SIGN_ALGS_PROPERTY + " config property not set, using default (RS256)");
			this.moduleConfig.setSupportedAlgs("RS256");
		}
		else
		{
			logger.debug(SUPPORTED_SIGN_ALGS_PROPERTY + ": " + this.moduleConfig.getSupportedAlgs());
			
		}
		if(this.moduleConfig.getBearerTokenHeaderName() == null || this.moduleConfig.getBearerTokenHeaderName().isEmpty())
		{
			logger.error(BEARER_TOKEN_HEADER_NAME_PROPERTY + " config property not set, the module will not work!");
		}
		else
		{
			logger.debug(BEARER_TOKEN_HEADER_NAME_PROPERTY + ": " + this.moduleConfig.getBearerTokenHeaderName());
		}
		super.initialize(subject, callbackHandler, sharedState, options);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean login() {
		HttpServletRequest request;
		String bearerVal = null;
		String jwtVal = null;
		try {
			request = (HttpServletRequest) PolicyContext.getContext("javax.servlet.http.HttpServletRequest");
			bearerVal = request.getHeader(this.moduleConfig.getBearerTokenHeaderName());
			jwtVal = request.getHeader(this.moduleConfig.getJwtTokenHeaderName()).substring("Bearer".length()).trim();

			if(bearerVal == null || bearerVal.isEmpty() || bearerVal.toLowerCase().contains("basic"))
			{
				logger.warn(this.moduleConfig.getBearerTokenHeaderName() + ": is empty, or is not a bearer skipping this login module");
				return this.failLogin();
			}
			else
			{
				logger.debug("retrieved token in property " + this.moduleConfig.getBearerTokenHeaderName() +":" + bearerVal );
			}
			if(jwtVal == null || jwtVal.isEmpty())
			{
				logger.error(this.moduleConfig.getJwtTokenHeaderName() + ": is empty, returning KO for this login module");
				return this.failLogin();
			}
			else
			{
				logger.debug("retrieved token in property " + this.moduleConfig.getJwtTokenHeaderName() +":" + jwtVal );
			}
		} catch (PolicyContextException e) {
			
			logger.error("failed to retrieve policy context:",e);
			return this.failLogin();
		}
		JsonToken token = null;
		try {
			logger.debug("parsing tokens...");
			token = JsonTokenParser.parseTokens(jwtVal,bearerVal,moduleConfig);
		} catch (InvalidTokenException | ConfigurationNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error("failed to parse the tokens: " + e.getMessage(),e);
			return this.failLogin();
		}
		logger.debug("creating principal...");		

		
		Principal p = null;

		try { 
			p = super.createIdentity(token.getUsername());
		} catch (Exception e) {
			logger.error("failed to create Identity:",e);
			return this.failLogin();
		}
		try
		{
			ThreadLocalHolder.set(token.getJwtToken(),token.getBearerToken());
		}catch(Throwable t)
		{
			logger.error(t.getMessage(),t);
		}
		logger.debug("setting principal...");	
		
		subject.getPrincipals().add(p);
		
		logger.debug("shared state...");
		// Put the principal name into the sharedState map
		sharedState.put("javax.security.auth.login.name", token.getUsername());
		
		// I set a dummy password: it's not important for the principal
		
		// I must set this value because if a previous module configured for
		// password stacking has authenticated the user, all the other stacking
		// modules will consider the user authenticated and only attempt to
		// provide a set of roles for the authorization step.
		// When password-stacking option is set to useFirstPass, this module first
		// looks for a shared user name and password under the property names
		// javax.security.auth.login.name and javax.security.auth.login.password
		// respectively in the login module shared state map.
		sharedState.put("javax.security.auth.login.password", "dummy");

		logger.debug("loginOK...");
		this.identity = p;
		super.loginOk = true;
		logger.debug("End method login");
		return true;

	}

	@Override
	protected Principal getIdentity() {
		return this.identity;
	}

	@Override
	protected Group[] getRoleSets() throws LoginException {
		// TODO Auto-generated method stub
		Group roles = new SimpleGroup("Roles");
		Group[] roleSets = {roles};
        return roleSets;
	}
	
	private boolean failLogin()
	{
		super.loginOk = false;
		this.identity=null;
		return false;
	}
	
}
