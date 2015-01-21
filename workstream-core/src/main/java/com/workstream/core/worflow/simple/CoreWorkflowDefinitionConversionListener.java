package com.workstream.core.worflow.simple;

import java.util.Collection;
import java.util.List;

import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.FormProperty;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.StartEvent;
import org.activiti.workflow.simple.converter.WorkflowDefinitionConversion;
import org.activiti.workflow.simple.converter.listener.DefaultWorkflowDefinitionConversionListener;
import org.activiti.workflow.simple.definition.WorkflowDefinition;

public class CoreWorkflowDefinitionConversionListener extends
		DefaultWorkflowDefinitionConversionListener {

	@Override
	protected void initializeProcess(WorkflowDefinitionConversion conversion) {
		super.initializeProcess(conversion);
		WorkflowDefinition workflowDefinition = conversion
				.getWorkflowDefinition();
		if (workflowDefinition.getStartFormDefinition() != null) {
			Process process = conversion.getProcess();
			Collection<FlowElement> elements = process.getFlowElements();
			for (FlowElement ele : elements) {
				if (ele instanceof StartEvent) {
					StartEvent start = (StartEvent) ele;
					List<FormProperty> formProperties = CoreHumanStepDefinitionConverter
							.convertFormProperties(workflowDefinition
									.getStartFormDefinition());
					start.setFormProperties(formProperties);
				}
			}
		}
	}

}
