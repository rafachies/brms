package org.rchies.bpm.process;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.TransactionManager;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceChangeScannerConfiguration;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ClassPathResource;
import org.drools.persistence.info.SessionInfo;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.jbpm.process.audit.JPAWorkingMemoryDbLogger;
import org.jbpm.process.workitem.wsht.SyncWSHumanTaskHandler;
import org.rchies.bpm.dao.SessionInfoDAO;
import org.rchies.bpm.process.listener.SnoaProcessEventListener;
import org.rchies.bpm.task.HumanTaskManager;
import org.rchies.bpm.util.SystemPropertyUtil;
import org.slf4j.Logger;

@Startup
@Singleton
public class SessionManager {


	@Inject
	private Logger logger;

	@PersistenceUnit(unitName = "org.jbpm.persistence.jpa")
	private EntityManagerFactory entityManagerFactory;

	@Resource(mappedName="java:/TransactionManager")
	private TransactionManager transactionManager;

	@Inject
	private HumanTaskManager humanTaskManager;

	@Inject
	private SnoaProcessEventListener snoaProcessEventListener;
	
	@Inject
	private SystemPropertyUtil systemPropertyUtil;
	
	@Inject
	private SessionInfoDAO sessionInfoDAO;

	private StatefulKnowledgeSession session;

	@PostConstruct
	public void construct() {
		logger.info("Starting session manager: Building the KnowledgeBase and configuring a KnowledgeSession ...");
		KnowledgeBase knowledgeBase = createKnowledgeBase();
		createSession(knowledgeBase);
		configureSession();
	}

	@PreDestroy
	public void preDestroy() {
		logger.info("Destroying session manager: disposing session ...");
		if (session != null) {
			session.dispose();
		}
	}

	@Produces
	public StatefulKnowledgeSession getSession() {
		return session;
	}


	private void configureSession() {
		SyncWSHumanTaskHandler humanTaskHandler = new SyncWSHumanTaskHandler(humanTaskManager.getLocalTaskService(), session);
		humanTaskHandler.connect();
		session.getWorkItemManager().registerWorkItemHandler("Human Task", humanTaskHandler);
		session.addEventListener(snoaProcessEventListener);
	}


	private void createSession(KnowledgeBase knowledgeBase) {
		Environment environment = KnowledgeBaseFactory.newEnvironment();
		environment.set(EnvironmentName.ENTITY_MANAGER_FACTORY, entityManagerFactory);
		environment.set(EnvironmentName.TRANSACTION_MANAGER, transactionManager);
		Properties sessionconfigproperties = new Properties();
		sessionconfigproperties.put("drools.processInstanceManagerFactory", "org.jbpm.persistence.processinstance.JPAProcessInstanceManagerFactory");
		sessionconfigproperties.put("drools.processSignalManagerFactory", "org.jbpm.persistence.processinstance.JPASignalManagerFactory");
		KnowledgeSessionConfiguration configuration = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(sessionconfigproperties);
		Integer sessionId = systemPropertyUtil.getInteger(SystemPropertyUtil.SNOA_BPM_SESSION_ID);
		SessionInfo sessionInfo = sessionInfoDAO.findById(sessionId);
		if (isNewSessionId(sessionInfo)) {
			logger.info("There is no persisted session with id {}, we're going to create it", sessionId);
			session = JPAKnowledgeService.newStatefulKnowledgeSession(knowledgeBase, configuration, environment);
		} else {
			logger.info("Session {} already existis, loading it ...", sessionId);
			session = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId, knowledgeBase, configuration, environment);
		}
		new JPAWorkingMemoryDbLogger(session);
	}

	private boolean isNewSessionId(SessionInfo sessionInfo) {
		return sessionInfo != null;
	}

	private KnowledgeBase createKnowledgeBase() {
		KnowledgeBuilder knowledgeBuilder = createKnowledgeBuilder();		
		KnowledgeBase knowledgeBase = knowledgeBuilder.newKnowledgeBase();
		startScannerService();
		return knowledgeBase;
	}

	private KnowledgeBuilder createKnowledgeBuilder() {
		KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		knowledgeBuilder.add(new ClassPathResource("processes/teste.bpmn2"), ResourceType.BPMN2);
		return knowledgeBuilder;
	}

	private void startScannerService() {
		ResourceChangeScannerConfiguration configuration = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
		configuration.setProperty("drools.resource.scanner.interval", "30");
		configuration.setProperty("drools.agent.newInstance", "false");
		ResourceFactory.getResourceChangeScannerService().configure(configuration);
		ResourceFactory.getResourceChangeNotifierService().start();
		ResourceFactory.getResourceChangeScannerService().start();
	}
}