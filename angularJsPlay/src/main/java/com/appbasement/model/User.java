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
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.angularjsplay.mvc.validation.ValidateOnCreate;
import com.angularjsplay.mvc.validation.ValidateOnUpdate;

@Entity
@Table(name = "APP_USER")
@Access(AccessType.FIELD)
@NamedQueries(@NamedQuery(name = User.QUERY_BY_USERNAME, query = "select u from User as u where u.username=:username"))
public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1285942145807388313L;

	public static final String QUERY_BY_USERNAME = "User-query_by_username";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Access(AccessType.PROPERTY)
	private Long id;

	@Column(nullable = false, unique = true, updatable = false, length = 30)
	// Bean validation
	@NotNull
	@Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters long.")
	@Pattern(regexp = "^[a-zA-Z0-9\u4e00-\u9fa5]+$", message = "Username must be alphanumeric or Chinese characters with non spaces.")
	private String username;

	@Column(nullable = false, length = 50)
	// Bean validation
	@NotNull(groups = ValidateOnCreate.class)
	@Size(min = 6, max = 50, message = "The password must be 6-50 characters long.", groups = {
			ValidateOnCreate.class, ValidateOnUpdate.class })
	private String password;

	@Column(nullable = false, unique = true, updatable = true, length = 60)
	// Bean validation
	@NotNull
	@Pattern(regexp = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}", message = "Invalid email address.")
	@Size(max = 60, message = "Email address too long.")
	private String email;

	@ManyToMany(mappedBy = "users", fetch = FetchType.LAZY)
	private Set<Group> groups = new HashSet<Group>();

	@Column(nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	public User() {
	}

	public User(String username) {
		super();
		this.username = username;
	}

	public User(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

	public Long getId() {
		return id;
	}

	public User setId(Long id) {
		this.id = id;
		return this;
	}

	public String getUsername() {
		return username;
	}

	public User setUsername(String username) {
		this.username = username;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public User setEmail(String email) {
		this.email = email;
		return this;
	}

	@JsonIgnore
	public String getPassword() {
		return password;
	}

	public User setPassword(String password) {
		this.password = password;
		return this;
	}

	/**
	 * Bidirectional link, don't directly call add()
	 * 
	 * @return
	 */
	@JsonIgnore
	public Set<Group> getGroups() {
		return groups;
	}

	public void addToGroup(Group group) {
		if (group == null) {
			throw new IllegalArgumentException("Null group");
		}
		group.getUsers().add(this);
		this.groups.add(group);
	}

	public void removeFromGroup(Group group) {
		if (group == null) {
			throw new IllegalArgumentException("Null group");
		}
		group.getUsers().remove(this);
		this.groups.remove(group);
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
	public User setCreatedAt(Date createdTime) {
		this.createdAt = createdTime;
		return this;
	}

	@Override
	public int hashCode() {
		// username, createdTime, and email rarely changes, so we use them in
		// hash code generation
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getEmail() == null) ? 0 : getEmail().hashCode());
		result = prime * result
				+ ((getUsername() == null) ? 0 : getUsername().hashCode());
		result = prime * result
				+ ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		// do not use getClass() because it fails with dynamic proxy
		if (!(obj instanceof User))
			return false;
		User other = (User) obj;

		// Book JPWH P435: the equals() and hashCode() methods always access the
		// properties of the "other" object via the getter methods. Because it
		// could be a lazy proxy with null in the property
		if (getEmail() == null) {
			if (other.getEmail() != null)
				return false;
		} else if (!getEmail().equals(other.getEmail()))
			return false;
		if (getUsername() == null) {
			if (other.getUsername() != null)
				return false;
		} else if (!getUsername().equals(other.getUsername()))
			return false;
		if (getPassword() == null) {
			if (other.getPassword() != null)
				return false;
		} else if (!getPassword().equals(other.getPassword()))
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
		return "User [username=" + username + ", email=" + email + "]";
	}

	@PrePersist
	protected void setCreatedAt() {
		if (getCreatedAt() == null) {
			setCreatedAt(new Date());
		}
	}

}
