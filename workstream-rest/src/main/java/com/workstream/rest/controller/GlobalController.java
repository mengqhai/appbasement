package com.workstream.rest.controller;

import java.util.TimeZone;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.Api;
import com.workstream.rest.model.SingleValueResponse;

@Api(value = "global", description = "Global information")
@RestController
@RequestMapping(value = "/global", produces = MediaType.APPLICATION_JSON_VALUE)
public class GlobalController {

	@RequestMapping(value = "/timezones", method = RequestMethod.GET)
	public String[] getTimezoneList() {
		return TimeZone.getAvailableIDs();
	}

	@RequestMapping(value = "/timezones/default", method = RequestMethod.GET)
	public SingleValueResponse getServerTimezone() {
		return new SingleValueResponse(TimeZone.getDefault().getID());
	}

}
