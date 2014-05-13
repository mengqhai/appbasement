package com.angularjsplay.model;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.ForeignKey;

import com.angularjsplay.mvc.validation.ValidateOnCreate;
import com.angularjsplay.mvc.validation.ValidateOnUpdate;
import com.appbasement.component.PatchableIdRef;
import com.appbasement.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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

	@NotNull(groups = ValidateOnCreate.class)
	@Size(min = 1, max = 256, groups = { ValidateOnCreate.class,
			ValidateOnUpdate.class })
	@Column(length = 255)
	private String name;

	@Size(min = 1, max = 2048, groups = { ValidateOnCreate.class,
			ValidateOnUpdate.class })
	@Column(length = 2048)
	private String desc;

	@PatchableIdRef(setter = "setBacklog", setterHost = Task.class)
	@JsonIgnore
	@ManyToOne(targetEntity = Backlog.class, optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "BACKLOG_ID", nullable = true)
	@ForeignKey(name = "FK_TASK_BACKLOG")
	private Backlog backlog;

	@Min(value = 1, groups = { ValidateOnCreate.class, ValidateOnUpdate.class })
	private Short estimation;

	@Min(value = 0, groups = { ValidateOnCreate.class, ValidateOnUpdate.class })
	private Short remaining;

	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	@Column(nullable = false)
	private TaskState state = TaskState.NEW;

	@PatchableIdRef(setter = "setOwner", setterHost = Task.class)
	@JsonIgnore
	@ManyToOne(targetEntity = User.class, optional = true, fetch = FetchType.LAZY)
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

	public Short getEstimation() {
		return estimation;
	}

	public void setEstimation(Short estimation) {
		this.estimation = estimation;
	}

	public Short getRemaining() {
		return remaining;
	}

	public void setRemaining(Short remaining) {
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
		result = prime * result
				+ ((getEstimation() == null) ? 0 : getEstimation().hashCode());
		result = prime * result
				+ ((getName() == null) ? 0 : getName().hashCode());
		result = prime * result
				+ ((getRemaining() == null) ? 0 : getRemaining().hashCode());
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
		} else if (getCreatedAt().getTime() != other.getCreatedAt().getTime())
			return false;
		if (getDesc() == null) {
			if (other.getDesc() != null)
				return false;
		} else if (!getDesc().equals(other.getDesc()))
			return false;
		if (getEstimation() == null) {
			if (other.getEstimation() != null)
				return false;
		} else if (!getEstimation().equals(other.getEstimation()))
			return false;
		if (getName() == null) {
			if (other.getName() != null)
				return false;
		} else if (!getName().equals(other.getName()))
			return false;
		if (getRemaining() == null) {
			if (other.getRemaining() != null)
				return false;
		} else if (!getRemaining().equals(other.getRemaining()))
			return false;
		if (getState() != other.getState())
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Task [name=" + name + ", state=" + state + "]";
	}

	@JsonProperty
	public Long getOwnerId() {
		if (owner != null) {
			return owner.getId();
		} else {
			return null;
		}
	}

	@JsonProperty
	public void setOwnerId(Long id) {
		if (id != null) {
			if (owner == null) {
				owner = new User();
			}
			owner.setId(id);
		} else {
			owner = null;
		}
	}

	@JsonProperty
	public Long getBacklogId() {
		if (backlog != null) {
			return backlog.getId();
		} else {
			return null;
		}
	}

	@JsonProperty
	public void setBacklogId(Long backlogId) {
		if (backlogId != null) {
			if (backlog == null) {
				backlog = new Backlog();
			}
			backlog.setId(backlogId);
		} else {
			backlog = null;
		}
	}

}
