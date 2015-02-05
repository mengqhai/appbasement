package com.workstream.core.worflow.simple.def;

import java.util.List;

import javax.validation.Valid;

import org.activiti.workflow.simple.definition.form.FormDefinition;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = { "formGroups", "formKey" })
public class WsFormDefinition extends FormDefinition {

	@Valid
	@Override
	public List<FormPropertyDefinition> getFormPropertyDefinitions() {
		return super.getFormPropertyDefinitions();
	}

}
