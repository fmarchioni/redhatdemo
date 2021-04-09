package com.sample.pam.security.customlogin.undertow;

import static org.wildfly.security.http.HttpConstants.AUTHORIZATION;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.AuthorizeCallback;

import org.jboss.logging.Logger;
import org.wildfly.security.auth.callback.AuthenticationCompleteCallback;
import org.wildfly.security.auth.callback.EvidenceVerifyCallback;
import org.wildfly.security.auth.callback.IdentityCredentialCallback;
import org.wildfly.security.credential.PasswordCredential;
import org.wildfly.security.evidence.PasswordGuessEvidence;
import org.wildfly.security.http.HttpAuthenticationException;
import org.wildfly.security.http.HttpServerAuthenticationMechanism;
import org.wildfly.security.http.HttpServerMechanismsResponder;
import org.wildfly.security.http.HttpServerRequest;
import org.wildfly.security.http.HttpServerResponse;
import org.wildfly.security.password.interfaces.ClearPassword;

/*
 * Elytron HTTP Server Authentication Mechanism for dealing with custom Authentication
 * exposes custom authentication method.
 */
public class CustomBearerAuthMechanism implements HttpServerAuthenticationMechanism{
	private static final Logger LOGGER = Logger.getLogger(CustomBearerAuthMechanism.class);
	private static final String CUSTOM_NAME = "Acme";
	
	 private static final HttpServerMechanismsResponder RESPONDER = new HttpServerMechanismsResponder() {
	        /*
	         * As the responses are always the same a static instance of the responder can be used.
	         */

	        public void sendResponse(HttpServerResponse response) throws HttpAuthenticationException {
	            
	            response.setStatusCode(401);
	        }
	    };
	
	private CallbackHandler callbackHandler;
	CustomBearerAuthMechanism(final CallbackHandler callbackHandler) {
		LOGGER.debug("CustomBearerAuthMechanism created");
        this.callbackHandler = callbackHandler;
    }

	
	public String getMechanismName() {
		
		return CUSTOM_NAME;
	}

	public void evaluateRequest(HttpServerRequest request) throws HttpAuthenticationException {
		
		LOGGER.debug("evaluateRequest invoked");
		// checks if Authentication is "Bearer", if not return the noAuthenticationInProgress to allow other authentication methods 
		String authHeader = request.getFirstRequestHeaderValue(AUTHORIZATION);
		if (authHeader == null || !authHeader.startsWith("Bearer")) {
            /*
             * This mechanism is not performing authentication at this time however other mechanisms may be in use concurrently and could succeed so we register
             */
            request.noAuthenticationInProgress(RESPONDER);
            return;
        }
		
		/*
		 * extract the username to be passed to the security context, this step is not strictly necessary indeed since the correct principal will be added by the CustomLoginModule.
		 */
		String username="bearer";
		String password="password";
		
				
		NameCallback nameCallback = new NameCallback("Remote Authentication Name", username);
        nameCallback.setName(username);
        final PasswordGuessEvidence evidence = new PasswordGuessEvidence(password.toCharArray());
        EvidenceVerifyCallback evidenceVerifyCallback = new EvidenceVerifyCallback(evidence);

        try {
            callbackHandler.handle(new Callback[] { nameCallback, evidenceVerifyCallback });
        } catch (IOException | UnsupportedCallbackException e) {
        	LOGGER.error("caught " + e.getClass().getName() + " handling callbacks: "+ e.getMessage(),e);
            throw new HttpAuthenticationException(e);
        }

        if (evidenceVerifyCallback.isVerified() == false) {
            request.authenticationFailed("JWT Authentication Failed", RESPONDER);
            LOGGER.error("JWT Authentication Failed");
        }
        else
        {
        	LOGGER.debug("JWT Authentication Success!");
        }

        /*
         * This next callback is optional, as we have the users password we can associate it with the private credentials of the
         * SecurityIdentity so it can be used again later.
         */

        try {
            callbackHandler.handle(new Callback[] {new IdentityCredentialCallback(new PasswordCredential(ClearPassword.createRaw(ClearPassword.ALGORITHM_CLEAR, password.toCharArray())), true)});
        } catch (IOException | UnsupportedCallbackException e) {
        	LOGGER.error("caught " + e.getClass().getName() + " handling callbacks: "+ e.getMessage(),e);
            throw new HttpAuthenticationException(e);
        }

        /*
         * The next callback is important, although at this stage they are authenticated an authorization check is now needed to
         * ensure the user has the LoginPermission granted allowing them to login.
         */

        AuthorizeCallback authorizeCallback = new AuthorizeCallback(username, username);

        try {
            callbackHandler.handle(new Callback[] {authorizeCallback});
            
            /*
             * Finally this example is very simple so we can deduce the outcome from the callbacks so far, however some
             * mechanisms may still go on to take additional information into account and make an alternative decision so a
             * callback is required to report the final outcome.
             */

            if (authorizeCallback.isAuthorized()) {
                callbackHandler.handle(new Callback[] { AuthenticationCompleteCallback.SUCCEEDED });
                LOGGER.debug("authentication completed successfully");
                request.authenticationComplete();
            } else {
                callbackHandler.handle(new Callback[] { AuthenticationCompleteCallback.FAILED });
                LOGGER.warn("authentication Failed for user" + username);
                request.authenticationFailed("Authorization check failed.", RESPONDER);
            }
            return;
        } catch (IOException | UnsupportedCallbackException e) {
            throw new HttpAuthenticationException(e);
        }
    }
	
	}
