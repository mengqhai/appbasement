package com.workstream.core.service.components;

import java.util.List;

import org.activiti.bpmn.model.FormProperty;
import org.activiti.engine.form.AbstractFormType;
import org.activiti.engine.impl.form.BooleanFormType;
import org.activiti.engine.impl.form.DoubleFormType;
import org.activiti.engine.impl.form.FormTypes;
import org.activiti.engine.impl.form.LongFormType;
import org.activiti.engine.impl.form.StringFormType;
import org.apache.commons.lang3.StringUtils;

public class CoreFormTypes extends FormTypes {

	public CoreFormTypes(List<AbstractFormType> customFormTypes) {
		// build in types
		addFormType(new StringFormType());
		addFormType(new LongFormType());
		//addFormType(new WsDateFormType("yyyy-MM-dd"));
		// see http://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
		addFormType(new WsDateFormType("yyyy-MM-dd"));
		addFormType(new BooleanFormType());
		addFormType(new DoubleFormType());

		// custom types
		if (customFormTypes != null) {
			for (AbstractFormType customType : customFormTypes) {
				addFormType(customType);
			}
		}
	}

	@Override
	public void addFormType(AbstractFormType formType) {
		super.addFormType(formType);
	}

	@Override
	public AbstractFormType parseFormPropertyType(FormProperty formProperty) {
		if ("date".equals(formProperty.getType())
				&& StringUtils.isNotEmpty(formProperty.getDatePattern())) {
			return new WsDateFormType(formProperty.getDatePattern());
		} else {
			return super.parseFormPropertyType(formProperty);
		}

	}

}
