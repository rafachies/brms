package org.rchies.brms.sample.listener;

import org.drools.event.rule.ActivationCancelledEvent;
import org.drools.event.rule.ActivationCreatedEvent;
import org.drools.event.rule.AfterActivationFiredEvent;
import org.drools.event.rule.AgendaEventListener;
import org.drools.event.rule.AgendaGroupPoppedEvent;
import org.drools.event.rule.AgendaGroupPushedEvent;
import org.drools.event.rule.BeforeActivationFiredEvent;
import org.drools.event.rule.RuleFlowGroupActivatedEvent;
import org.drools.event.rule.RuleFlowGroupDeactivatedEvent;

public class MyAgendaListener implements AgendaEventListener{
	@Override
	public void activationCancelled(ActivationCancelledEvent event) {
		System.out.println("[Agenda Listener] Activation cancelled: " + event.getActivation().getRule());
	}
	@Override
	public void activationCreated(ActivationCreatedEvent event) {
		System.out.println("[Agenda Listener] Activation created: " + event.getActivation().getRule());
	}
	@Override
	public void afterActivationFired(AfterActivationFiredEvent event) {
		System.out.println("[Agenda Listener] Rule fired " + event.getActivation().getRule() );
	}

	@Override
	public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent arg0) {
		
	}

	@Override
	public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent arg0) {
		
	}

	@Override
	public void agendaGroupPopped(AgendaGroupPoppedEvent arg0) {
		
	}

	@Override
	public void agendaGroupPushed(AgendaGroupPushedEvent arg0) {
		
	}

	@Override
	public void beforeActivationFired(BeforeActivationFiredEvent arg0) {
		
	}

	@Override
	public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent arg0) {
		
	}

	@Override
	public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent arg0) {
		
	}

	
}
