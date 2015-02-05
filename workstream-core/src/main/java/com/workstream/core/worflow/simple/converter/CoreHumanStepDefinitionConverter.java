package com.workstream.core.worflow.simple.converter;

import java.util.ArrayList;
import java.util.List;

import org.activiti.bpmn.model.FormProperty;
import org.activiti.bpmn.model.FormValue;
import org.activiti.workflow.simple.converter.step.HumanStepDefinitionConverter;
import org.activiti.workflow.simple.definition.StepDefinition;
import org.activiti.workflow.simple.definition.form.BooleanPropertyDefinition;
import org.activiti.workflow.simple.definition.form.DatePropertyDefinition;
import org.activiti.workflow.simple.definition.form.FormDefinition;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;
import org.activiti.workflow.simple.definition.form.ListPropertyDefinition;
import org.activiti.workflow.simple.definition.form.ListPropertyEntry;
import org.activiti.workflow.simple.definition.form.NumberPropertyDefinition;

import com.workstream.core.worflow.simple.def.step.WsHumanStepDefinition;

public class CoreHumanStepDefinitionConverter extends
		HumanStepDefinitionConverter {

	@Override
	public Class<? extends StepDefinition> getHandledClass() {
		return WsHumanStepDefinition.class;
	}

	public static List<FormProperty> convertFormProperties(
			FormDefinition formDefinition) {
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
				DatePropertyDefinition datePropDef = (DatePropertyDefinition) propertyDefinition;
				if (datePropDef.isShowTime()) {
					formProperty.setDatePattern("yyyy-MM-dd HH:mm:ss");
				} else {
					formProperty.setDatePattern("yyyy-MM-dd");
					// by default Activiti uses dd/MM/yyyy
					// here we customize the date pattern
					// How the pattern if finally used by activiti?
					// See activiti FormTypes.parseFormPropertyType()
				}
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

	@Override
	public List<FormProperty> convertProperties(FormDefinition formDefinition) {
		return convertFormProperties(formDefinition);
	}

}
