package org.rchies.bpm.task;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.drools.SystemEventListenerFactory;
import org.jbpm.task.AccessType;
import org.jbpm.task.Content;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.TaskData;
import org.jbpm.task.User;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.UserGroupCallbackManager;
import org.jbpm.task.service.local.LocalTaskService;
import org.rchies.bpm.process.vo.TaskVO;
import org.rchies.bpm.task.listener.CustomTaskEventListener;
import org.rchies.bpm.task.security.CustomUserCallback;
import org.rchies.bpm.util.TaskCheckService;
import org.slf4j.Logger;

@Startup
@Singleton
public class HumanTaskManager {

	private static final String DEFAULT_LANGUAGE = "pt_BR";

	@Inject
	private Logger logger;

	@Inject
	private TaskCheckService taskCheckService;
	
	@Inject
	private CustomTaskEventListener snoaTaskEventListener;

	@PersistenceUnit(unitName = "org.jbpm.task")
	private EntityManagerFactory entityManagerFactory;

	private LocalTaskService localTaskService;

	@PostConstruct
	public void postConstruct() {
		UserGroupCallbackManager callBackManager = UserGroupCallbackManager.getInstance();
		callBackManager.setCallback(new CustomUserCallback());
		TaskService taskService = new TaskService(entityManagerFactory, SystemEventListenerFactory.getSystemEventListener());
		taskService.addEventListener(snoaTaskEventListener);
		localTaskService = new LocalTaskService(taskService);
	}

	@PreDestroy
	public void preDestroy() {
		logger.info("Destroying task manager: disposing task service ...");
		if (localTaskService != null) {
			localTaskService.dispose();
		}
	}

	public List<TaskSummary> getTasks(String username, List<String> groups) throws Exception {
		if(groups != null)
			return localTaskService.getTasksAssignedAsPotentialOwner(username, groups, DEFAULT_LANGUAGE);
		return localTaskService.getTasksAssignedAsPotentialOwner(username, DEFAULT_LANGUAGE);
	}

	public List<TaskVO> getTasksByInstance(Long instanceId, List<Status> statusList) {
		List<TaskVO> result = new ArrayList<TaskVO>();
		List<TaskSummary> tasks = localTaskService.getTasksByStatusByProcessId(instanceId, statusList, DEFAULT_LANGUAGE);
		for (TaskSummary taskSummary : tasks) {
			TaskVO taskVO = new TaskVO();
			taskVO.setId(taskSummary.getId());
			taskVO.setName(taskSummary.getName());
			result.add(taskVO);
		}
		return result;
	}
	
	public void addPotentialOwner(Long taskId, List<String> potentialOwners) {
		Task task = getTask(taskId);
		for (String potentialOwner : potentialOwners) {
			task.getPeopleAssignments().getPotentialOwners().add(new User(potentialOwner));
		}
		if (hasPotentialOwner(potentialOwners)) {
			task.getTaskData().setCreatedBy(new User(potentialOwners.get(0)));
		}
	}

	public List<TaskSummary> getTasks(String username) {
		return localTaskService.getTasksAssignedAsPotentialOwner(username, "en-UK");
	}

	public Task getTask(Long taskId) {
		return localTaskService.getTask(taskId);
	}

	public void claimTask(Long taskId, String userId) {
		localTaskService.claim(taskId, userId);
	}

	public void startTask(Long taskId, String userId) {
		localTaskService.start(taskId, userId);
	}

	public void completeTask(Long taskId, String username, Map<String, Object> outputMap) {
		try {
			ContentData contentData = createOutputData(outputMap);
			localTaskService.complete(taskId, username, contentData);
		} catch (Exception err) {
			logger.error("Error while finishing human task :: " + err);
		}
	}

