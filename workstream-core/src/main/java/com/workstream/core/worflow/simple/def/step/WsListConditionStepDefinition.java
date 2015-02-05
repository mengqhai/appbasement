package com.workstream.core.worflow.simple.def.step;

import org.activiti.workflow.simple.definition.ListConditionStepDefinition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonTypeName("list")
@JsonInclude(Include.NON_NULL)
public class WsListConditionStepDefinition<T> extends
		ListConditionStepDefinition<T> {

	@JsonIgnore
	@Override
	public String getId() {
		return super.getId();
	}

	@JsonIgnore
	@Override
	public void setId(String id) {
		super.setId(id);
	}

}
