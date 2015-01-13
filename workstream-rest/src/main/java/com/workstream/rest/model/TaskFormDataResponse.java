package com.workstream.rest.model;

import java.util.List;

import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;

public class TaskFormDataResponse extends InnerWrapperObj<TaskFormData> {

	private TaskFormData formData;

	public TaskFormDataResponse(TaskFormData formData) {
		super(formData);
		this.formData = formData;
	}

	public String getFormKey() {
		return formData.getFormKey();
	}

	public String getDeploymentId() {
		return formData.getDeploymentId();
	}

	public List<FormProperty> getFormProperties() {
		return formData.getFormProperties();
	}

}
