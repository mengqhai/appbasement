package com.angularjsplay.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Access(AccessType.FIELD)
public class Sprint {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Access(AccessType.PROPERTY)
	private long id;

	private String name;

	private String desc;

	private short capacity;

	@Temporal(TemporalType.TIMESTAMP)
	private Date startAt;

	@Temporal(TemporalType.TIMESTAMP)
	private Date endAt;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false, updatable = false)
	private Date createdAt;

	/**
	 * bag The Sprint backlog is a deliverable created as a subset of the
	 * Product Backlog. The Sprint backlog is a to-do list of backlog items to
	 * be completed in the current iteration.
	 */
	@OneToMany
	@JoinColumn(name = "SPRINT_ID", nullable= true)
	private Collection<Backlog> backlogs = new ArrayList<Backlog>();

	public Sprint() {
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

	protected short getCapacity() {
		return capacity;
	}

	protected void setCapacity(short capacity) {
		this.capacity = capacity;
	}

	protected Date getStartAt() {
		return startAt;
	}

	protected void setStartAt(Date startAt) {
		this.startAt = startAt;
	}

	protected Date getEndAt() {
		return endAt;
	}

	protected void setEndAt(Date endAt) {
		this.endAt = endAt;
	}

	protected Date getCreatedAt() {
		return createdAt;
	}

	protected void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	protected Collection<Backlog> getBacklogs() {
		return backlogs;
	}

	protected void setBacklogs(Collection<Backlog> backlogs) {
		this.backlogs = backlogs;
	}

}
