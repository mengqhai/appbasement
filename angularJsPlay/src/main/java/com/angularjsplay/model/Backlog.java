package com.angularjsplay.model;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Access(AccessType.FIELD)
public class Backlog {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Access(AccessType.PROPERTY)
	private long id;

	@Column(length = 255)
	private String name;

	@Column(length = 2048)
	private String desc;

	private short priority;

	private short estimation;

	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	public Backlog() {
	}

	protected long getId() {
		return id;
	}

	protected void setId(long id) {
		this.id = id;
	}

	protected String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	protected String getDesc() {
		return desc;
	}

	protected void setDesc(String desc) {
		this.desc = desc;
	}

	protected short getPriority() {
		return priority;
	}

	protected void setPriority(short priority) {
		this.priority = priority;
	}

	protected short getEstimation() {
		return estimation;
	}

	protected void setEstimation(short estimation) {
		this.estimation = estimation;
	}

	protected Date getCreatedAt() {
		return createdAt;
	}

	protected void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	
}
