package com.workstream.core.worflow.simple.def;

import java.util.List;

import javax.validation.Valid;

import org.activiti.workflow.simple.definition.form.FormDefinition;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;

public class WsFormDefinition extends FormDefinition {

	@Valid
	@Override
	public List<FormPropertyDefinition> getFormPropertyDefinitions() {
		return super.getFormPropertyDefinitions();
	}

}
