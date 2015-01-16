package com.workstream.core.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@SuppressWarnings("serial")
@Entity
@Table(name = "WS_BINARY")
@Access(AccessType.FIELD)
public class BinaryObj implements Serializable {

	public enum BinaryObjType {
		ATTACHMENT_THUMB, ATTACHMENT_CONTENT, USER_PICTURE
	}

	public enum BinaryReposType {
		FILE_SYSTEM_REPOSITORY
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Access(AccessType.PROPERTY)
	private long id;

	private String name;

	private BinaryObjType type;

	private String targetId;

	private String contentType;

	private BinaryReposType reposType;

	private String repositoryKey;

	/**
	 * In byte
	 */
	private long size;

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

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public BinaryReposType getReposType() {
		return reposType;
	}

	public void setReposType(BinaryReposType reposType) {
		this.reposType = reposType;
	}

	public String getRepositoryKey() {
		return repositoryKey;
	}

	public void setRepositoryKey(String repositoryKey) {
		this.repositoryKey = repositoryKey;
	}

	public static BinaryObj newBinaryObj(BinaryObjType objType,
			String targetId, BinaryReposType reposType, String contentType,
			String name) {
		BinaryObj binary = new BinaryObj();
		binary.setName(name);
		binary.setContentType(contentType);
		binary.setType(objType);
		binary.setReposType(reposType);
		binary.setTargetId(targetId);
		return binary;
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
