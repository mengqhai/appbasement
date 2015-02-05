package com.workstream.core.worflow.simple.form;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.activiti.workflow.simple.definition.form.NumberPropertyDefinition;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.workstream.core.CoreConstants;

@JsonTypeName("number")
public class WsNumberPropertyDefinition extends NumberPropertyDefinition {

	public WsNumberPropertyDefinition() {
		super();
		setType("number");
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
