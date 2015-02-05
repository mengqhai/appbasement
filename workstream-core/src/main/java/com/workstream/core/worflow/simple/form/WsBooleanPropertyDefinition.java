package com.workstream.core.worflow.simple.form;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.activiti.workflow.simple.definition.form.BooleanPropertyDefinition;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.workstream.core.CoreConstants;

@JsonTypeName("boolean")
public class WsBooleanPropertyDefinition extends BooleanPropertyDefinition {

	public WsBooleanPropertyDefinition() {
		super();
		setType("boolean");
	}

	@NotNull
	@Size(min = 1, max = CoreConstants.VALID_NAME_SIZE)
	@Override
	public String getName() {
		return super.getName();
	}

	@NotNull
	@Size(min = 1, max = CoreConstants.VALID_NAME_SIZE)
	@Override
	public String getDisplayName() {
		return super.getDisplayName();
	}

}
