package com.workstream.rest.model;

import java.util.List;

import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;

public class StartFormDataResponse extends InnerWrapperObj<StartFormData> {
	private StartFormData startFormData;

	public StartFormDataResponse(StartFormData startFormData) {
		super(startFormData);
		this.startFormData = startFormData;
	}

	public String getFormKey() {
		return startFormData.getFormKey();
	}

	public String getDeploymentId() {
		return startFormData.getDeploymentId();
	}

	public List<FormProperty> getFormProperties() {
		return startFormData.getFormProperties();
	}

}
