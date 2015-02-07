package com.workstream.rest.controller;

import static com.workstream.rest.RestConstants.TEST_USER_ID_INFO;
import static com.workstream.rest.utils.RestUtils.decodeUserId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.validation.groups.Default;

import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
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
import com.workstream.core.service.UserService.GroupType;
import com.workstream.rest.exception.BeanValidationException;
import com.workstream.rest.model.GroupRequest;
import com.workstream.rest.model.GroupResponse;
import com.workstream.rest.model.InnerWrapperObj;
import com.workstream.rest.model.ModelRequest;
import com.workstream.rest.model.ModelResponse;
import com.workstream.rest.model.OrgRequest;
import com.workstream.rest.model.OrgResponse;
import com.workstream.rest.model.ProcessResponse;
import com.workstream.rest.model.ProjectRequest;
import com.workstream.rest.model.ProjectResponse;
import com.workstream.rest.model.SingleValueResponse;
import com.workstream.rest.model.TemplateResponse;
import com.workstream.rest.model.UserResponse;
import com.workstream.rest.validation.ValidateOnCreate;
import com.workstream.rest.validation.ValidateOnUpdate;

@Api(value = "orgs", description = "Organization related operations")
@RestController
@RequestMapping(value = "/orgs", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrgController {

	private final static Logger log = LoggerFactory
			.getLogger(OrgController.class);

	@Autowired
	private CoreFacadeService core;

	@Autowired
	private RestLoginController login;

	@ApiOperation(value = "Create a new organization", notes = "The name field is required.<br/>"
			+ "About identifier: If identifier is not specified, the created organization will be automatically assigned an"
			+ " identifier which is globally unique.  It's the name of the organization by default, but if are existing ones in the system, suffix will"
			+ " be appended to guarantee its uniqueness.")
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public OrgResponse createOrg(@RequestBody(required = true) @Validated({
			Default.class, ValidateOnCreate.class }) OrgRequest orgReq,
			BindingResult bResult) throws AuthenticationNotSetException {
		if (bResult.hasErrors()) {
			throw new BeanValidationException(bResult);
		}

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

		// Update Authentication because the user has currently logged in
		String userId = core.getAuthUserId();
		List<Group> groups = new ArrayList<Group>(2);
		groups.addAll(core.getUserService().filterGroupByUser(userId,
				GroupType.PROCESS_DESIGNER, org.getId()));
		groups.addAll(core.getUserService().filterGroupByUser(userId,
				GroupType.ADMIN, org.getId()));
		login.addGroupsToAuthentication(userId, groups);
		return res;
	}

	@ApiOperation(value = "Get my organizations", notes = "Only returns the organizations that the current user has joined")
	@RequestMapping(method = RequestMethod.GET)
	public List<OrgResponse> getMyOrgs() throws AuthenticationNotSetException {
		UserX userX = core.getAuthUserX();
		Collection<Organization> orgList = core.getOrgService()
				.filterOrg(userX);
		List<OrgResponse> resultList = InnerWrapperObj.valueOf(orgList,
				OrgResponse.class);
		return resultList;
	}

	@ApiOperation(value = "Get an organization by id")
	@RequestMapping(value = "/{id:\\d+}", method = RequestMethod.GET)
	public OrgResponse getOrg(@PathVariable("id") Long orgId) {
		Organization org = core.getOrg(orgId);
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
	@PreAuthorize("isAuthAdmin(#orgId)")
	public void updateOrg(@PathVariable("id") Long orgId,
			@ApiParam(required = true) @RequestBody @Validated({ Default.class,
					ValidateOnUpdate.class }) OrgRequest orgReq,
			BindingResult bResult) {
		if (bResult.hasErrors()) {
			throw new BeanValidationException(bResult);
		}
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
	@PreAuthorize("isAuthInOrg(#orgId)")
	public List<GroupResponse> getUserGroups(@PathVariable("id") Long orgId) {
		List<Group> groups = core.getUserService().filterGroup(orgId);
		List<GroupResponse> respList = InnerWrapperObj.valueOf(groups,
				GroupResponse.class);
		return respList;
	}

	@ApiOperation(value = "Create a group in the given organization", notes = "Name field is required")
	@RequestMapping(method = RequestMethod.POST, value = "/{id:\\d+}/groups")
	@PreAuthorize("isAuthAdmin(#orgId)")
	public GroupResponse createGroupInOrg(@PathVariable("id") Long orgId,
			@ApiParam(required = true) @RequestBody @Validated({ Default.class,
					ValidateOnCreate.class }) GroupRequest groupReq,
			BindingResult bResult) {
		if (bResult.hasErrors()) {
			throw new BeanValidationException(bResult);
		}
		Group group = core.createGroupInOrg(orgId, groupReq.getName(),
				groupReq.getDescription());
		GroupResponse resp = new GroupResponse(group);
		return resp;
	}

	@ApiOperation(value = "Get all the users in the given organization")
	@RequestMapping(method = RequestMethod.GET, value = "/{id:\\d+}/users")
	@PreAuthorize("isAuthInOrg(#orgId)")
	public List<UserResponse> getUsersInOrg(@PathVariable("id") Long orgId) {
		List<User> users = core.filterUserByOrgId(orgId);
		List<UserResponse> respList = InnerWrapperObj.valueOf(users,
				UserResponse.class);
		return respList;
	}

	@ApiOperation(value = "Count all the users in the given organization")
	@RequestMapping(method = RequestMethod.GET, value = "/{id:\\d+}/users/_count")
	@PreAuthorize("isAuthInOrg(#orgId)")
	public SingleValueResponse countUsersInOrg(@PathVariable("id") Long orgId) {
		long count = core.countUserByOrgId(orgId);
		return new SingleValueResponse(count);
	}

	@ApiOperation(value = "Request to join an organization", notes = "A approval task will be created for the admin group of the target org")
	@RequestMapping(method = RequestMethod.PUT, value = "/{id:\\d+}/users")
	public void requestUserJoinOrg(@PathVariable("id") Long orgId)
			throws AuthenticationNotSetException, AttempBadStateException {
		core.requestUserJoinOrg(orgId);
	}

	@ApiOperation(value = "Get groups of the user in the given organization", notes = TEST_USER_ID_INFO)
	@RequestMapping(method = RequestMethod.GET, value = "/{orgId:\\d+}/users/{userIdBase64}/groups")
	@PreAuthorize("isAuthInOrg(#orgId)")
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
			for (Group orgGroup : orgGroups) {
				if (userGroup.getId().equals(orgGroup.getId())) {
					respList.add(new GroupResponse(userGroup));
					break;
				}
			}
		}
		return respList;
	}

	@ApiOperation(value = "Get project list of in the given organization")
	@RequestMapping(method = RequestMethod.GET, value = "/{orgId:\\d+}/projects")
	@PreAuthorize("isAuthInOrg(#orgId)")
	public List<ProjectResponse> getProjectsInOrg(
			@PathVariable("orgId") Long orgId,
			@RequestParam(defaultValue = "0") int first,
			@RequestParam(defaultValue = "10") int max) {
		Collection<Project> projects = core.filterProjectByOrgId(orgId, first,
				max);
		List<ProjectResponse> respList = InnerWrapperObj.valueOf(projects,
				ProjectResponse.class);
		return respList;
	}

	@ApiOperation(value = "Get project list of in the given organization")
	@RequestMapping(method = RequestMethod.GET, value = "/{orgId:\\d+}/projects/_count")
	@PreAuthorize("isAuthInOrg(#orgId)")
	public SingleValueResponse countProjectsInOrg(
			@PathVariable("orgId") Long orgId) {
		Long count = core.countProjectByOrgId(orgId);
		return new SingleValueResponse(count);
	}

	@ApiOperation(value = "Create a project in the organization")
	@RequestMapping(method = RequestMethod.POST, value = "/{orgId:\\d+}/projects", consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("isAuthAdmin(#orgId)")
	public ProjectResponse createProjectInOrg(
			@PathVariable("orgId") Long orgId,
			@ApiParam(required = true) @RequestBody @Validated({ Default.class,
					ValidateOnCreate.class }) ProjectRequest projectReq,
			BindingResult bResult) {
		if (bResult.hasErrors()) {
			throw new BeanValidationException(bResult);
		}

		Project proj = core.createProjectInOrg(orgId, projectReq.getName(),
				projectReq.getStartTime(), projectReq.getDueTime(),
				projectReq.getDescription());
		return new ProjectResponse(proj);
	}

	@ApiOperation(value = "Get process template model list in the given organization")
	@RequestMapping(method = RequestMethod.GET, value = "/{orgId:\\d+}/templatemodels")
	@PreAuthorize("isAuthProcessDesigner(#orgId)")
	public List<ModelResponse> getModelsInOrg(
			@PathVariable("orgId") Long orgId,
			@RequestParam(defaultValue = "0") int first,
			@RequestParam(defaultValue = "10") int max) {
		// core.getOrg(orgId); // check org existence
		List<Model> models = core.getTemplateService().filterModel(orgId,
				first, max);
		return InnerWrapperObj.valueOf(models, ModelResponse.class);
	}

	@ApiOperation(value = "Count the process template models in the given organization")
	@RequestMapping(method = RequestMethod.GET, value = "/{orgId:\\d+}/templatemodels/_count")
	@PreAuthorize("isAuthProcessDesigner(#orgId)")
	public SingleValueResponse countModelsInOrg(
			@PathVariable("orgId") Long orgId) {
		long count = core.getTemplateService().countModel(orgId);
		return new SingleValueResponse(count);
	}

	@ApiOperation(value = "Create an empty model in the organization", notes = "name field is required")
	@RequestMapping(method = RequestMethod.POST, value = "/{orgId:\\d+}/templatemodels")
	@PreAuthorize("isAuthProcessDesigner(#orgId)")
	public ModelResponse createModelInOrg(@PathVariable("orgId") Long orgId,
			@ApiParam(required = true) @RequestBody @Validated({ Default.class,
					ValidateOnCreate.class }) ModelRequest modelReq,
			BindingResult bResult) {
		if (bResult.hasErrors()) {
			throw new BeanValidationException(bResult);
		}

		core.getOrg(orgId); // check org existence
		Model model = core.getTemplateService().createModel(orgId,
				modelReq.getName());
		return new ModelResponse(model);
	}

	@ApiOperation(value = "Retrieve the process template list in the organization")
	@RequestMapping(method = RequestMethod.GET, value = "/{orgId:\\d+}/templates")
	@PreAuthorize("isAuthInOrg(#orgId)")
	public List<TemplateResponse> getProcessTemplatesInOrg(
			@PathVariable("orgId") Long orgId,
			@RequestParam(required = false) boolean onlyLatest,
			@RequestParam(defaultValue = "0") int first,
			@RequestParam(defaultValue = "10") int max) {
		// core.getOrg(orgId); // check org existence
		List<ProcessDefinition> pd = core.getTemplateService()
				.filterProcessTemplate(orgId, onlyLatest, first, max);
		List<TemplateResponse> respList = InnerWrapperObj.valueOf(pd,
				TemplateResponse.class);
		return respList;
	}

	@ApiOperation(value = "Count the process template count in the organization")
	@RequestMapping(method = RequestMethod.GET, value = "/{orgId:\\d+}/templates/_count")
	@PreAuthorize("isAuthInOrg(#orgId)")
	public SingleValueResponse countProcessTemplatesInOrg(
			@PathVariable("orgId") Long orgId,
			@RequestParam(required = false) boolean onlyLatest) {
		long count = core.getTemplateService().countProcessTemplate(orgId,
				onlyLatest);
		return new SingleValueResponse(count);
	}

	@ApiOperation(value = "Retrieve the running process list in the organization")
	@RequestMapping(method = RequestMethod.GET, value = "/{orgId:\\d+}/processes")
	@PreAuthorize("isAuthInOrg(#orgId)")
	public List<ProcessResponse> getProcessesInOrg(
			@PathVariable("orgId") Long orgId,
			@RequestParam(defaultValue = "0") int first,
			@RequestParam(defaultValue = "10") int max) {
		List<ProcessInstance> piList = core.getProcessService().filterProcess(
				orgId, first, max);
		return InnerWrapperObj.valueOf(piList, ProcessResponse.class);
	}

	@ApiOperation(value = "Count the running process in the organization")
	@RequestMapping(method = RequestMethod.GET, value = "/{orgId:\\d+}/processes/_count")
	@PreAuthorize("isAuthInOrg(#orgId)")
	public SingleValueResponse countProcessesInOrg(
			@PathVariable("orgId") Long orgId) {
		long count = core.getProcessService().countProcess(orgId);
		return new SingleValueResponse(count);
	}
}
