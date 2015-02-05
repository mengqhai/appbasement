package com.workstream.core.service.components;

import java.io.InputStream;

import org.activiti.workflow.simple.converter.json.SimpleWorkflowJsonConverter;
import org.activiti.workflow.simple.definition.WorkflowDefinition;
import org.activiti.workflow.simple.exception.SimpleWorkflowException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.workstream.core.worflow.simple.def.WsWorkflowDefinition;

@Component
public class WsSimpleWorkflowJsonConverter extends SimpleWorkflowJsonConverter {

	@Autowired
	public void setObjectMapper(WsObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public WorkflowDefinition readWorkflowDefinition(InputStream inputStream)
			throws SimpleWorkflowException {
		try {
			return getObjectMapper().readValue(inputStream,
					WsWorkflowDefinition.class);
		} catch (Exception e) {
			throw wrapExceptionRead(e);
		}
	}

	@Override
	public WorkflowDefinition readWorkflowDefinition(byte[] bytes)
			throws SimpleWorkflowException {
		try {
			return getObjectMapper().readValue(bytes,
					WsWorkflowDefinition.class);
		} catch (Exception e) {
			throw wrapExceptionRead(e);
		}
	}

}
