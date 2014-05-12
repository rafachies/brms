package org.rchies.bpm.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.drools.definition.process.Node;
import org.drools.definition.process.Process;
import org.drools.runtime.StatefulKnowledgeSession;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.task.Status;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.rchies.bpm.process.ProcessManager;
import org.rchies.bpm.process.vo.ProcessVO;
import org.rchies.bpm.process.vo.TaskVO;
import org.rchies.bpm.task.HumanTaskManager;

@Path("/process")
public class ProcessWS {

	@Inject
	private ProcessManager processManager;
	
	@Inject
	private HumanTaskManager humanTaskManager;
	
	@Inject
	private StatefulKnowledgeSession session;

	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<ProcessVO> getProcesses() {
		List<ProcessVO> result = new ArrayList<ProcessVO>();
		Collection<Process> processes = session.getKnowledgeBase().getProcesses();
		for (Process process : processes) {
			ProcessVO processVO = new ProcessVO();
			processVO.setId(process.getId());
			processVO.setName(process.getName());
			result.add(processVO);
		}
		return result;
	}
	
	@GET
	@Path("/{processId}")
	@Produces(MediaType.APPLICATION_JSON)
	public ProcessVO getProcess(@PathParam("processId") String processId) {
		return processManager.getProcess(processId);
	}
	
	@GET
	@Path("/{instanceId}")
	@Produces(MediaType.APPLICATION_JSON)
	public ProcessVO getParentProcess(@PathParam("instanceId") Long instanceId) {
		return processManager.getParentProcess(instanceId);
	}
	
	@GET
	@Path("/instance/{instanceId}/task")
	@Produces(MediaType.APPLICATION_JSON)
	public List<TaskVO> getTasksByProcess(@PathParam("instanceId") Long instanceId) {
		return humanTaskManager.getTasksByInstance(instanceId, Arrays.asList(Status.values()));
	}
	
	@GET
	@Path("/instance/{instanceId}/task/alive")
	@Produces(MediaType.APPLICATION_JSON)
	public List<TaskVO> getAliveTasksByProcess(@PathParam("instanceId") Long instanceId) {
		ArrayList<Status> statusList = new ArrayList<Status>();
		statusList.add(Status.Reserved);
		statusList.add(Status.Ready);
		return humanTaskManager.getTasksByInstance(instanceId, statusList);
	}
	
	@GET
	@Path("/instance/{instanceId}/task/ready")
	@Produces(MediaType.APPLICATION_JSON)
	public List<TaskVO> getReadyTasksByProcess(@PathParam("instanceId") Long instanceId) {
		ArrayList<Status> statusList = new ArrayList<Status>();
		statusList.add(Status.Ready);
		return humanTaskManager.getTasksByInstance(instanceId, statusList);
	}
	
	@GET
	@Path("/instance/{instanceId}/task/status/{status}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<TaskVO> getTasksByProcessAndStatus(@PathParam("instanceId") Long instanceId, @PathParam("status") String status) {
		ArrayList<Status> statusList = new ArrayList<Status>();
		statusList.add(Status.valueOf(status));
		return humanTaskManager.getTasksByInstance(instanceId, statusList);
	}
	
	@GET
	@Path("/instance/{instanceId}/task/name/{taskName}/status/{status}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<TaskVO> getTasksByProcessAndTaskNameAndStatus(@PathParam("instanceId") Long instanceId, @PathParam("taskName") String taskName, @PathParam("status") String status) {
		ArrayList<Status> statusList = new ArrayList<Status>();
		statusList.add(Status.valueOf(status));
		return humanTaskManager.getTasksByProcessAndNameAndStatus(instanceId, taskName, statusList);
	}
	
	@GET
	@Path("/instance/{instanceId}/variable/{variableName}")
	public Object getProcessVariable(@PathParam("instanceId") Long instanceId, @PathParam("variableName") String variableName) {
		return processManager.getVariable(instanceId, variableName);
	}
	
	@GET
	@Path("/instance/{instanceId}/variable")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> getProcessVariables(@PathParam("instanceId") Long instanceId) {
		return processManager.getVariables(instanceId);
	}
	
	@POST
	@Path("/{processId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Long startProcess(@PathParam("processId") String processId, Map<String, Object> parameters) {
		return processManager.startProcess(processId, parameters);
	}
	
//	@POST
//	@Path("/instance/{instanceId}/signal/{signalId}")
//	public void signal(@PathParam("instanceId") Long instanceId, @PathParam("signalId") String signalId, @FormParam("signalData") Serializable signalData) {
//		processManager.signal(instanceId, signalId, signalData);
//	}
	
	@POST
	@Path("/signal/{signalId}/data/{signalData}")
	public void signalAll(@PathParam("signalId") String signalId, @PathParam("signalData") String signalData) {
		processManager.signal(null, signalId, signalData);
	}
	
	@POST
	@Path("/instance/{instanceId}/execute/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void startAndCompleteTaskByProcessAndStatus(@PathParam(value = "instanceId") Long processInstanceId, @PathParam(value = "username") String username, Map<String, Object> parameters) {
		Long taskId = humanTaskManager.getNextTaskByProcess(processInstanceId);
		humanTaskManager.startAndCompleteTask(taskId, username, parameters);
	}

	@GET
	@Path("/{processName}/humantask/name")
	public List<String> getNodesNames(@PathParam("processName") String processName) {
		List<String> result = new ArrayList<String>();
		Process process = session.getKnowledgeBase().getProcess(processName);
		Node[] nodes = ((RuleFlowProcess) process).getNodes();
		for (Node node : nodes) {
			if (node instanceof HumanTaskNode ) {
				result.add(node.getName().trim());
			}
		}
		return result;
	}
	
	@GET
	@Path("/name")
	public List<String> getNames() {
		List<String> result = new ArrayList<String>();
		Collection<Process> processes = session.getKnowledgeBase().getProcesses();
		for (Process process : processes) {
			result.add(process.getName());
		}
		return result;
	}

}

