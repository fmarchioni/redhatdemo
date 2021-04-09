package com.sample.pam.security.customlogin.jwt.configuration;

/**
 * Module configuration storage
 * @author fceresol
 *
 */
public class ModuleConfig {
	/* 
	 * the name of the header conaining the bearer token
	 */
	private String bearerTokenHeaderName;
	/*
	 * the name of the header containing the JWT token (Authorization)
	 */
	private String jwtTokenHeaderName;

	/*
	 * Folder containing the IDP Configuration Files
	 */
	private String idpConfigurationFolder;
	
	/*
	 * Supported algorithms list, if the list is present inside the idpConfiguration will be overridden
	 */
	private String supportedAlgs;

	/*
	 * toggle jwt validation
	 */
	private boolean enableJWTValidation;

	/**
	 * @return the bearerTokenHeaderName
	 */
	public String getBearerTokenHeaderName() {
		return bearerTokenHeaderName;
	}

	/**
	 * @param bearerTokenHeaderName the bearerTokenHeaderName to set
	 */
	public void setBearerTokenHeaderName(String bearerTokenHeaderName) {
		this.bearerTokenHeaderName = bearerTokenHeaderName;
	}

	/**
	 * @return the jwtTokenHeaderName
	 */
	public String getJwtTokenHeaderName() {
		return jwtTokenHeaderName;
	}

	/**
	 * @param jwtTokenHeaderName the jwtTokenHeaderName to set
	 */
	public void setJwtTokenHeaderName(String jwtTokenHeaderName) {
		this.jwtTokenHeaderName = jwtTokenHeaderName;
	}

	/**
	 * @return the idpConfigurationFolder
	 */
	public String getIdpConfigurationFolder() {
		return idpConfigurationFolder;
	}

	/**
	 * @param idpConfigurationFolder the idpConfigurationFolder to set
	 */
	public void setIdpConfigurationFolder(String idpConfigurationFolder) {
		this.idpConfigurationFolder = idpConfigurationFolder;
	}

	/**
	 * @return the supportedAlgs
	 */
	public String getSupportedAlgs() {
		return supportedAlgs;
	}

	/**
	 * @param supportedAlgs the supportedAlgs to set
	 */
	public void setSupportedAlgs(String supportedAlgs) {
		this.supportedAlgs = supportedAlgs;
	}

	/**
	 * @return the enableJWTValidation
	 */
	public boolean isEnableJWTValidation() {
		return enableJWTValidation;
	}

	/**
	 * @param enableJWTValidation the enableJWTValidation to set
	 */
	public void setEnableJWTValidation(boolean enableJWTValidation) {
		this.enableJWTValidation = enableJWTValidation;
	}
}
