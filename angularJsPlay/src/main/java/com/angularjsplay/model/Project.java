package com.angularjsplay.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;

import com.appbasement.model.User;

@Entity
public class Project implements IEntity{

	@Id
	private Long id;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "PRODUCT_OWNER")
	private User productOwner;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "SCRUM_MASTER")
	private User scrumMaster;

	@ManyToMany
	@JoinTable(name = "PROJECT_MEMBERS", joinColumns = @JoinColumn(name = "PROJECT_ID"), inverseJoinColumns = @JoinColumn(name = "USER_ID"))
	private Set<User> teamMembers;

	@Column(length = 512)
	private String name;

	@Column(length = 2048)
	private String desc;

	/**
	 * A product backlog is a list of all desired product features (weather you
	 * plan to implement them or not).
	 */
	@OneToMany
	@JoinColumn(name = "PROJECT_ID", nullable = false)
	@ForeignKey(name = "FK_PROJECT_PRODUCT_BACKLOGS")
	private Collection<Backlog> productBacklogs = new ArrayList<Backlog>();

	@OneToMany
	@JoinColumn(name = "PROJECT_ID", nullable = false)
	@ForeignKey(name = "FK_PROJECT_SPRINTS")
	private Collection<Sprint> sprints = new ArrayList<Sprint>();

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false, updatable = false)
	private Date createdAt;

	public Project() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getProductOwner() {
		return productOwner;
	}

	public void setProductOwner(User productOwner) {
		this.productOwner = productOwner;
	}

	public User getScrumMaster() {
		return scrumMaster;
	}

	public void setScrumMaster(User scrumMaster) {
		this.scrumMaster = scrumMaster;
	}

	public Set<User> getTeamMembers() {
		return teamMembers;
	}

	public void setTeamMembers(Set<User> teamMembers) {
		this.teamMembers = teamMembers;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Collection<Backlog> getProductBacklogs() {
		return productBacklogs;
	}

	public void setProductBacklogs(Collection<Backlog> productBacklogs) {
		this.productBacklogs = productBacklogs;
	}

	public Collection<Sprint> getSprints() {
		return sprints;
	}

	public void setSprints(Collection<Sprint> sprints) {
		this.sprints = sprints;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	@PrePersist
	protected void setCreatedAt() {
		if (createdAt == null) {
			createdAt = new Date();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
		result = prime * result
				+ ((getDesc() == null) ? 0 : getDesc().hashCode());
		result = prime * result
				+ ((getName() == null) ? 0 : getName().hashCode());
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
		} else if (!getCreatedAt().equals(other.getCreatedAt()))
			return false;
		if (getDesc() == null) {
			if (other.getDesc() != null)
				return false;
		} else if (!getDesc().equals(other.getDesc()))
			return false;
		if (getName() == null) {
			if (other.getName() != null)
				return false;
		} else if (!getName().equals(other.getName()))
			return false;
		return true;
	}

}
