package com.workstream.core.worflow.simple.def.step;

import org.activiti.workflow.simple.definition.ListStepDefinition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("list")
public class WsListStepDefinition<T> extends ListStepDefinition<T> {

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
