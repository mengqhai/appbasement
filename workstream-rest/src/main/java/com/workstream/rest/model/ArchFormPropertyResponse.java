package com.workstream.rest.model;

import java.util.Date;

import org.activiti.engine.history.HistoricFormProperty;

public class ArchFormPropertyResponse extends InnerWrapperObj<HistoricFormProperty>{
	
	private HistoricFormProperty formProperty;
	
	public ArchFormPropertyResponse(HistoricFormProperty formProperty) {
		super(formProperty);
		this.formProperty = formProperty;
	}

	public String getId() {
		return formProperty.getId();
	}

	public String getPropertyId() {
		return formProperty.getPropertyId();
	}

	public String getProcessInstanceId() {
		return formProperty.getProcessInstanceId();
	}

	public String getPropertyValue() {
		return formProperty.getPropertyValue();
	}

	public String getTaskId() {
		return formProperty.getTaskId();
	}

	public Date getTime() {
		return formProperty.getTime();
	}

}
