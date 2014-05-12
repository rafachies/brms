package org.rchies.bpm.util;

public class SystemPropertyUtil {
	
	public static final String BPM_SESSION_ID = "bpm.session.id";

	public Integer getInteger(String propertyKey) {
		String propertyValue = System.getProperty(propertyKey);
		if (propertyValue == null) {
			throw new PropertyNotFoundException("System property not found: " + propertyKey);
		}
		return Integer.valueOf(propertyValue);
	}
	
	public String getString(String propertyKey) {
		return System.getProperty(propertyKey);
	}
}
