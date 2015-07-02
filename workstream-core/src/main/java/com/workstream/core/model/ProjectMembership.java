package com.workstream.core.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
@Table(name = "WS_PROJECT_MEM")
@Access(AccessType.FIELD)
public class ProjectMembership implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5867115344554201942L;

	public enum ProjectMembershipType {
		ADMIN, PARTICIPANT, GUEST
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Access(AccessType.PROPERTY)
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "ORG_ID", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PROJECT_MEM_ORG"))
	private Organization org;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "PRO_ID", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PROJECT_MEM_PRO"))
	private Project project;

	@Column(nullable = false, updatable = false)
	private String userId;

	@Enumerated(EnumType.ORDINAL)
	private ProjectMembershipType type;

	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Organization getOrg() {
		return org;
	}

	public void setOrg(Organization org) {
		this.org = org;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public ProjectMembershipType getType() {
		return type;
	}

	public void setType(ProjectMembershipType type) {
		this.type = type;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	@PrePersist
	protected void setCreatedAt() {
		if (getCreatedAt() == null) {
			setCreatedAt(new Date());
		}
	}

}
