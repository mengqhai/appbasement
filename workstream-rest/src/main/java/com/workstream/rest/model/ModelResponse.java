package com.workstream.rest.model;

import java.util.Date;

import org.activiti.engine.repository.Model;

public class ModelResponse extends InnerWrapperObj<Model> {

	public ModelResponse(Model model) {
		super(model);
	}

	public String getId() {
		return inner.getId();
	}

	public String getName() {
		return inner.getName();
	}

	public Date getCreateTime() {
		return inner.getCreateTime();
	}

	public Date getLastUpdateTime() {
		return inner.getLastUpdateTime();
	}

	public Integer getVersion() {
		return inner.getVersion();
	}

	public String getDeploymentId() {
		return inner.getDeploymentId();
	}

	public String getOrgId() {
		return inner.getTenantId();
	}

}
