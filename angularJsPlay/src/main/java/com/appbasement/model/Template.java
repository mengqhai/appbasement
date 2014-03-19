package com.appbasement.model;

import java.io.Serializable;
import java.sql.Clob;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "APP_TEMPLATE")
@Access(AccessType.FIELD)
@NamedQueries({ @NamedQuery(name = Template.QUERY_BY_NAME, query = "select t from Template as t where t.name=:name") })
public class Template implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5072272475856592606L;

	public static final String QUERY_BY_NAME = "Template-query_by_name";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Access(AccessType.PROPERTY)
	private Long id;

	@Column(nullable = false, unique = true, length = 50)
	private String name;

	/**
	 * velocity ResourceLoader requires stream of template resource, so have to
	 * define this field as CLOB
	 */
	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(nullable = false)
	private Clob definition;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date lastUpdate;

	public Template() {
	}

	@PrePersist
	protected void onCreate() {
		setLastUpdate(new Date());
	}

	@PreUpdate
	protected void onUpdate() {
		setLastUpdate(new Date());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getLastUpdate() == null) ? 0 : getLastUpdate().hashCode());
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
		if (!(obj instanceof Template))
			return false;
		Template other = (Template) obj;
		if (getLastUpdate() == null) {
			if (other.getLastUpdate() != null)
				return false;
		} else if (!getLastUpdate().equals(other.getLastUpdate()))
			return false;
		if (getName() == null) {
			if (other.getName() != null)
				return false;
		} else if (!getName().equals(other.getName()))
			return false;
		return true;
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

	public Template setName(String name) {
		this.name = name;
		return this;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public Template setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
		return this;
	}

	@Override
	public String toString() {
		return "Template [name=" + getName() + ", lastUpdate="
				+ getLastUpdate() + "]";
	}

	public Clob getDefinition() {
		return definition;
	}

	public void setDefinition(Clob definition) {
		this.definition = definition;
	}

}
