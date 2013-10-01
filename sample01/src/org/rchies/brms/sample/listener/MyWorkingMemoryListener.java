package org.rchies.brms.sample.listener;

import org.drools.event.rule.ObjectInsertedEvent;
import org.drools.event.rule.ObjectRetractedEvent;
import org.drools.event.rule.ObjectUpdatedEvent;
import org.drools.event.rule.WorkingMemoryEventListener;

public class MyWorkingMemoryListener implements WorkingMemoryEventListener {

	@Override
	public void objectInserted(ObjectInsertedEvent event) {
		System.out.println("[WM Listener] Object inserted: " + event.getObject().toString());
	}
	@Override
	public void objectRetracted(ObjectRetractedEvent event) {
		System.out.println("[WM Listener] Object retracted: " + event.getOldObject().toString());
	}
	@Override
	public void objectUpdated(ObjectUpdatedEvent event) {
		System.out.println("[WM Listener] Object updated: " + event.getObject().toString());
	}

}
