package com.workstream.rest.model;

import java.util.Date;

import com.workstream.core.model.Revision;

public class RevisionResponse extends InnerWrapperObj<Revision> {

	private Revision revision;

	public RevisionResponse(Revision revision) {
		super(revision);
		this.revision = revision;
	}

	public String getUserId() {
		return revision.getUserId();
	}

	public String getComment() {
		return revision.getComment();
	}

	public String getAction() {
		return revision.getAction();
	}

	public Date getCreatedAt() {
		return revision.getCreatedAt();
	}

	public String getObjType() {
		return revision.getObjType();
	}

	public String getObjId() {
		return revision.getObjId();
	}

	public Long getId() {
		return revision.getId();
	}

}
