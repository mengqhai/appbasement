package com.workstream.core.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "WS_EVENT")
@Access(AccessType.FIELD)
public class CoreEvent implements Serializable {

	public enum TargetType {
		COMMENT, TASK, PROJECT, PROCESS, ORG
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -1023038030037542431L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Access(AccessType.PROPERTY)
	private long id;

	@Enumerated(EnumType.ORDINAL)
	@Column(nullable = false, updatable = false)
	private TargetType targetType;

	private String targetId;

	@Enumerated(EnumType.ORDINAL)
	private TargetType parentType;

	private String parentId;

	private String eventType;

	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	/**
	 * The one who triggered the event
	 */
	private String userId;

	public TargetType getTargetType() {
		return targetType;
	}

	public void setTargetType(TargetType targetType) {
		this.targetType = targetType;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public TargetType getParentType() {
		return parentType;
	}

	public void setParentType(TargetType parentType) {
		this.parentType = parentType;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
