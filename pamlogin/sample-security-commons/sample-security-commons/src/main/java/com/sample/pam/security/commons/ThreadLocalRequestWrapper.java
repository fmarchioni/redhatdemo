package com.sample.pam.security.commons;

import java.io.Serializable;

/**
 * wrapper class for holding jwt token body and bearer token
 * @author fceresol
 *
 */

public class ThreadLocalRequestWrapper implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7412411737637477291L;
	private String jwtTokenAsJSON;
    private String bearerToken;


    public ThreadLocalRequestWrapper()
    {
        this.jwtTokenAsJSON = null;
        this.bearerToken = null;
        
    }


	public String getJwtTokenAsJSON() {
		return jwtTokenAsJSON;
	}


	public void setJwtTokenAsJSON(String jwtTokenAsJSON) {
		this.jwtTokenAsJSON = jwtTokenAsJSON;
	}


	public String getBearerToken() {
		return bearerToken;
	}


	public void setBearerToken(String bearerToken) {
		this.bearerToken = bearerToken;
	}


 



}
