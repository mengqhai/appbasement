package com.workstream.core.model;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "WS_NOTIFICATION", indexes = @Index(name = "userId_idx", unique = false, columnList = "userId"))
@Access(AccessType.FIELD)
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Access(AccessType.PROPERTY)
	private Long id;

	/**
	 * Receiver
	 */
	private String userId;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "SUB_ID", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_NOTIFICATION_SUB"))
	private Subscription sub;

	@ManyToOne(optional = true, fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "EVENT_ID", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_NOTIFICATION_EVENT"))
	private CoreEvent event;

	private String type = "EVENT"; // currently the only type

	private String message; // no use for now

	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	private boolean read;

	private Long orgId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Subscription getSub() {
		return sub;
	}

	public void setSub(Subscription sub) {
		this.sub = sub;
	}

	public CoreEvent getEvent() {
		return event;
	}

	public void setEvent(CoreEvent event) {
		this.event = event;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
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

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
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
}
