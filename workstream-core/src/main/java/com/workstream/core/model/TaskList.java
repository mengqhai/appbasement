package com.workstream.core.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "WS_TASK_LIST")
@Access(AccessType.FIELD)
public class TaskList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8434070969673769336L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Access(AccessType.PROPERTY)
	private Long id;

	@Column(nullable = false, unique = false, length = 50)
	private String name;

	@Column(nullable = true, unique = false, length = 500)
	private String description;

	@Temporal(TemporalType.TIMESTAMP)
	private Date startTime;

	@Temporal(TemporalType.TIMESTAMP)
	private Date dueTime;

	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "ORG_ID", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_TASK_LIST_ORG"))
	private Organization org;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "PRO_ID", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_TASK_LIST_PRO"))
	private Project project;

	@Column(nullable = false)
	private boolean archived;

	@ElementCollection
	@CollectionTable(name = "WS_TASK_LIST_TASK_IDS", joinColumns = @JoinColumn(name = "TASK_LIST_ID"))
	@Column(name = "TASK_ID")
	private Set<String> taskIds = new HashSet<String>();

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getDueTime() {
		return dueTime;
	}

	public void setDueTime(Date dueTime) {
		this.dueTime = dueTime;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Organization getOrg() {
		return org;
	}

	public void setOrg(Organization org) {
		this.org = org;
	}

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Set<String> getTaskIds() {
		return taskIds;
	}

	@PrePersist
	protected void setCreatedAt() {
		if (getCreatedAt() == null) {
			setCreatedAt(new Date());
		}
	}

}
