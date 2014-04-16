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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.appbasement.model.User;

@Entity
public class Project {

	@Id
	private long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "PRODUCT_OWNER")
	private User productOwner;

	@ManyToOne
	@JoinColumn(name = "SCRUM_MASTER")
	private User scrumMaster;

	@ManyToMany
	@JoinTable(name = "PROJECT_MEMBERS", joinColumns = @JoinColumn(name = "PROJECT_ID"), inverseJoinColumns = @JoinColumn(name = "USER_ID"))
	private Set<User> teamMembers;

	private String name;

	private String desc;

	/**
	 * A product backlog is a list of all desired product features (weather you
	 * plan to implement them or not).
	 */
	@OneToMany
	@JoinColumn(name = "PROJECT_ID", nullable = false)
	private Collection<Backlog> productBacklogs = new ArrayList<Backlog>();

	@OneToMany
	@JoinColumn(name = "PROJECT_ID", nullable = false)
	private Collection<Sprint> sprints = new ArrayList<Sprint>();

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false, updatable = true)
	private Date createdAt;

	public Project() {
	}

	protected long getId() {
		return id;
	}

	protected void setId(long id) {
		this.id = id;
	}

	protected User getProductOwner() {
		return productOwner;
	}

	protected void setProductOwner(User productOwner) {
		this.productOwner = productOwner;
	}

	protected User getScrumMaster() {
		return scrumMaster;
	}

	protected void setScrumMaster(User scrumMaster) {
		this.scrumMaster = scrumMaster;
	}

	protected Set<User> getTeamMembers() {
		return teamMembers;
	}

	protected void setTeamMembers(Set<User> teamMembers) {
		this.teamMembers = teamMembers;
	}

	protected String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	protected String getDesc() {
		return desc;
	}

	protected void setDesc(String desc) {
		this.desc = desc;
	}

	protected Collection<Backlog> getProductBacklogs() {
		return productBacklogs;
	}

	protected void setProductBacklogs(Collection<Backlog> productBacklogs) {
		this.productBacklogs = productBacklogs;
	}

	protected Collection<Sprint> getSprints() {
		return sprints;
	}

	protected void setSprints(Collection<Sprint> sprints) {
		this.sprints = sprints;
	}

	protected Date getCreatedAt() {
		return createdAt;
	}

	protected void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

}
