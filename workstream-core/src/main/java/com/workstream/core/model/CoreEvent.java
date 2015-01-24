package com.workstream.core.model;

import java.io.Serializable;
import java.util.Date;
import java.util.EnumSet;

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
@org.hibernate.annotations.BatchSize(size = 10)
public class CoreEvent implements Serializable {

	public enum TargetType {
		COMMENT, TASK, PROJECT, PROCESS, ORG
	}

	public enum EventType {
		CREATED, DELETED, COMPLETED, ASSIGNED, ARCHIVED
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -1023038030037542431L;

	public static final EnumSet<EventType> END_EVENT_TYPE = EnumSet.of(
			EventType.COMPLETED, EventType.DELETED, EventType.ARCHIVED);

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Access(AccessType.PROPERTY)
	private Long id;

	@Enumerated(EnumType.ORDINAL)
	@Column(nullable = false, updatable = false)
	private TargetType targetType;

	private String targetId;

	@Enumerated(EnumType.ORDINAL)
	private TargetType parentType;

	private String parentId;

	@Enumerated(EnumType.ORDINAL)
	private EventType eventType;

	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	@Column(name = "ADDITIONAL", updatable = false)
	private String additionalInfo;

	/**
	 * Have to make an non-Foreign-Key reference here. Because problem happens
	 * when deleting org: delete org -> delete process -> insert process deleted
	 * event -> breaks org Id foreign key
	 * 
	 */
	private Long orgId;

	/**
	 * Let JPA delete the notifications when the event is removed(caused by
	 * organization removal).
	 */
	// @OneToMany(mappedBy = "event", cascade = CascadeType.REMOVE)
	// private Collection<Notification> notifications = new
	// ArrayList<Notification>();

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

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public boolean isEndEvent() {
		if (END_EVENT_TYPE.contains(getEventType())) {
			return true;
		} else {
			return false;
		}
	}

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
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

	public String getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

}
