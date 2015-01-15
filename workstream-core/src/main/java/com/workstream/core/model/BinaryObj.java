package com.workstream.core.model;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.workstream.core.exception.DataPersistException;

@SuppressWarnings("serial")
@Entity
@Table(name = "WS_BINARY")
@Access(AccessType.FIELD)
public class BinaryObj implements Serializable {

	public enum BinaryObjType {
		ATTACHMENT_THUMB, ATTACHMENT_CONTENT
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Access(AccessType.PROPERTY)
	private long id;

	private String name;

	private BinaryObjType type;

	private String targetId;

	private String contentType;

	/**
	 * In byte
	 */
	private long size;

	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(nullable = false)
	private Blob content;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date lastUpdate;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BinaryObjType getType() {
		return type;
	}

	public void setType(BinaryObjType type) {
		this.type = type;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public Blob getContent() {
		return content;
	}

	public InputStream getContentInputStream() {
		try {
			return getContent().getBinaryStream();
		} catch (SQLException e) {
			throw new DataPersistException("Unable to load stream", e);
		}
	}

	public OutputStream getContentOutputStream() {
		try {
			return getContent().setBinaryStream(1);
		} catch (SQLException e) {
			throw new DataPersistException("Unable to load stream", e);
		}
	}

	public void setContent(Blob content) {
		this.content = content;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	@PrePersist
	protected void onCreate() {
		setLastUpdate(new Date());
	}

	@PreUpdate
	protected void onUpdate() {
		setLastUpdate(new Date());
	}

}
