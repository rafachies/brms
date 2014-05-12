package org.rchies.bpm.process;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.ejb.Singleton;
import javax.inject.Inject;

import org.drools.definition.KnowledgePackage;
import org.drools.definition.process.Process;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkflowProcessInstance;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.jbpm.task.Task;
import org.rchies.bpm.process.vo.ProcessVO;
import org.slf4j.Logger;

@Singleton
public class ProcessManager {

	@Inject
	private Logger logger;
	
	@Inject
	private StatefulKnowledgeSession session;
	
	public Long startProcess(String processId, Map<String, Object> parameters) {
		logger.debug("Starting a new process with id {}", processId);
		ProcessInstance processInstance = session.createProcessInstance(processId, parameters);
		session.startProcessInstance(processInstance.getId());
		return processInstance.getId();
	}
	
	public void signal(Long instanceId, String signalId, Object signalData) {
		logger.debug("Sending signal {} to process instance {}", signalId, instanceId);
		if (forSpecificInstance(instanceId)) {
			ProcessInstance processInstance = session.getProcessInstance(instanceId);
			processInstance.signalEvent(signalId, signalData);
		} else {
			session.signalEvent(signalId, signalData);
		}
	}
	
	public List<String> getProcesses() {
		List<String> result = new ArrayList<String>();
		Collection<KnowledgePackage> packages = session.getKnowledgeBase().getKnowledgePackages();
		for (KnowledgePackage knowledgePackage : packages) {
			Collection<Process> processes = knowledgePackage.getProcesses();
			for (Process process : processes) {
				result.add(process.getId());
			}
		}
		return result;
	}
	
	public ProcessVO getProcess(String processId) {
		Process process = session.getKnowledgeBase().getProcess(processId);
		ProcessVO processVO = new ProcessVO();
		processVO.setId(processId);
		processVO.setName(process.getName());
		return processVO;
		
	}
	
	public ProcessVO getProcessByTask(Task task) {
		ProcessInstance processInstance = session.getProcessInstance(task.getTaskData().getProcessInstanceId());
		ProcessVO processVO = new ProcessVO();
		processVO.setInstanceId(processInstance.getId());
		processVO.setId(processInstance.getProcessId());
		processVO.setName(processInstance.getProcessName());
		return processVO;
		
	}
	
	public ProcessVO getParentProcess(Long processInstanceId) {
		ProcessVO processVO = new ProcessVO();
		WorkflowProcessInstance processInstance = (WorkflowProcessInstance) session.getProcessInstance(processInstanceId);
		Long parentProcessInstanceId = (Long) processInstance.getProcess().getMetaData().get("ParentProcessInstanceId");
		if (forSpecificInstance(parentProcessInstanceId)) {
			ProcessInstance parentProcess = session.getProcessInstance(parentProcessInstanceId);
			processVO.setInstanceId(parentProcess.getId());
			processVO.setName(parentProcess.getProcessName());
			processVO.setId(parentProcess.getProcessId());
		}
		return processVO;
	}

	public Object getVariable(Long processInstanceId, String variableName) {
		WorkflowProcessInstance processInstance = (WorkflowProcessInstance) session.getProcessInstance(processInstanceId);
		return processInstance.getVariable(variableName);
	}
	
	public Map<String, Object> getVariables(Long processInstanceId) {
		ProcessInstance processInstance = (ProcessInstance) session.getProcessInstance(processInstanceId);  
		Map<String, Object> variables = ((VariableScopeInstance) ((ProcessInstanceImpl) processInstance).getContextInstance(VariableScope.VARIABLE_SCOPE)).getVariables();
        return variables;  
	}
	
	private boolean forSpecificInstance(Long instanceId) {
		return instanceId != null;
	}

	public boolean isLegacyProcess(String processInstanceId) {
		// TODO Auto-generated method stub
		return false;
	}
}
