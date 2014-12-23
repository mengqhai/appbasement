package com.workstream.core.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "WS_USER")
@Access(AccessType.FIELD)
public class UserX {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Access(AccessType.PROPERTY)
	private Long id;

	@Column(nullable = false, updatable = false)
	private String userId;

	@ManyToMany(mappedBy = "users", fetch = FetchType.LAZY)
	private Set<Organization> orgs = new HashSet<Organization>();

	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Set<Organization> getOrgs() {
		return orgs;
	}

	public void joinOrg(Organization org) {
		this.getOrgs().add(org);
		org.getUsers().add(this);
	}

	public void leaveOrg(Organization org) {
		this.getOrgs().remove(org);
		org.getUsers().remove(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getUserId() == null) ? 0 : getUserId().hashCode());

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
		if (!(obj instanceof UserX))
			return false;
		UserX other = (UserX) obj;

		if (getCreatedAt() == null) {
			if (other.getCreatedAt() != null)
				return false;
		} else if (getCreatedAt().getTime() != other.getCreatedAt().getTime())
			// Date/time compare in hibernate must be like this
			// because at runtime this field will be replaced by
			// java.sql.Timestamp
			// which will not pass the Date.equals()
			return false;

		if (getUserId() == null) {
			if (other.getUserId() != null)
				return false;
		} else if (!getUserId().equals(other.getUserId()))
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
