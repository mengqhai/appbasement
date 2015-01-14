package com.workstream.core.service.components;

import org.activiti.engine.impl.form.DateFormType;

public class WsDateFormType extends DateFormType {
	DateFormType date;

	public WsDateFormType(String datePattern) {
		super(datePattern);
	}

	/**
	 * Expose the datePattern, so client can display/validate the value format
	 * correctly.
	 * 
	 * @return
	 */
	public String getDatePattern() {
		return datePattern;
	}

}