	public void startAndCompleteTask(Long taskId, String username, Map<String, Object> outputMap) {
		logger.debug("Starting and finishing task {} with username {}", taskId, username);
		try {
			Timestamp timestamp = new Timestamp(Calendar.getInstance().getTimeInMillis());
			boolean taskBlocked = taskCheckService.isTaskBlocked(taskId, username, timestamp);
			if(taskBlocked) {
				logger.warn("Task {} blocked for username {}", taskId, username);
				return;
			}
			localTaskService.claim(taskId, username);
			localTaskService.start(taskId, username);
			ContentData outputData = createOutputData(outputMap);
			localTaskService.complete(taskId, username, outputData);
			taskCheckService.unblockTask(taskId);
		} catch (Exception e) {
			logger.error("Error while handling task {}", taskId, e);
			taskCheckService.unblockTask(taskId);
		}
	}

	public Task getTaskByWorkItem(Long workItemId){
		return localTaskService.getTaskByWorkItemId(workItemId);
	}

	public Long getNextTaskByName(Long processInstanceId, String taskName) {
		Long taskId = null;
		List<TaskSummary> taskSummaries = localTaskService.getTasksByStatusByProcessIdByTaskName( processInstanceId, Arrays.asList( Status.Ready, Status.Reserved ), taskName, DEFAULT_LANGUAGE );
		if ( taskSummaries != null && !taskSummaries.isEmpty() ) {
			TaskSummary taskSummary = taskSummaries.get( 0 );
			taskId = taskSummary.getId();
		}
		return taskId;
	}

	public List<TaskVO> getTasksByProcessAndNameAndStatus(Long processInstanceId, String taskName, List<Status> status) {
		List<TaskVO> taskList = new ArrayList<TaskVO>();
		List<TaskSummary> taskSummaries = localTaskService.getTasksByStatusByProcessIdByTaskName( processInstanceId, status, taskName, DEFAULT_LANGUAGE );
		for (TaskSummary taskSummary : taskSummaries) {
			TaskVO taskVO = new TaskVO();
			taskVO.setId(taskSummary.getId());
			taskList.add(taskVO);
		}
		return taskList;
	}

	public Long getNextTaskByProcess(Long processInstanceId) {
		Long taskId = null;
		List<TaskSummary> taskSummaries = localTaskService.getTasksByStatusByProcessId(processInstanceId, Arrays.asList( Status.Ready, Status.Reserved ), DEFAULT_LANGUAGE );
		if ( taskSummaries != null && !taskSummaries.isEmpty() ) {
			TaskSummary taskSummary = taskSummaries.get( 0 );
			taskId = taskSummary.getId();
		}
		return taskId;
	}

	public Long getProcessInstanceId(Long taskId) {
		Task task = localTaskService.getTask(taskId);
		return task.getTaskData().getProcessInstanceId();
	}

	public void forwardTask(Long taskId, String username) throws Exception {
		logger.debug("Forwarding task {} manually", taskId);
		Task task = loadTask(taskId);
		task.getTaskData().setStatus(Status.InProgress);
		task.getTaskData().setCompletedOn(null);
		completeTask(taskId, username, null);
	}

	public LocalTaskService getLocalTaskService() {
		return localTaskService;
	}
	
	public ContentData createOutputData(Map<String, Object> outputMap) {
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(outputMap);
			objectOutputStream.close();
			ContentData contentData = new ContentData();
			contentData.setContent(byteArrayOutputStream.toByteArray());
			contentData.setAccessType(AccessType.Inline);
			return contentData;
		} catch (Exception exception) {
			logger.error("Error while creating task output data", exception);
			throw new RuntimeException(exception);
		}
	}

	@SuppressWarnings({ "unchecked"})
	public Map<String, Object> getDataInput(Long taskId) {
		try {
			Task task = loadTask(taskId);
			TaskData taskData = task.getTaskData();
			Content content = localTaskService.getContent(taskData.getDocumentContentId());
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content.getContent());
			ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
			return (Map<String, Object>) objectInputStream.readObject();
		} catch (Exception exception) {
			logger.error("Error while reading task data input of task {}", taskId, exception);
			throw new RuntimeException(exception);
		}
	}

	private Task loadTask(Long taskId) throws Exception {
		return localTaskService.getTask(taskId);
	}

	private boolean hasPotentialOwner(List<String> potentialOwners) {
		return potentialOwners != null && !potentialOwners.isEmpty();
	}

	public boolean isLegacyProcess(String taskId) {
		return true;
	}

}
