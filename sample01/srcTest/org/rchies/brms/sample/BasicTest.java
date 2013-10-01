package org.rchies.brms.sample;

import org.drools.RuleBaseConfiguration;
import org.junit.Test;
import org.rchies.brms.sample.BRMSManager;
import org.rchies.brms.sample.model.Transacao;


public class BasicTest {

	@Test
	public void testMultipleExecute() throws Exception {
//		System.setProperty("drools.mbeans", "enabled");
//		System.setProperty("drools.sequential", "true");
		RuleBaseConfiguration.getDefaultInstance().setSequential(true);
		BRMSManager droolsManager = BRMSManager.getInstance();
		Transacao transacao = new Transacao();
		transacao.setBandeira("master");
		droolsManager.execute(transacao);
		System.out.println("FINISHED");
		
	}

}
