package com.workstream.rest.model;

import java.util.Date;

import com.workstream.core.model.Organization;

public class OrgResponse {

	private Organization org;

	public OrgResponse(Organization org) {
		super();
		this.org = org;
	}

	public String getName() {
		return org.getName();
	}

	public String getIdentifier() {
		return org.getIdentifier();
	}

	public String getDescription() {
		return org.getDescription();
	}

	public Date getCreatedAt() {
		return org.getCreatedAt();
	}

}
