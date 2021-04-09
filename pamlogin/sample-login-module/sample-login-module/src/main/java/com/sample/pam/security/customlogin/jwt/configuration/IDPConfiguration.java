package com.sample.pam.security.customlogin.jwt.configuration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;

/**
 * IDP Configuration cache element
 * 
 * @author fceresol
 *
 */
public class IDPConfiguration {
	private String issuer;
	private JWKSet signatures;
	private Set<JWSAlgorithm> supportedAlgorithms;
	public IDPConfiguration()
	{
		this.issuer = null;
		this.signatures = null;
		this.supportedAlgorithms = new HashSet<>();	
	}
	/**
	 * @return the issuer
	 */
	public String getIssuer() {
		return issuer;
	}
	/**
	 * @param issuer the issuer to set
	 */
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}
	/**
	 * @return the signatures
	 */
	public JWKSet getSignatureKeys() {
		return signatures;
	}
	/**
	 * @param signatures the signatures to set
	 */
	public void setSignatureKeys(JWKSet signatures) {
		this.signatures = signatures;
	}
	public void setSupportedAlgorithms(List<String> supportedAlgList) {

		for(String algorithm : supportedAlgList)
		{
			this.supportedAlgorithms.add(JWSAlgorithm.parse(algorithm));
		}
		
	}
	public Set<JWSAlgorithm> getSupportedAlgorithms() {
		// TODO Auto-generated method stub
		return this.supportedAlgorithms;
	}
	
	
	
}
