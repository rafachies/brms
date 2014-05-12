package org.rchies.bpm.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.drools.persistence.info.SessionInfo;

@Stateless
public class SessionInfoDAO {
	
	@PersistenceContext(unitName = "org.jbpm.persistence.jpa")
	private EntityManager entityManager;
	

	public SessionInfo findById(Integer id) {
		return entityManager.find(SessionInfo.class, id);
	}
}
