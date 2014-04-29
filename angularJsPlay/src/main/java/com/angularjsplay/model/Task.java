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
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;

import com.appbasement.model.User;

@Entity
@Access(AccessType.FIELD)
public class Task implements IEntity {

	public enum TaskState {
		NEW, IN_PROGRESS, FINISHED, CANCELED
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Access(AccessType.PROPERTY)
	private Long id;

	@Column(length = 255)
	private String name;

	@Column(length = 2048)
	private String desc;

	@ManyToOne(targetEntity = Backlog.class)
	@JoinColumn(name = "BACKLOG_ID", nullable = true)
	@ForeignKey(name = "FK_TASK_BACKLOG")
	private Backlog backlog;

	private Short estimation;

	private Short remaining;

	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	@Column(nullable = false)
	private TaskState state = TaskState.NEW;

	@ManyToOne(targetEntity = User.class)
	@JoinColumn(name = "OWNER_ID", nullable = true)
	@ForeignKey(name = "FK_TASK_OWNER")
	private User owner;

	public Task() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Backlog getBacklog() {
		return backlog;
	}

	public void setBacklog(Backlog backlog) {
		this.backlog = backlog;
	}

	public short getEstimation() {
		return estimation;
	}

	public void setEstimation(short estimation) {
		this.estimation = estimation;
	}

	public short getRemaining() {
		return remaining;
	}

	public void setRemaining(short remaining) {
		this.remaining = remaining;
	}

	public TaskState getState() {
		return state;
	}

	public void setState(TaskState state) {
		this.state = state;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	@PrePersist
	protected void setCreatedAt() {
		if (createdAt == null) {
			createdAt = new Date();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
		result = prime * result
				+ ((getDesc() == null) ? 0 : getDesc().hashCode());
		result = prime * result + getEstimation();
		result = prime * result
				+ ((getName() == null) ? 0 : getName().hashCode());
		result = prime * result + getRemaining();
		result = prime * result
				+ ((getState() == null) ? 0 : getState().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Task))
			return false;
		Task other = (Task) obj;
		if (getCreatedAt() == null) {
			if (other.getCreatedAt() != null)
				return false;
		} else if (!getCreatedAt().equals(other.getCreatedAt()))
			return false;
		if (getDesc() == null) {
			if (other.getDesc() != null)
				return false;
		} else if (!getDesc().equals(other.getDesc()))
			return false;
		if (getEstimation() != other.getEstimation())
			return false;
		if (getName() == null) {
			if (other.getName() != null)
				return false;
		} else if (!getName().equals(other.getName()))
			return false;
		if (getRemaining() != other.getRemaining())
			return false;
		if (getState() != other.getState())
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Task [name=" + name + ", state=" + state + "]";
	}

}
