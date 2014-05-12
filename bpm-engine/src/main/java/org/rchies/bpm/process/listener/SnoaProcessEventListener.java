package org.rchies.bpm.process.listener;

import javax.inject.Inject;

import org.drools.event.process.ProcessCompletedEvent;
import org.drools.event.process.ProcessEventListener;
import org.drools.event.process.ProcessNodeLeftEvent;
import org.drools.event.process.ProcessNodeTriggeredEvent;
import org.drools.event.process.ProcessStartedEvent;
import org.drools.event.process.ProcessVariableChangedEvent;
import org.slf4j.Logger;


public class SnoaProcessEventListener implements ProcessEventListener {

	@Inject
	private Logger logger;
	
	

	@Override
	public void afterProcessCompleted(ProcessCompletedEvent event) {
		logger.info("process completed");
	}

	@Override
	public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
		logger.info("before node triggered");
	}
	
	@Override
	public void beforeNodeLeft(ProcessNodeLeftEvent event) {
		logger.info("before node left");
	}

	@Override
	public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
		logger.info("after node triggered");
	}

	@Override
	public void afterProcessStarted(ProcessStartedEvent arg0) {
	}

	@Override
	public void afterNodeLeft(ProcessNodeLeftEvent arg0) {
	}
	@Override
	public void afterVariableChanged(ProcessVariableChangedEvent arg0) {
	}

	@Override
	public void beforeProcessCompleted(ProcessCompletedEvent arg0) {
	}

	@Override
	public void beforeProcessStarted(ProcessStartedEvent arg0) {
	}

	@Override
	public void beforeVariableChanged(ProcessVariableChangedEvent arg0) {
	}
}