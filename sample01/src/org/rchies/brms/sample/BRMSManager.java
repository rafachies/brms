package org.rchies.brms.sample;

import java.util.Arrays;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentConfiguration;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.io.ResourceChangeScannerConfiguration;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;
import org.rchies.brms.sample.listener.MyAgendaListener;
import org.rchies.brms.sample.listener.MySystemEventListener;
import org.rchies.brms.sample.listener.MyWorkingMemoryListener;


public class BRMSManager {

	private KnowledgeBase knowledgeBase;
	private KnowledgeAgent knowledgeAgent;
	private MyAgendaListener agendaListener;
	private StatefulKnowledgeSession statefulSession;
	private MyWorkingMemoryListener workingMemoryListener;

	
	/*
	 * Method signature to evaluate facts with a StatelessSession
	 * A new session is created every time, since it's a light task
	 */
	public void execute(Object ... facts) {
		StatelessKnowledgeSession session = knowledgeBase.newStatelessKnowledgeSession();
		session.addEventListener(agendaListener);

		session.addEventListener(workingMemoryListener);

		List<Object> factList = Arrays.asList(facts);
		session.execute(factList);
	}

	/*
	 * Method signature to evaluate facts with a singleton StatefulSession
	 * As it works with StatefulSession, remember to retract facts in the rules, or even in Java code
	 */
	public void insertAndFire(Object ... facts) {
		for (Object fact : facts) {
			statefulSession.insert(fact);
		}
		statefulSession.fireAllRules();
	}


	private static BRMSManager instance;

	private BRMSManager() {
		createSessionListeners();
		startScanners();
		KnowledgeAgent knowledgeAgent = createKnowledgeAgent();
		knowledgeBase = knowledgeAgent.getKnowledgeBase();
		createSingletonStatefulSession();
	}

	private void createSingletonStatefulSession() {
		statefulSession = knowledgeBase.newStatefulKnowledgeSession();
		statefulSession.addEventListener(agendaListener);
		statefulSession.addEventListener(workingMemoryListener);
	}

	private void createSessionListeners() {
		agendaListener = new MyAgendaListener();
		workingMemoryListener = new MyWorkingMemoryListener();
	}

	private KnowledgeAgent createKnowledgeAgent() {
		System.setProperty("drools.resource.urlcache", "/home/rafael/cache");
		KnowledgeAgentConfiguration agentConfiguration = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
		agentConfiguration.setProperty("drools.agent.newInstance", "false");
		knowledgeAgent = KnowledgeAgentFactory.newKnowledgeAgent("agent", agentConfiguration);
		knowledgeAgent.setSystemEventListener(new MySystemEventListener());
		knowledgeAgent.applyChangeSet(new ClassPathResource("changeset.xml"));
		return knowledgeAgent;
	}

	private void startScanners() {
		ResourceChangeScannerConfiguration scannerConfiguration = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
		scannerConfiguration.setProperty( "drools.resource.scanner.interval", "30" ); 
		ResourceFactory.getResourceChangeScannerService().configure(scannerConfiguration);
		ResourceFactory.getResourceChangeNotifierService().start();
		ResourceFactory.getResourceChangeScannerService().start();
	}

	public static synchronized BRMSManager getInstance() {
		if (instance == null) {
			instance = new BRMSManager();
		}
		return instance;
	}


	@SuppressWarnings("unused")
	private void tipsAndTricks() {
		//TODO: The line below create a log that can be used in audit feature of JBoss Developer Studio
		//KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(session, "log file nam");
		//TODO: The line below can optimize performance when using StatelessSession
		//System.setProperty("drools.sequential", "true");
		//TODO: Get the ChangeSet from file system, and let each environment with their own configuration (Guvnor URL and user/pwd)
		//knowledgeBuilder.add(new FileSystemResource("changeset.xml"), ResourceType.CHANGE_SET);
		//TODO: We need to set TrustStore when connecting with Guvnor using SSL
		//TODO: set the property drools.resource.urlcache with server start, using -D
	}
	


}
