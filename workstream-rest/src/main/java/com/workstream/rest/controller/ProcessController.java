package com.workstream.rest.controller;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.workstream.core.exception.AttempBadStateException;
import com.workstream.core.exception.ResourceNotFoundException;
import com.workstream.core.model.Subscription;
import com.workstream.core.model.CoreEvent.TargetType;
import com.workstream.core.service.CoreFacadeService;
import com.workstream.rest.model.HiProcessResponse;
import com.workstream.rest.model.InnerWrapperObj;
import com.workstream.rest.model.ProcessResponse;
import com.workstream.rest.model.SubscriptionResponse;

@Api(value = "processes", description = "Process related operations")
@RestController
@RequestMapping(value = "/processes", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProcessController {

	@Autowired
	private CoreFacadeService core;

	@ApiOperation(value = "Retrieve a running process")
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ProcessResponse getProcess(@PathVariable("id") String processId) {
		ProcessInstance pi = core.getProcessService().getProcess(processId);
		if (pi == null) {
			throw new ResourceNotFoundException("No such process, archived?");
		}
		return new ProcessResponse(pi);
	}

	@ApiOperation(value = "Retrieve running processes started by the current user", notes = "Note: The returned result is a list of <b>history process objects</b>")
	@RequestMapping(value = "/_startedByMe", method = RequestMethod.GET)
	public List<HiProcessResponse> getProcessesStartedByMe() {
		String userId = core.getAuthUserId();
		List<HistoricProcessInstance> hiList = core.getProcessService()
				.filterHiProcessByStarter(userId, false);
		return InnerWrapperObj.valueOf(hiList, HiProcessResponse.class);
	}

	@ApiOperation(value = "Retrieve all the variables for the process instance", notes = "This is a security hole to be fixed.")
	@RequestMapping(value = "/{id}/vars", method = RequestMethod.GET)
	public Map<String, Object> getProcessVariables(
			@PathVariable("id") String processId) {
		ProcessInstance pi = core.getProcessService().getProcessWithVars(
				processId);
		if (pi == null) {
			throw new ResourceNotFoundException("No such process, archived?");
		}
		return pi.getProcessVariables();
	}

	@ApiOperation(value = "Retrieve subscription list for a process")
	@RequestMapping(value = "/{id}/subscriptions", method = RequestMethod.GET)
	public Collection<SubscriptionResponse> getProcessSubscriptions(
			@PathVariable("id") String processId) {
		Collection<Subscription> subs = core.getEventService()
				.filterSubscription(TargetType.PROCESS, processId);
		return InnerWrapperObj.valueOf(subs, SubscriptionResponse.class);
	}

	/**
	 * 
	 * @param processId
	 * @return
	 * @throws AttempBadStateException
	 *             if user already subscribed it
	 */
	@ApiOperation(value = "Subscribe a process for the current user")
	@RequestMapping(value = "/{id}/subscriptions", method = RequestMethod.POST)
	public SubscriptionResponse subscribeProcess(
			@PathVariable("id") String processId)
			throws AttempBadStateException {
		String userId = core.getAuthUserId();
		Subscription sub = core.getEventService().subscribe(userId,
				TargetType.PROCESS, processId);
		return new SubscriptionResponse(sub);
	}

}
