package com.angularjsplay.model;

import java.util.ArrayList;
import java.util.Collection;
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
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.ForeignKey;

@Entity
@Access(AccessType.FIELD)
public class Sprint implements IEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Access(AccessType.PROPERTY)
	private Long id;

	@Column(length = 512)
	private String name;

	@Column(length = 2048)
	private String desc;

	private Short capacity;

	@Temporal(TemporalType.TIMESTAMP)
	private Date startAt;

	@Temporal(TemporalType.TIMESTAMP)
	private Date endAt;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false, updatable = false)
	private Date createdAt;

	@JsonIgnore
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "PROJECT_ID", nullable = false)
	@ForeignKey(name = "FK_PROJECT_PRODUCT_SPRINT")
	private Project project;

	/**
	 * bag The Sprint backlog is a deliverable created as a subset of the
	 * Product Backlog. The Sprint backlog is a to-do list of backlog items to
	 * be completed in the current iteration.
	 */
	@JsonIgnore
	@OneToMany(mappedBy = "sprint", fetch = FetchType.LAZY)
	private Collection<Backlog> backlogs = new ArrayList<Backlog>();

	public Sprint() {
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

	public short getCapacity() {
		return capacity;
	}

	public void setCapacity(short capacity) {
		this.capacity = capacity;
	}

	public Date getStartAt() {
		return startAt;
	}

	public void setStartAt(Date startAt) {
		this.startAt = startAt;
	}

	public Date getEndAt() {
		return endAt;
	}

	public void setEndAt(Date endAt) {
		this.endAt = endAt;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Collection<Backlog> getBacklogs() {
		return backlogs;
	}

	public void setBacklogs(Collection<Backlog> backlogs) {
		this.backlogs = backlogs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getCapacity();
		result = prime * result
				+ ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
		result = prime * result
				+ ((getDesc() == null) ? 0 : getDesc().hashCode());
		result = prime * result
				+ ((getEndAt() == null) ? 0 : getEndAt().hashCode());
		result = prime * result
				+ ((getName() == null) ? 0 : getName().hashCode());
		result = prime * result
				+ ((getStartAt() == null) ? 0 : getStartAt().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Sprint))
			return false;
		Sprint other = (Sprint) obj;
		if (getCapacity() != other.getCapacity())
			return false;
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
		if (getEndAt() == null) {
			if (other.getEndAt() != null)
				return false;
		} else if (!getEndAt().equals(other.getEndAt()))
			return false;
		if (getName() == null) {
			if (other.getName() != null)
				return false;
		} else if (!getName().equals(other.getName()))
			return false;
		if (getStartAt() == null) {
			if (other.getStartAt() != null)
				return false;
		} else if (!getStartAt().equals(other.getStartAt()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Sprint [name=" + name + "]";
	}

}
