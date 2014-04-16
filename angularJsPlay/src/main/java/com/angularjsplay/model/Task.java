package com.angularjsplay.model;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.appbasement.model.User;

@Entity
@Access(AccessType.FIELD)
public class Task {

	public enum TaskState {
		NEW, IN_PROGRESS, FINISHED, CANCELED
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Access(AccessType.PROPERTY)
	private long id;

	@Column(length = 255)
	private String name;

	@Column(length = 2048)
	private String desc;

	@ManyToOne(targetEntity = Backlog.class)
	@JoinColumn(name = "BACKLOG_ID", nullable = false)
	private Backlog backlog;

	private short estimation;

	private short remaining;

	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	@Column(nullable = false)
	private TaskState state = TaskState.NEW;

	@ManyToOne(targetEntity = User.class)
	@JoinColumn(name = "OWNER_ID", nullable = true)
	private User owner;

	public Task() {
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

	protected Backlog getBacklog() {
		return backlog;
	}

	protected void setBacklog(Backlog backlog) {
		this.backlog = backlog;
	}

	protected short getEstimation() {
		return estimation;
	}

	protected void setEstimation(short estimation) {
		this.estimation = estimation;
	}

	protected short getRemaining() {
		return remaining;
	}

	protected void setRemaining(short remaining) {
		this.remaining = remaining;
	}

	protected TaskState getState() {
		return state;
	}

	protected void setState(TaskState state) {
		this.state = state;
	}

	protected User getOwner() {
		return owner;
	}

	protected void setOwner(User owner) {
		this.owner = owner;
	}

	protected Date getCreatedAt() {
		return createdAt;
	}

	protected void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
}
