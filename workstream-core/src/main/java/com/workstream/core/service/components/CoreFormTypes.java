package com.workstream.core.service.components;

import org.activiti.bpmn.model.FormProperty;
import org.activiti.engine.form.AbstractFormType;
import org.activiti.engine.impl.form.FormTypes;
import org.springframework.stereotype.Component;

@Component
public class CoreFormTypes extends FormTypes {

	@Override
	public void addFormType(AbstractFormType formType) {
		super.addFormType(formType);
	}

	@Override
	public AbstractFormType parseFormPropertyType(FormProperty formProperty) {
		return super.parseFormPropertyType(formProperty);
	}
	
	

}
