package org.rchies.brms.sample;

import java.util.UUID;

import org.junit.Test;
import org.rchies.brms.sample.model.Transacao;


public class SessionPerformanceTest {

	@Test
	public void testWithMultipleStatelessSession() throws Exception {
		Transacao txMaster = new Transacao();
		txMaster.setBandeira("master");
		txMaster.setTipo(UUID.randomUUID().toString());
		BRMSManager droolsManager = BRMSManager.getInstance();
		droolsManager.execute(txMaster);
	}
	
	@Test
	public void testWithSingletonStatefulSession() throws Exception {
		Transacao txMaster = new Transacao();
		txMaster.setBandeira("master");
		txMaster.setTipo(UUID.randomUUID().toString());
		BRMSManager droolsManager = BRMSManager.getInstance();
		droolsManager.insertAndFire(txMaster);
	}

}
