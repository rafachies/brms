package org.rchies.bpm.service;

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

import org.jbpm.task.I18NText;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.Task;
import org.jbpm.task.query.TaskSummary;
import org.rchies.bpm.process.ProcessManager;
import org.rchies.bpm.process.vo.ProcessVO;
import org.rchies.bpm.process.vo.TaskVO;
import org.rchies.bpm.task.HumanTaskManager;

@Path("/task")
public class TaskWS {

	@Inject
	private HumanTaskManager humanTaskManager;
	
	@Inject
	private ProcessManager processManager;

	
	@POST
	@Path("/test/{taskId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void test(@PathParam(value = "taskId") String taskId, Map<String, String> parameters) {
		System.out.println("ahuehuaheua" + parameters);
	}
	
	
	@GET
	@Path("/username/{username}")
	public List<TaskSummary> getTasksByUser(@PathParam(value = "username") String username) {
		return humanTaskManager.getTasks(username);
	}
	
	@GET
	@Path("/{taskId}")
	public TaskVO getTasksById(@PathParam(value = "taskId") Long taskId) {
		Task task = humanTaskManager.getTask(taskId);
		TaskVO taskVO = new TaskVO();
		taskVO.setId(task.getId());
		taskVO.setStatus(task.getTaskData().getStatus().name());
		taskVO.setCreatedBy(task.getTaskData().getCreatedBy().getId());
		taskVO.setCreatedOn(task.getTaskData().getCreatedOn());
		taskVO.setCompletedOn(task.getTaskData().getCompletedOn());
		taskVO.setSessionId(task.getTaskData().getProcessSessionId());
		for (I18NText name : task.getNames()) {
			taskVO.getNames().add(name.getText());
		}
		for (OrganizationalEntity potentialOwner : task.getPeopleAssignments().getPotentialOwners()) {
			taskVO.getPotentialOwners().add(potentialOwner.getId());
		}
		for (I18NText description : task.getDescriptions()) {
			taskVO.getDescriptions().add(description.getText());
		}
		return taskVO;
	}
	
	@GET
	@Path("/{taskId}/process")
	public ProcessVO getProcessByTask(@PathParam(value = "taskId") Long taskId) {
		Task task = humanTaskManager.getTask(taskId);
		return processManager.getProcessByTask(task);
	}
	
	@GET
	@Path("/{taskId}/parameter")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> getTaskParameter(@PathParam(value = "taskId") Long taskId) {
		return humanTaskManager.getDataInput(taskId);
	}
	
	@GET
	@Path("/{taskId}/claim/{username}")
	public void claimTask(@PathParam(value = "taskId") Long taskId, @PathParam(value = "username") String username) {
		humanTaskManager.claimTask(taskId, username);
	}

	@GET
	@Path("/{taskId}/start/{username}")
	public void startTask(@PathParam(value = "taskId") Long taskId, @PathParam(value = "username") String username) {
		humanTaskManager.startTask(taskId, username);
	}

	@GET
	@Path("/{taskId}/complete/{username}")
	public void completeTask(@PathParam(value = "taskId") Long taskId, @PathParam(value = "username") String username) {
		humanTaskManager.completeTask(taskId, username, null);
	}
	
	@POST
	@Path("/{taskId}/execute/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void startAndCompleteTask(@PathParam(value = "taskId") Long taskId, @PathParam(value = "username") String username, Map<String, Object> parameters) {
		humanTaskManager.startAndCompleteTask(taskId, username, parameters);
	}
	
	@POST
	@Path("/name/{taskName}/process/{instanceId}/execute/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void startAndCompleteTaskByName(@PathParam(value = "taskName") String taskName, @PathParam(value = "InstanceId") Long processInstanceId, @PathParam(value = "username") String username, Map<String, Object> parameters) {
		Long taskId = humanTaskManager.getNextTaskByName(processInstanceId, taskName);
		humanTaskManager.startAndCompleteTask(taskId, username, parameters);
	}
	
	@GET
	@Path("/{taskId}/process/variable/{variableName}")
	public Object getProcessVariableByTask(@PathParam(value = "taskId") Long taskId, @PathParam(value = "variableName") String variableName) {
		Long processInstanceId = humanTaskManager.getProcessInstanceId(taskId);
		return processManager.getVariable(processInstanceId, variableName);
	}
	
	@GET
	@Path("/{taskId}/execute/{username}")
	public void forwardTask(@PathParam(value = "taskId") Long taskId, @PathParam(value = "username") String username) throws Exception {
		humanTaskManager.forwardTask(taskId, username);
	}
	
	@POST
	@Path("/{taskId}/owner")
	@Consumes(MediaType.APPLICATION_JSON)
	public void addPotentialOwner(@PathParam(value = "taskId") Long taskId, List<String> potentialOwners) {
		humanTaskManager.addPotentialOwner(taskId, potentialOwners);
	}
}