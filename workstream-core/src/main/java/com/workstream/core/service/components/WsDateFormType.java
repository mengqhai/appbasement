package com.workstream.core.service.components;

import org.activiti.engine.impl.form.DateFormType;

public class WsDateFormType extends DateFormType {

	public WsDateFormType(String datePattern) {
		super(datePattern);
	}

	public String getDatePattern() {
		return datePattern;
	}

}
