package org.rchies.brms.sample.listener;

import org.drools.SystemEventListener;

public class MySystemEventListener implements SystemEventListener {


	@Override
	public void exception(Throwable throwable) {
		throw new RuntimeException(throwable);
	}

	@Override
	public void exception(String message, Throwable throwable) {
		throw new RuntimeException(message, throwable);
	}

	@Override
	public void debug(String arg0) {
		System.out.println(arg0);
	}
	
	@Override
	public void debug(String arg0, Object arg1) {
		System.out.println(arg0 + " " + arg1);
	}

	@Override
	public void info(String arg0) {
		System.out.println(arg0);
	}

	@Override
	public void info(String arg0, Object arg1) {
		System.out.println(arg0 + " " + arg1);
	}

	@Override
	public void warning(String arg0) {
		System.out.println(arg0);
	}

	@Override
	public void warning(String arg0, Object arg1) {
		System.out.println(arg0 + " " + arg1);
	}

}
