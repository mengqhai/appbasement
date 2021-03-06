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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "WS_PROJECT")
@Access(AccessType.FIELD)
public class Project implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8121462675178174005L;

	/**
	 * <b>Open</b>: Members of your organization will be able to see and work
	 * with this project. So the ProjectMembership is ignored in this case.<br>
	 * <b>Invite</b>: (default)invite-only, only invited users will see this
	 * project
	 * 
	 * @author qinghai
	 * 
	 */
	public enum ProjectVisibility {
		OPEN, INVITE
	}

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
	@JoinColumn(name = "ORG_ID", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PROJECT_ORG"))
	private Organization org;

	@Enumerated(EnumType.ORDINAL)
	@Column(nullable = false)
	private ProjectVisibility visibility = ProjectVisibility.INVITE;

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

	public Organization getOrg() {
		return org;
	}

	public void setOrg(Organization org) {
		this.org = org;
		// org.getProjects().add(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((getDescription() == null) ? 0 : getDescription().hashCode());
		result = prime * result
				+ ((getDueTime() == null) ? 0 : getDueTime().hashCode());
		result = prime * result
				+ ((getName() == null) ? 0 : getName().hashCode());
		result = prime * result
				+ ((getOrg() == null) ? 0 : getOrg().hashCode());
		result = prime * result
				+ ((getStartTime() == null) ? 0 : getStartTime().hashCode());
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
		if (!(obj instanceof Project))
			return false;
		Project other = (Project) obj;

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
		if (getDueTime() == null) {
			if (other.getDueTime() != null)
				return false;
		} else if (getDueTime().getTime() != other.getDueTime().getTime())
			return false;
		if (getName() == null) {
			if (other.getName() != null)
				return false;
		} else if (!getName().equals(other.getName()))
			return false;
		if (getOrg() == null) {
			if (other.getOrg() != null)
				return false;
		} else if (!getOrg().equals(other.getOrg()))
			return false;
		if (getStartTime() == null) {
			if (other.getStartTime() != null)
				return false;
		} else if (getStartTime().getTime() != other.getStartTime().getTime())
			// Date/time compare in hibernate must be like this
			// because at runtime this field will be replaced by
			// java.sql.Timestamp
			// which will not pass the Date.equals()
			return false;
		return true;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public ProjectVisibility getVisibility() {
		return visibility;
	}

	public void setVisibility(ProjectVisibility visibility) {
		this.visibility = visibility;
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
