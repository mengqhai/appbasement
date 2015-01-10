package com.workstream.rest.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.workstream.core.model.Organization;

@JsonInclude(Include.NON_NULL)
public class OrgResponse {

	private Organization org;

	public OrgResponse(Organization org) {
		super();
		this.org = org;
	}
	
	public Long getId() {
		return org.getId();
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
