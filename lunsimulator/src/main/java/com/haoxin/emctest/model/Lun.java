package com.haoxin.emctest.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;

/**
 * Model object for LUN.
 *
 */
@Entity
@Table(name = "LUN")
public class Lun implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6422837616062931580L;

	/**
	 * unique id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Min(0)
	private long id;
	
	/**
	 * The size of the the LUN, in KB.
	 */
	@Min(0)
	private long size;
	
	
	public Lun() {
	}
	
	public Lun(long size) {
		super();
		this.size = size;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the size
	 */
	public long getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(long size) {
		this.size = size;
	}
}
