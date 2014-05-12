package org.rchies.bpm.task.listener;

import org.jbpm.task.event.TaskClaimedEvent;
import org.jbpm.task.event.TaskCompletedEvent;
import org.jbpm.task.event.TaskEventListener;
import org.jbpm.task.event.TaskFailedEvent;
import org.jbpm.task.event.TaskSkippedEvent;

public class CustomTaskEventListener implements TaskEventListener {

	
	@Override
	public void taskClaimed(TaskClaimedEvent arg0) {
	}

	@Override
	public void taskCompleted(TaskCompletedEvent arg0) {
	}

	@Override
	public void taskFailed(TaskFailedEvent arg0) {
	}

	@Override
	public void taskSkipped(TaskSkippedEvent arg0) {
	}
}