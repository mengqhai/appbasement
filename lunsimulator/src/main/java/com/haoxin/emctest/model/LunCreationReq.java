package com.haoxin.emctest.model;

import javax.validation.constraints.Min;

public class LunCreationReq {

	@Min(0)
	private long size;
	
	public LunCreationReq() {
	}

	public LunCreationReq(long size) {
		super();
		this.size = size;
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
