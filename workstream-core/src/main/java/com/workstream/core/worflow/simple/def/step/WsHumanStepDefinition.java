package com.workstream.core.worflow.simple.def.step;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.activiti.workflow.simple.definition.HumanStepDefinition;
import org.activiti.workflow.simple.definition.form.FormDefinition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workstream.core.CoreConstants;
import com.workstream.core.worflow.simple.def.WsFormDefinition;

@JsonTypeName("human-step")
public class WsHumanStepDefinition extends HumanStepDefinition {

	@Override
	public String getAssignee() {
		return super.getAssignee();
	}

	@Override
	public List<String> getCandidateUsers() {
		return super.getCandidateUsers();
	}

	@Override
	public List<String> getCandidateGroups() {
		return super.getCandidateGroups();
	}

	@Override
	public FormDefinition getForm() {
		return super.getForm();
	}

	@JsonDeserialize(as = WsFormDefinition.class)
	@Override
	public void setForm(FormDefinition form) {
		super.setForm(form);
	}

	@NotNull
	@Size(min = 1, max = CoreConstants.VALID_NAME_SIZE)
	@Override
	public String getName() {
		return super.getName();
	}

	@Size(min = 1, max = CoreConstants.VALID_DESCRIPTION_SIZE)
	@Override
	public String getDescription() {
		return super.getDescription();
	}

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
