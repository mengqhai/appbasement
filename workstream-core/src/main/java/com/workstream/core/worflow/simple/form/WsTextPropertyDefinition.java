package com.workstream.core.worflow.simple.form;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.activiti.workflow.simple.definition.form.TextPropertyDefinition;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.workstream.core.CoreConstants;

/**
 * Replaces the Activiti TextPropertyDefinition as the type property is null.
 * 
 * @author qinghai
 * 
 */
@JsonTypeName("text")
public class WsTextPropertyDefinition extends TextPropertyDefinition {

	public WsTextPropertyDefinition() {
		super();
		this.setType("text");
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
	public void setDisplayName(String displayName) {
		super.setDisplayName(displayName);
	}


}
