package org.rchies.bpm.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;


@Stateless
public class TaskCheckService {
	
	@PersistenceContext(unitName = "org.jbpm.persistence.jpa")
	private EntityManager entityManager;

	// CREATE SEQUENCE seq_bloqueio_task MINVALUE 1 START WITH 1 INCREMENT BY 1 CACHE 10;
	// create table bloqueio_task (id_bloqueio NUMBER(19,0) not null PRIMARY KEY, task_id NUMBER(19,0) not null, usuario varchar2(50) not null, horario_bloqueio timestamp(6) not null, hash_bloqueio varchar2(32));

	@SuppressWarnings("unchecked")
	public synchronized boolean isTaskBlocked(Long taskId, String username, Timestamp horarioSubmit) {
		System.out.println("REDHAT ["+Thread.currentThread().getId()+"] :: Verificando se a task esta bloqueada " + taskId);
		String selectSQL = new String("SELECT id_bloqueio, task_id, usuario, horario_bloqueio, hash_bloqueio FROM bloqueio_task WHERE task_id = :taskId");
		Query selectQuery = entityManager.createNativeQuery(selectSQL);
		selectQuery.setParameter("taskId", taskId);
		List<Object> resultList = selectQuery.getResultList();
		if (isTaskBlocked(resultList)) {
			System.out.println("REDHAT ["+Thread.currentThread().getId()+"] :: Task ja esta bloqueada");
			return true;
		} 
		blockTask(taskId, username, horarioSubmit);
		entityManager.close();
		return false;
	}
	
	public synchronized void unblockTask(Long taskId) {
		try {
			System.out.println("REDHAT ["+Thread.currentThread().getId()+"] :: Desbloqueando tarefa " + taskId);
			String deleteSQL = new String("DELETE FROM BLOQUEIO_TASK WHERE TASK_ID = :taskId");
			Query deleteQuery = entityManager.createNativeQuery(deleteSQL);
			deleteQuery.setParameter("taskId", taskId);
			deleteQuery.executeUpdate();
			entityManager.close();
			System.out.println("REDHAT ["+Thread.currentThread().getId()+"] :: Tarefa desbloqueada " + taskId);
		} catch (Exception e) {
			System.out.println("REDHAT ["+Thread.currentThread().getId()+"] :: Erro ao desbloquear tarefa");
			e.printStackTrace();
		}
	}

	private boolean isTaskBlocked(List<Object> resultList) {
		return resultList != null && !resultList.isEmpty();
	}

	private void blockTask(Long taskId, String username, Timestamp timestamp) {
		Calendar now = Calendar.getInstance();
		now.setTimeInMillis(timestamp.getTime());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
		StringBuffer stringBuffer = new StringBuffer("");
		stringBuffer.append("INSERT INTO BLOQUEIO_TASK (ID_BLOQUEIO, TASK_ID, USUARIO, HORARIO_BLOQUEIO) VALUES (SEQ_BLOQUEIO_TASK.nextval, '");
		stringBuffer.append(taskId);
		stringBuffer.append("', '");
		stringBuffer.append(username);
		stringBuffer.append("', TO_TIMESTAMP('");
		stringBuffer.append(simpleDateFormat.format(now.getTime()));
		stringBuffer.append("', 'yyyy/mm/dd hh24:mi:ss:FF3'))");
		Query createNativeQuery = entityManager.createNativeQuery(stringBuffer.toString());
		createNativeQuery.executeUpdate();
		System.out.println("REDHAT ["+Thread.currentThread().getId()+"] :: Bloqueando task " + taskId + " para usuario " + username);
	}
}