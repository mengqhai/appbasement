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
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.annotations.ForeignKey;

import com.angularjsplay.mvc.validation.ValidateOnCreate;
import com.angularjsplay.mvc.validation.ValidateOnUpdate;

@Entity
@Access(AccessType.FIELD)
public class Backlog implements IEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Access(AccessType.PROPERTY)
	private Long id;

	@NotNull(groups = ValidateOnCreate.class)
	@Size(min = 1, max = 256, groups = { ValidateOnCreate.class,
			ValidateOnUpdate.class })
	@Column(length = 256)
	private String name;

	@Size(min = 1, max = 2048, groups = { ValidateOnCreate.class,
			ValidateOnUpdate.class })
	@Column(length = 2048)
	private String desc;

	@Min(value = 1, groups = { ValidateOnCreate.class, ValidateOnUpdate.class })
	@Max(value = 10, groups = { ValidateOnCreate.class, ValidateOnUpdate.class })
	private Short priority;

	@Min(value = 1, groups = { ValidateOnCreate.class, ValidateOnUpdate.class })
	private Short estimation;

	@NotNull(groups = ValidateOnCreate.class)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "PROJECT_ID", nullable = false)
	@ForeignKey(name = "FK_PROJECT_PRODUCT_BACKLOGS")
	private Project project;

	@JsonIgnore
	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	@JsonIgnore
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "SPRINT_ID", nullable = true)
	@ForeignKey(name = "FK_SPRINT_BACKLOGS")
	private Sprint sprint;

	public Backlog() {
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

	public Short getPriority() {
		return priority;
	}

	public void setPriority(Short priority) {
		this.priority = priority;
	}

	public Short getEstimation() {
		return estimation;
	}

	public void setEstimation(Short estimation) {
		this.estimation = estimation;
	}

	@JsonProperty
	public Date getCreatedAt() {
		return createdAt;
	}

	@JsonIgnore
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
		result = prime * result + ((getPriority() == null) ? 0 : getPriority());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Backlog))
			return false;
		Backlog other = (Backlog) obj;
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
		if (getEstimation() == null) {
			if (other.getEstimation() != null)
				return false;
		} else if (!getEstimation().equals(other.getEstimation())) {
			return false;
		}

		if (getName() == null) {
			if (other.getName() != null)
				return false;
		} else if (!getName().equals(other.getName()))
			return false;

		if (getPriority() == null) {
			if (other.getPriority() != null)
				return false;
		} else if (!getPriority().equals(other.getPriority())) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return "Backlog [name=" + name + "]";
	}

	@JsonIgnore
	public Project getProject() {
		return project;
	}

	@JsonProperty
	public void setProject(Project project) {
		this.project = project;
	}

	@NotNull(groups = ValidateOnCreate.class)
	@JsonProperty
	// a read only
	public Long getProjectId() {
		if (this.project != null) {
			return this.project.getId();
		} else {
			return null;
		}
	}

	@JsonProperty
	public void setProjectId(Long projectId) {
		if (projectId != null) {
			if (this.project == null) {
				this.project = new Project();
			}
			project.setId(projectId);
		}
	}

}
