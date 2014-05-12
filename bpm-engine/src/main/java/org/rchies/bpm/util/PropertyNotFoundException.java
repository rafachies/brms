package org.rchies.bpm.util;

public class PropertyNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -1543783687932494088L;
	
	public PropertyNotFoundException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
	public PropertyNotFoundException(String message) {
		super(message);
	}
}
