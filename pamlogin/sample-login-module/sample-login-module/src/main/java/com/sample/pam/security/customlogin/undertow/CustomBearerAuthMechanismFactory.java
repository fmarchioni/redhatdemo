package com.sample.pam.security.customlogin.undertow;

import java.util.Map;

import javax.security.auth.callback.CallbackHandler;

import org.jboss.logging.Logger;
import org.wildfly.security.http.HttpAuthenticationException;
import org.wildfly.security.http.HttpServerAuthenticationMechanism;
import org.wildfly.security.http.HttpServerAuthenticationMechanismFactory;


public class CustomBearerAuthMechanismFactory  implements HttpServerAuthenticationMechanismFactory {

    /*
     * This example uses service loader discovery to locate the factory, if a Provider was used instead visibility could be
     * reduced to be only accessible by the provider.
     */

    static final String CUSTOM_NAME = "Acme";
    private static final Logger LOGGER = Logger.getLogger(CustomBearerAuthMechanismFactory.class);
    
    /**
     * HttpServerAuthenticationMechanism Factory required by Elytron subsystem for creating 
     * and exposing a custom Authentication mechanism, in our case "Acme"
     */
    
    public HttpServerAuthenticationMechanism createAuthenticationMechanism(String mechanismName, Map<String, ?> properties, CallbackHandler callbackHandler) throws HttpAuthenticationException {
    	LOGGER.debug("createAuthenticationMechanism: "+ mechanismName);
        if (CUSTOM_NAME.equals(mechanismName)) {
            /*
             * The properties could be used at this point to further customise the behaviour of the mechanism.
             */
            return new CustomBearerAuthMechanism(callbackHandler);
        }

        return null;
    }

    public String[] getMechanismNames(Map<String, ?> properties) {
    	LOGGER.info("exposing mechanism: "+ CUSTOM_NAME);
        /*         * 
         * At this stage the properties could be queried to only return a mechanism if compatible with the properties provided.
         */
        return new String[] { CUSTOM_NAME };
    }

}
