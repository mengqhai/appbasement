package com.workstream.core.service.components;

import org.activiti.workflow.simple.converter.json.SimpleWorkflowJsonConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WsSimpleWorkflowJsonConverter extends SimpleWorkflowJsonConverter {

	@Autowired
	public void setObjectMapper(WsObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

}
