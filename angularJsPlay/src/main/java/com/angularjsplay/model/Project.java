package com.angularjsplay.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

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

	public Project() {
	}

}
