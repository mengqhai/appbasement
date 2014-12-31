package com.workstream.core.model;

import java.util.ArrayList;
import java.util.List;

public class ProcessModelMetaInfo {

	List<RevisionEntry> revisions = new ArrayList<RevisionEntry>();

	String name;

	String description;

	public List<RevisionEntry> getRevisions() {
		return revisions;
	}

	public void setRevisions(List<RevisionEntry> revisions) {
		this.revisions = revisions;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
