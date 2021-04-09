package com.sample.pam.security.customlogin.jwt.configuration;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jboss.logging.Logger;

import com.sample.pam.security.customlogin.jwt.exceptions.ConfigurationNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.JWKSet;

/**
 * loads the IDPConfiguration from filesystem and keep a cached copy
 * @author fceresol
 *
 */
public class IDPConfigurationLoader {
	private static final Logger LOGGER = Logger.getLogger(IDPConfigurationLoader.class);
	private static IDPConfigurationLoader loader = null;
	private HashMap<String, IDPConfiguration> idpConfigurations;
	private String baseFilePath;
	private String defaultAlgorithms;

	private IDPConfigurationLoader(String baseFilePath) {
		idpConfigurations = new HashMap<String, IDPConfiguration>();
		this.baseFilePath = baseFilePath;
	}

	public static IDPConfigurationLoader getLoader(String baseFilePath) {
		if (loader == null) {
			loader = new IDPConfigurationLoader(baseFilePath);
		}
		return loader;
	}

	public IDPConfiguration loadIDPConfiguration(String issuer) throws ConfigurationNotFoundException {
		IDPConfiguration ret;
		LOGGER.info("retrieving configuration for " + issuer);
		if (idpConfigurations.containsKey(issuer)) {
			ret = idpConfigurations.get(issuer);
		} else {
			ret = loadIDPConfigurationFromFileSystem(issuer);
		}
		return ret;
	}

	private IDPConfiguration loadIDPConfigurationFromFileSystem(String issuer) throws ConfigurationNotFoundException {
		LOGGER.debug("issuer: " + issuer);
		String idp = issuer.substring(issuer.indexOf(':') + 3);
		LOGGER.debug("idp: " + idp);
		idp = idp.substring(0, idp.indexOf('/'));
		LOGGER.debug("idp: " + idp);
		File well_known = new File(this.baseFilePath + File.separator + idp + "_openid-configuration.json");
		File jwks = new File(this.baseFilePath + File.separator + idp + "_jwks.json");
		if (!well_known.canRead()) {
			throw new ConfigurationNotFoundException(
					"the file " + well_known.getPath() + " is not present or cannot be read!");
		}
		if (!jwks.canRead()) {
			throw new ConfigurationNotFoundException(
					"the file " + jwks.getPath() + " is not present or cannot be read!");
		}
		IDPConfiguration idpConfig = new IDPConfiguration();
		ObjectMapper well_knownMapper = new ObjectMapper();

		try {
			// recupero l'issuer dalla well_known

			JsonNode well_known_root = well_knownMapper.readTree(well_known);
			JsonNode issuerClaim = well_known_root.path("issuer");
			JsonNode supportedAlgClaim = well_known_root.path("token_endpoint_auth_signing_alg_values_supported");
			List<String> supportedAlgList = new ArrayList<String>();
			if (issuerClaim.isMissingNode()) {
				throw new ConfigurationNotFoundException("the issuer claim is missing! cannot proceed further!");
			}

			idpConfig.setIssuer(issuerClaim.asText());

			if (supportedAlgClaim.isMissingNode()) {
				// LOGGER.warn
				supportedAlgList = Arrays.asList(this.defaultAlgorithms.split(","));
			} else {
				supportedAlgList = new ArrayList<String>(supportedAlgClaim.size());
				Iterator<JsonNode> supportedArgClaimIterator = supportedAlgClaim.elements();
				while (supportedArgClaimIterator.hasNext()) {
					JsonNode element = supportedArgClaimIterator.next();
					supportedAlgList.add(element.asText());
				}
			}

			JWKSet signatures = JWKSet.load(jwks);

			idpConfig.setSignatureKeys(signatures);
			idpConfig.setSupportedAlgorithms(supportedAlgList);

			this.idpConfigurations.put(idp, idpConfig);
			return idpConfig;
		} catch (JsonProcessingException e) {
			LOGGER.error("failed to load IDP configuration: " + e.getMessage(), e);
			throw new ConfigurationNotFoundException("failed to load IDP configuration: " + e.getMessage(), e);
		
		} catch (IOException e) {
			LOGGER.error("failed to load IDP configuration: " + e.getMessage(), e);
			throw new ConfigurationNotFoundException("failed to load IDP configuration: " + e.getMessage(), e);
		} catch (ParseException e) {
			LOGGER.error("failed to load IDP configuration: " + e.getMessage(), e);
			throw new ConfigurationNotFoundException("failed to load IDP configuration: " + e.getMessage(), e);
		}


	}

}
