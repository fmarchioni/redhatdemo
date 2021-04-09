package com.sample.pam.security.commons;

/*
 * Static class for holding Thread Local object Reference
 */
public class ThreadLocalHolder {
	
	private ThreadLocalHolder() {}

	public static  ThreadLocal<ThreadLocalRequestWrapper> requestThreadLocal = null ;
	
	public static void set(ThreadLocalRequestWrapper threadLocalRequestWrapper) {
		if (requestThreadLocal == null )
		{
			requestThreadLocal =  new ThreadLocal<>();
		}
        requestThreadLocal.set(threadLocalRequestWrapper);
	}
	
	public static void set(String jwtTokenAsJSON, String accessTokenAsJSON) {
		ThreadLocalRequestWrapper threadLocalRequestWrapper = new ThreadLocalRequestWrapper();
		threadLocalRequestWrapper.setBearerToken(accessTokenAsJSON);
		threadLocalRequestWrapper.setJwtTokenAsJSON(jwtTokenAsJSON);
		ThreadLocalHolder.set(threadLocalRequestWrapper);

   
	}

	public static void unset() {
		requestThreadLocal.remove();
	}


	public static ThreadLocalRequestWrapper getThreadLocalRequestWrapper() {

		return requestThreadLocal.get();
	}

}