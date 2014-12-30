package com.workstream.core.worflow.simple;

import java.util.ArrayList;
import java.util.List;

import org.activiti.bpmn.model.FormProperty;
import org.activiti.bpmn.model.FormValue;
import org.activiti.workflow.simple.converter.step.HumanStepDefinitionConverter;
import org.activiti.workflow.simple.definition.form.BooleanPropertyDefinition;
import org.activiti.workflow.simple.definition.form.DatePropertyDefinition;
import org.activiti.workflow.simple.definition.form.FormDefinition;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.activiti.workflow.simple.definition.form.ListPropertyDefinition;
import org.activiti.workflow.simple.definition.form.ListPropertyEntry;
import org.activiti.workflow.simple.definition.form.NumberPropertyDefinition;

public class CoreHumanStepDefinitionConverter extends
		HumanStepDefinitionConverter {

	@Override
	protected List<FormProperty> convertProperties(FormDefinition formDefinition) {
		List<FormProperty> formProperties = new ArrayList<FormProperty>();

		for (FormPropertyDefinition propertyDefinition : formDefinition
				.getFormPropertyDefinitions()) {
			FormProperty formProperty = new FormProperty();
			formProperties.add(formProperty);

			formProperty.setId(propertyDefinition.getName());
			formProperty.setName(propertyDefinition.getName());
			formProperty.setRequired(propertyDefinition.isMandatory());

			String type = null;
			if (propertyDefinition instanceof NumberPropertyDefinition) {
				type = "long";
			} else if (propertyDefinition instanceof DatePropertyDefinition) {
				type = "date";
			} else if (propertyDefinition instanceof ListPropertyDefinition) {

				type = "enum";
				ListPropertyDefinition listDefinition = (ListPropertyDefinition) propertyDefinition;

				if (!listDefinition.getEntries().isEmpty()) {
					List<FormValue> formValues = new ArrayList<FormValue>(
							listDefinition.getEntries().size());
					for (ListPropertyEntry entry : listDefinition.getEntries()) {
						FormValue formValue = new FormValue();
						// We're using same value for id and name for the moment
						formValue.setId(entry.getValue());
						formValue.setName(entry.getName());
						formValues.add(formValue);
					}
					formProperty.setFormValues(formValues);
				}
			} else if (propertyDefinition instanceof BooleanPropertyDefinition) {
				type = "boolean";
			} else {
				// Fallback to simple text
				type = "string";
			}
			formProperty.setType(type);
		}

		return formProperties;
	}

}
