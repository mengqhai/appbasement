package com.workstream.rest.controller;

import static com.workstream.rest.RestConstants.TEST_USER_ID_INFO;
import static com.workstream.rest.utils.RestUtils.decodeUserId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
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
import com.workstream.core.exception.AttempBadStateException;
import com.workstream.core.exception.AuthenticationNotSetException;
import com.workstream.core.exception.ResourceNotFoundException;
import com.workstream.core.model.Organization;
import com.workstream.core.model.Project;
import com.workstream.core.model.UserX;
import com.workstream.core.service.CoreFacadeService;
import com.workstream.rest.model.GroupRequest;
import com.workstream.rest.model.GroupResponse;
import com.workstream.rest.model.OrgRequest;
import com.workstream.rest.model.OrgResponse;
import com.workstream.rest.model.ProjectRequest;
import com.workstream.rest.model.ProjectResponse;
import com.workstream.rest.model.UserResponse;

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
		if (org == null) {
			throw new ResourceNotFoundException("No such org.");
		}
		return new OrgResponse(org);
	}

	@ApiOperation(value = "Get an organization by identifier", notes = "The identifier must be provided as a query param.", position = 0)
	@RequestMapping(value = "/byIdentifier", method = RequestMethod.GET)
	public OrgResponse getOrg(@RequestParam("identifier") String identifier) {
		Organization org = core.getOrgService().findOrgByIdentifier(identifier);
		if (org == null) {
			throw new ResourceNotFoundException("No such org.");
		}
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

	@ApiOperation(value = "Create a group in the given organization", notes = "Name field is required")
	@RequestMapping(method = RequestMethod.POST, value = "/{id:\\d+}/groups")
	public GroupResponse createGroupInOrg(@PathVariable("id") Long orgId,
			@ApiParam(required = true) @RequestBody GroupRequest groupReq) {
		Group group = core.createGroupInOrg(orgId, groupReq.getName(),
				groupReq.getDescription());
		GroupResponse resp = new GroupResponse(group);
		return resp;
	}

	@ApiOperation(value = "Get all the users in the given organization")
	@RequestMapping(method = RequestMethod.GET, value = "/{id:\\d+}/users")
	public List<UserResponse> getUsersInOrg(@PathVariable("id") Long orgId) {
		List<User> users = core.filterUserByOrgId(orgId);
		List<UserResponse> respList = new ArrayList<UserResponse>(users.size());
		for (User user : users) {
			respList.add(new UserResponse(user));
		}
		return respList;
	}

	@ApiOperation(value = "Request to join an organization", notes = "A approval task will be created for the admin group of the target org")
	@RequestMapping(method = RequestMethod.PUT, value = "/{id:\\d+}/users")
	public void requestUserJoinOrg(@PathVariable("id") Long orgId)
			throws AuthenticationNotSetException, AttempBadStateException {
		core.requestUserJoinOrg(orgId);
	}

	@ApiOperation(value = "Get groups of the user in the given organization", notes = TEST_USER_ID_INFO)
	@RequestMapping(method = RequestMethod.GET, value = "/{orgId:\\d+}/users/{userIdBase64}/groups")
	public List<GroupResponse> getGroupsOfUserInOrg(
			@PathVariable("orgId") Long orgId,
			@PathVariable("userIdBase64") String userIdBase64) {
		String userId = decodeUserId(userIdBase64);
		List<Group> userGroups = core.getUserService()
				.filterGroupByUser(userId);
		List<Group> orgGroups = core.getUserService().filterGroup(orgId);
		List<GroupResponse> respList = new ArrayList<GroupResponse>(
				userGroups.size());
		for (Group userGroup : userGroups) {
			boolean inOrg = false;
			for (Group orgGroup : orgGroups) {
				if (userGroup.getId().equals(orgGroup.getId())) {
					inOrg = true;
					break;
				}
			}
			if (inOrg) {
				respList.add(new GroupResponse(userGroup));
			}
		}
		return respList;
	}

	@ApiOperation(value = "Get project list of in the given organization")
	@RequestMapping(method = RequestMethod.GET, value = "/{orgId:\\d+}/projects")
	public List<ProjectResponse> getProjectsInOrg(
			@PathVariable("orgId") Long orgId) {
		Collection<Project> projects = core.filterProjectByOrgId(orgId);
		List<ProjectResponse> respList = new ArrayList<ProjectResponse>(
				projects.size());
		for (Project pro : projects) {
			respList.add(new ProjectResponse(pro));
		}
		return respList;
	}

	@ApiOperation(value = "Create a project in the organization")
	@RequestMapping(method = RequestMethod.POST, value = "/{orgId:\\d+}/projects", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ProjectResponse createProjectInOrg(
			@PathVariable("orgId") Long orgId,
			@ApiParam(required = true) @RequestBody ProjectRequest projectReq) {
		Project proj = core.createProjectInOrg(orgId, projectReq.getName(),
				projectReq.getStartTime(), projectReq.getDueTime(),
				projectReq.getDescription());
		return new ProjectResponse(proj);
	}

}
