package com.workstream.core.model;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "WS_REVISION")
@Access(AccessType.FIELD)
public class Revision {

	public static final String ACTION_EDIT = "Edit";

	public static final String TYPE_CREATE = "Create";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Access(AccessType.PROPERTY)
	private Long id;

	@Column(nullable = true)
	private String userId;

	@Column(nullable = true, unique = false, length = 500)
	private String comment;

	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	/**
	 * Edit or Create
	 */
	@Column(nullable = false, updatable = false)
	private String action = ACTION_EDIT;

	/**
	 * E.g. Model, etc.
	 */
	@Column(nullable = false, length = 50, updatable = false)
	private String objType;

	@Column(nullable = false, length = 20, updatable = false)
	private String objId;

	public Revision() {
		super();
	}

	public Revision(String userId, String objType, String objId, String comment) {
		super();
		this.userId = userId;
		this.comment = comment;
		this.objType = objType;
		this.objId = objId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String description) {
		this.comment = description;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String type) {
		this.action = type;
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

	public String getObjType() {
		return objType;
	}

	public void setObjType(String objType) {
		this.objType = objType;
	}

	public String getObjId() {
		return objId;
	}

	public void setObjId(String objId) {
		this.objId = objId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	

}
