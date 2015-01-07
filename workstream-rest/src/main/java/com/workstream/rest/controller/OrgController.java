package com.workstream.rest.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.activiti.engine.identity.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.workstream.core.exception.AuthenticationNotSetException;
import com.workstream.core.model.Organization;
import com.workstream.core.model.UserX;
import com.workstream.core.service.CoreFacadeService;
import com.workstream.rest.model.GroupResponse;
import com.workstream.rest.model.OrgRequest;
import com.workstream.rest.model.OrgResponse;

@Api(value = "orgs", description = "Organization related operations")
@RestController
@RequestMapping(value = "/orgs", produces = MediaType.APPLICATION_JSON_VALUE)
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
	public OrgResponse createOrg(@RequestBody OrgRequest orgReq)
			throws AuthenticationNotSetException {
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

	@ApiOperation(value = "Get my organizations", notes = "Only returns the organizations that the current user has joined")
	@RequestMapping(method = RequestMethod.GET)
	public List<OrgResponse> getMyOrgs() throws AuthenticationNotSetException {
		UserX userX = core.getAuthUserX();
		Collection<Organization> orgList = core.getOrgService()
				.filterOrg(userX);
		List<OrgResponse> resultList = new ArrayList<OrgResponse>();
		for (Organization org : orgList) {
			resultList.add(new OrgResponse(org));
		}
		return resultList;
	}

	@ApiOperation(value = "Get an organization by id")
	@RequestMapping(value = "/{id:\\d+}", method = RequestMethod.GET)
	public OrgResponse getOrg(@PathVariable("id") Long orgId) {
		Organization org = core.getOrgService().findOrgById(orgId);
		return new OrgResponse(org);
	}

	@ApiOperation(value = "Get an organization by identifier", notes = "The identifier must be provided as a query param.", position = 0)
	@RequestMapping(value = "/byIdentifier", method = RequestMethod.GET)
	public OrgResponse getOrg(@RequestParam("identifier") String identifier) {
		Organization org = core.getOrgService().findOrgByIdentifier(identifier);
		return new OrgResponse(org);
	}

	@ApiOperation(value = "Partially update the organization", notes = "The id field in the request doesn't matter.  And if needs to update"
			+ " the identifier, system automatically guarantees its uniqueness.")
	@RequestMapping(value = "/{id:\\d+}", method = RequestMethod.PATCH)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateOrg(@PathVariable("id") Long orgId,
			@ApiParam(required = true) @RequestBody OrgRequest orgReq) {
		Map<String, Object> props = orgReq.getPropMap();
		if (props.isEmpty()) {
			return;
		}
		core.getOrgService().updateOrg(orgId, props);
		log.info("Updated org {} ", orgId);
	}

	@ApiOperation(value = "Get the group list(no description) of a give organization", notes = "Returns the non-detailed information of all the groups "
			+ "that belong to the given organization.")
	@RequestMapping(method = RequestMethod.GET, value = "/{id:\\d+}/groups")
	public List<GroupResponse> getUserGroups(@PathVariable("id") Long orgId) {
		List<Group> groups = core.getUserService().filterGroup(orgId);
		List<GroupResponse> respList = new ArrayList<GroupResponse>(
				groups.size());
		for (Group group : groups) {
			respList.add(new GroupResponse(group));
		}
		return respList;
	}

}
