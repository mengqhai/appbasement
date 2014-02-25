package com.appbasement.model;

import java.io.Serializable;
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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.ForeignKey;

@Entity
@Table(name = "APP_GROUP")
@Access(AccessType.FIELD)
public class Group implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5614194900665433187L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Access(AccessType.PROPERTY)
	private Long id;

	@Column(nullable = false, unique = true, length = 50)
	// Bean validation
	@NotNull
	@Size(min = 3, max = 50, message = "Group name must be between 3 and 50 characters long.")
	@Pattern(regexp = "^[a-zA-Z0-9\u4e00-\u9fa5]+$", message = "Group name must be alphanumeric or Chinese characters with non spaces.")
	private String name;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "APP_GROUP_USER", joinColumns = { @JoinColumn(name = "GROUP_ID") }, inverseJoinColumns = { @JoinColumn(name = "USER_ID") })
	@ForeignKey(name = "FK_GROUP_USER_GROUP", inverseName = "FK_GROUP_USER_USER")
	private Set<User> users = new HashSet<User>();

	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt = new Date();

	public Group() {
		super();
	}

	public Group(String name) {
		super();
		this.name = name;
	}

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

	/**
	 * Bidirectional link, don't directly call add()
	 * 
	 * @return
	 */
	public Set<User> getUsers() {
		return users;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	/**
	 * Convenient method only for UT. No invocation in runtime code.
	 * 
	 * @param createdAt
	 * @return
	 */
	public Group setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		// group name may change, which may do harm to the group instance in
		// User.groups set, so use createdAt (never changes) as hashCode factor
		result = prime * result
				+ ((createdAt == null) ? 0 : createdAt.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Group))
			return false;
		Group other = (Group) obj;
		if (getName() == null) {
			if (other.getName() != null)
				return false;
		} else if (!getName().equals(other.getName()))
			return false;

		if (getCreatedAt() == null) {
			if (other.getCreatedAt() != null)
				return false;
		} else if (getCreatedAt().getTime() != other.getCreatedAt().getTime())
			// Date/time compare in hibernate must be like this
			// because at runtime this field will be replaced by
			// java.sql.Timestamp
			// which will not pass the Date.equals()
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Group [name=" + name + "]";
	}

}
