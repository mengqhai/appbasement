package com.workstream.rest.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.workstream.core.model.Organization;

@JsonInclude(Include.NON_NULL)
public class OrgResponse extends InnerWrapperObj<Organization> {

	public OrgResponse(Organization org) {
		super(org);
	}

	public Long getId() {
		return inner.getId();
	}

	public String getName() {
		return inner.getName();
	}

	public String getIdentifier() {
		return inner.getIdentifier();
	}

	public String getDescription() {
		return inner.getDescription();
	}

	public Date getCreatedAt() {
		return inner.getCreatedAt();
	}

}
