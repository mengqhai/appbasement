package com.workstream.core.model;

import java.io.Serializable;
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
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;

@Entity
@Table(name = "WS_GROUP")
@Access(AccessType.FIELD)
public class GroupX implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3689707446355463505L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Access(AccessType.PROPERTY)
	private Long id;

	@Column(nullable = true, unique = true, length = 50)
	private String groupId;

	@Column(length = 500)
	private String description;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "ORG_ID")
	@ForeignKey(name = "FK_GROUP_ORG")
	private Organization org;

	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Organization getOrg() {
		return org;
	}

	public void setOrg(Organization org) {
		this.org = org;
		//org.getGroups().add(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((getDescription() == null) ? 0 : getDescription().hashCode());
		result = prime * result
				+ ((getGroupId() == null) ? 0 : getGroupId().hashCode());
		// result = prime * result
		// + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
		// comment the createdAt field, as before and after persistence, the
		// hashcode changes, which should never happen
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof GroupX))
			return false;
		GroupX other = (GroupX) obj;

		if (getCreatedAt() == null) {
			if (other.getCreatedAt() != null)
				return false;
		} else if (getCreatedAt().getTime() != other.getCreatedAt().getTime())
			// Date/time compare in hibernate must be like this
			// because at runtime this field will be replaced by
			// java.sql.Timestamp
			// which will not pass the Date.equals()
			return false;

		if (getDescription() == null) {
			if (other.getDescription() != null)
				return false;
		} else if (!getDescription().equals(other.getDescription()))
			return false;
		if (getGroupId() == null) {
			if (other.getGroupId() != null)
				return false;
		} else if (!getGroupId().equals(other.getGroupId()))
			return false;
		return true;
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
