package com.angularjsplay.model;

import java.io.Serializable;

public class UploadedFile implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5852173010306553704L;
	private String name;

	public UploadedFile() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	

}
