package com.workstream.core.worflow.simple;

import java.util.ArrayList;
import java.util.List;

import org.activiti.workflow.simple.converter.WorkflowDefinitionConversionFactory;
import org.activiti.workflow.simple.converter.listener.WorkflowDefinitionConversionListener;
import org.activiti.workflow.simple.converter.step.ChoiceStepsDefinitionConverter;
import org.activiti.workflow.simple.converter.step.DelayStepDefinitionConverter;
import org.activiti.workflow.simple.converter.step.FeedbackStepDefinitionConverter;
import org.activiti.workflow.simple.converter.step.ParallelStepsDefinitionConverter;
import org.activiti.workflow.simple.converter.step.ScriptStepDefinitionConverter;
import org.activiti.workflow.simple.converter.step.StepDefinitionConverter;

public class CoreWorkflowDefinitionConversionFactory extends
		WorkflowDefinitionConversionFactory {

	@Override
	@SuppressWarnings("rawtypes")
	protected void initDefaultStepConverters() {
		List<StepDefinitionConverter> converters = new ArrayList<StepDefinitionConverter>();
		converters.add(new ParallelStepsDefinitionConverter());
		converters.add(new ChoiceStepsDefinitionConverter());
		converters.add(new CoreHumanStepDefinitionConverter()); // customized
		converters.add(new FeedbackStepDefinitionConverter());
		converters.add(new ScriptStepDefinitionConverter());
		converters.add(new DelayStepDefinitionConverter());
		setDefaultStepDefinitionConverters(converters);
	}

	@Override
	protected void initDefaultWorkflowDefinitionConversionListeners() {
		List<WorkflowDefinitionConversionListener> listeners = new ArrayList<WorkflowDefinitionConversionListener>();
		CoreWorkflowDefinitionConversionListener listener = new CoreWorkflowDefinitionConversionListener();
		listeners.add(listener);
		setDefaultWorkflowDefinitionConversionListeners(listeners);
	}

}
