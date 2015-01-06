package com.workstream.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.workstream.core.model.Organization;
import com.workstream.core.model.UserX;
import com.workstream.core.service.CoreFacadeService;
import com.workstream.rest.model.OrgRequest;
import com.workstream.rest.model.OrgResponse;

@Api(value = "/org", description = "Organization related operations")
@RestController
@RequestMapping(value = "/org", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrgController {

	private final static Logger log = LoggerFactory
			.getLogger(OrgController.class);

	@Autowired
	private CoreFacadeService core;

	@ApiOperation(value = "Create a new organization", notes = "The name field is required.<br/>"
			+ "About identifier: If identifier is not specified, the created organization will be automatically assigned an"
			+ " identifier which is globally unique.  It's the name of the organization by default, but if are existing ones in the system, suffix will"
			+ " be appended to guarantee its uniqueness.")
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public OrgResponse createOrg(@RequestBody OrgRequest orgReq) {
		String identifier = orgReq.getIdentifier();
		String name = orgReq.getName();
		if (identifier == null || identifier.equals("")) {
			identifier = name;
		}

		UserX userX = core.getAuthUserX();
		Organization org = core.createInitOrg(userX, orgReq.getName(),
				identifier, orgReq.getDescription());
		OrgResponse res = new OrgResponse(org);
		log.info("Org created: {} - {}", org.getName(), org.getId());
		return res;
	}

}
