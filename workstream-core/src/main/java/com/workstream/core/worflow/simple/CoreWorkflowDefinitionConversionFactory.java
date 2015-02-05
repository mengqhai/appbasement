package com.workstream.core.worflow.simple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.activiti.workflow.simple.converter.WorkflowDefinitionConversionFactory;
import org.activiti.workflow.simple.converter.listener.WorkflowDefinitionConversionListener;
import org.activiti.workflow.simple.converter.step.ChoiceStepsDefinitionConverter;
import org.activiti.workflow.simple.converter.step.DelayStepDefinitionConverter;
import org.activiti.workflow.simple.converter.step.FeedbackStepDefinitionConverter;
import org.activiti.workflow.simple.converter.step.ParallelStepsDefinitionConverter;
import org.activiti.workflow.simple.converter.step.ScriptStepDefinitionConverter;
import org.activiti.workflow.simple.converter.step.StepDefinitionConverter;
import org.activiti.workflow.simple.definition.ChoiceStepsDefinition;
import org.activiti.workflow.simple.definition.DelayStepDefinition;
import org.activiti.workflow.simple.definition.FeedbackStepDefinition;
import org.activiti.workflow.simple.definition.HumanStepDefinition;
import org.activiti.workflow.simple.definition.ParallelStepsDefinition;
import org.activiti.workflow.simple.definition.ScriptStepDefinition;

import com.workstream.core.worflow.simple.converter.CoreHumanStepDefinitionConverter;
import com.workstream.core.worflow.simple.def.step.WsChoiceStepsDefinition;
import com.workstream.core.worflow.simple.def.step.WsDelayStepDefinition;
import com.workstream.core.worflow.simple.def.step.WsFeedbackStepDefinition;
import com.workstream.core.worflow.simple.def.step.WsHumanStepDefinition;
import com.workstream.core.worflow.simple.def.step.WsParallelStepsDefinition;
import com.workstream.core.worflow.simple.def.step.WsScriptStepDefinition;

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

		if (this.stepConverters == null) {
			this.stepConverters = new HashMap<>();
		}
		stepConverters.put(WsHumanStepDefinition.class,
				defaultStepConverters.get(HumanStepDefinition.class));
		stepConverters.put(WsParallelStepsDefinition.class,
				defaultStepConverters.get(ParallelStepsDefinition.class));
		stepConverters.put(WsChoiceStepsDefinition.class,
				defaultStepConverters.get(ChoiceStepsDefinition.class));
		stepConverters.put(WsFeedbackStepDefinition.class,
				defaultStepConverters.get(FeedbackStepDefinition.class));
		stepConverters.put(WsScriptStepDefinition.class,
				defaultStepConverters.get(ScriptStepDefinition.class));
		stepConverters.put(WsDelayStepDefinition.class,
				defaultStepConverters.get(DelayStepDefinition.class));
	}

	@Override
	protected void initDefaultWorkflowDefinitionConversionListeners() {
		List<WorkflowDefinitionConversionListener> listeners = new ArrayList<WorkflowDefinitionConversionListener>();
		CoreWorkflowDefinitionConversionListener listener = new CoreWorkflowDefinitionConversionListener();
		listeners.add(listener);
		setDefaultWorkflowDefinitionConversionListeners(listeners);
	}

}
