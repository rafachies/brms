package org.rchies.bpm.process.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskVO {

	private Long id;
	private String name;
	private String status;
	private String createdBy;
	private String owner;
	private Date createdOn;
	private Date completedOn;
	private Integer sessionId;
	private List<String> names = new ArrayList<String>();
	private List<String> descriptions = new ArrayList<String>();
	private List<String> potentialOwners = new ArrayList<String>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<String> getNames() {
		return names;
	}

	public void setNames(List<String> names) {
		this.names = names;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(List<String> descriptions) {
		this.descriptions = descriptions;
	}

	public List<String> getPotentialOwners() {
		return potentialOwners;
	}

	public void setPotentialOwners(List<String> potentialOwners) {
		this.potentialOwners = potentialOwners;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getCompletedOn() {
		return completedOn;
	}

	public void setCompletedOn(Date completedOn) {
		this.completedOn = completedOn;
	}

	public Integer getSessionId() {
		return sessionId;
	}

	public void setSessionId(Integer sessionId) {
		this.sessionId = sessionId;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
}