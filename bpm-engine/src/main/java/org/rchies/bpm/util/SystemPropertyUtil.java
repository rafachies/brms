package org.rchies.bpm.util;

public class SystemPropertyUtil {
	
	public static final String SNOA_BPM_SESSION_ID = "snoa.bpm.session.id";
	public static final String SNOA_BPM_LEGACY_URI = "snoa.bpm.legacy.uri";

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
