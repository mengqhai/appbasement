package com.workstream.core.service.components;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.workstream.core.worflow.simple.def.step.WsChoiceStepsDefinition;
import com.workstream.core.worflow.simple.def.step.WsDelayStepDefinition;
import com.workstream.core.worflow.simple.def.step.WsFeedbackStepDefinition;
import com.workstream.core.worflow.simple.def.step.WsHumanStepDefinition;
import com.workstream.core.worflow.simple.def.step.WsListConditionStepDefinition;
import com.workstream.core.worflow.simple.def.step.WsListStepDefinition;
import com.workstream.core.worflow.simple.def.step.WsParallelStepsDefinition;
import com.workstream.core.worflow.simple.def.step.WsScriptStepDefinition;
import com.workstream.core.worflow.simple.form.WsBooleanPropertyDefinition;
import com.workstream.core.worflow.simple.form.WsDatePropertyDefinition;
import com.workstream.core.worflow.simple.form.WsListPropertyDefinition;
import com.workstream.core.worflow.simple.form.WsNumberPropertyDefinition;
import com.workstream.core.worflow.simple.form.WsTextPropertyDefinition;

@Component
public class WsObjectMapper extends ObjectMapper {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3029539117829709475L;

	public WsObjectMapper() {
		super();
		regWorkStreamSubTypes();
	}

	public WsObjectMapper(JsonFactory jf, DefaultSerializerProvider sp,
			DefaultDeserializationContext dc) {
		super(jf, sp, dc);
		regWorkStreamSubTypes();
	}

	public WsObjectMapper(JsonFactory jf) {
		super(jf);
		regWorkStreamSubTypes();
	}

	public WsObjectMapper(ObjectMapper src) {
		super(src);
		regWorkStreamSubTypes();
	}

	protected void regWorkStreamSubTypes() {
		// see SimpleWorkflowJsonConvertor.getObjectMapper()
		// and
		// http://stackoverflow.com/questions/19239413/de-serializing-json-to-polymorphic-object-model-using-spring-and-jsontypeinfo-an

		// Register all property-definition model classes as sub-types,
		// otherwise
		// jackson won't understand the subtype json when deserializing
		registerSubtypes(
				WsListPropertyDefinition.class,
				WsTextPropertyDefinition.class,
				// ReferencePropertyDefinition.class,
				WsDatePropertyDefinition.class,
				WsNumberPropertyDefinition.class,
				WsBooleanPropertyDefinition.class);

		// Register all step-types
//		registerSubtypes(HumanStepDefinition.class,
//				FeedbackStepDefinition.class,
//				ParallelStepsDefinition.class, ChoiceStepsDefinition.class,
//				ListStepDefinition.class,
//				ListConditionStepDefinition.class,
//				ScriptStepDefinition.class, DelayStepDefinition.class);
		registerSubtypes(WsHumanStepDefinition.class,
				WsFeedbackStepDefinition.class,
				WsParallelStepsDefinition.class, WsChoiceStepsDefinition.class,
				WsListStepDefinition.class,
				WsListConditionStepDefinition.class,
				WsScriptStepDefinition.class, WsDelayStepDefinition.class);
	}

}
