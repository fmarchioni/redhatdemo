package com.sample.pam.security.customlogin.jwt;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.sample.pam.security.customlogin.jwt.configuration.IDPConfiguration;
import com.sample.pam.security.customlogin.jwt.configuration.IDPConfigurationLoader;
import com.sample.pam.security.customlogin.jwt.configuration.ModuleConfig;
import com.sample.pam.security.customlogin.jwt.exceptions.ConfigurationNotFoundException;
import com.sample.pam.security.customlogin.jwt.exceptions.InvalidTokenException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;

/**
 * validating JWT Parser, verifies the JWT expiration and signature then extracts the username.
 * @author fceresol
 *
 */
public class ValidatingJWTParser extends AbstractJwtParser{

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String extractUsername(String accessToken, String issuer, ModuleConfig config) throws InvalidTokenException, ConfigurationNotFoundException
	{

			// Create a JWT processor for the access tokens
			ConfigurableJWTProcessor<SecurityContext> jwtProcessor =
			    new DefaultJWTProcessor<>();
			IDPConfiguration idpConfig = IDPConfigurationLoader.getLoader(config.getIdpConfigurationFolder()).loadIDPConfiguration(issuer);

			// The public RSA keys to validate the signatures will be sourced from the
			// OAuth 2.0 server's JWK set, published at a well-known URL. The RemoteJWKSet
			// object caches the retrieved keys to speed up subsequent look-ups and can
			// also handle key-rollover
			JWKSource<SecurityContext> keySource = new ImmutableJWKSet<>(idpConfig.getSignatureKeys());
			   

			// The expected JWS algorithm of the access tokens (agreed out-of-band)
			Set<JWSAlgorithm> expectedJWSAlg = idpConfig.getSupportedAlgorithms();

			// Configure the JWT processor with a key selector to feed matching public
			// RSA keys sourced from the JWK set URL
			JWSKeySelector<SecurityContext> keySelector =
			    new JWSVerificationKeySelector<>(expectedJWSAlg, keySource);

			jwtProcessor.setJWSKeySelector(keySelector);

			// Set the required JWT claims for access tokens issued by the server
			jwtProcessor.setJWTClaimsSetVerifier(new DefaultJWTClaimsVerifier(
			    new JWTClaimsSet.Builder().issuer(idpConfig.getIssuer()).build(),
			    new HashSet<>(Arrays.asList("sub", "iat", "exp"))));
			
			
			// Process the token
			SecurityContext ctx = null; // optional context parameter, not required here
			JWTClaimsSet claimsSet = null;
			try {
				claimsSet = jwtProcessor.process(accessToken, ctx);
			} catch (ParseException | BadJOSEException | JOSEException e) {
				throw new InvalidTokenException("invalid token received: " + e.getMessage(),e);
			}
			
			return super.processUsernameFromClaim(claimsSet.getSubject());
			// Print out the token claims set

	}
}
