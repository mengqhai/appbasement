package com.workstream.rest.controller;

import static com.workstream.rest.RestConstants.TEST_USER_ID_INFO;
import static com.workstream.rest.utils.RestUtils.decodeUserId;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.groups.Default;

import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.apache.commons.io.IOUtils;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.workstream.core.exception.AttempBadStateException;
import com.workstream.core.exception.BadArgumentException;
import com.workstream.core.exception.BytesNotFoundException;
import com.workstream.core.exception.DataPersistException;
import com.workstream.core.exception.ResourceNotFoundException;
import com.workstream.core.model.Subscription;
import com.workstream.core.persistence.binary.BinaryPicture;
import com.workstream.core.service.CoreFacadeService;
import com.workstream.rest.RestConstants;
import com.workstream.rest.exception.BeanValidationException;
import com.workstream.rest.exception.NotAuthorizedException;
import com.workstream.rest.model.GroupResponse;
import com.workstream.rest.model.InnerWrapperObj;
import com.workstream.rest.model.SingleValueResponse;
import com.workstream.rest.model.SubscriptionResponse;
import com.workstream.rest.model.UserRequest;
import com.workstream.rest.model.UserResponse;
import com.workstream.rest.validation.ValidateOnCreate;
import com.workstream.rest.validation.ValidateOnUpdate;

@Api(value = "users", description = "User related operations", position = 2)
@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

	private final static Logger log = LoggerFactory
			.getLogger(UserController.class);

	@Autowired
	private CoreFacadeService core;

	@ApiOperation(value = "Create a user, e.g. user registration", notes = "In the request body, the following fields are required:<br/>"
			+ "<ul><li>*id -- the user id for the created user, doesn't have to be the email.</li>"
			+ "<li>*firstName -- the first name of the user, and will be used as the display label of the user</li>"
			+ "<li>*email -- the email address of the user</li>"
			+ "<li>*password -- the password of the accoutn</li>"
			+ "</ul><br/>"
			+ " And remember to access the captcha image before invoke this operation.")
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public UserResponse createUser(
			@ApiParam(required = true) @RequestBody @Validated({ Default.class,
					ValidateOnCreate.class }) UserRequest uReq,
			BindingResult bResult,
			@RequestParam(required = true) String captcha,
			@ApiIgnore HttpSession session) {
		if (bResult.hasErrors()) {
			throw new BeanValidationException(bResult);
		}

		if (captcha == null || captcha.equals("")) {
			throw new BadArgumentException("No captcha");
		}
		String token = (String) session
				.getAttribute(RestConstants.CAPTCHA_TOKEN);
		if (token == null) {
			throw new AttempBadStateException("Captcha not ready");
		}
		if (!captcha.equalsIgnoreCase(token)) {
			throw new BadArgumentException("Wrong captcha");
		}
		// the user id must be globally unique
		User user = core.getUserService().createUser(uReq.getId(),
				uReq.getEmail(), uReq.getFirstName(), uReq.getPassword());
		UserResponse resp = new UserResponse(user);
		session.removeAttribute(RestConstants.CAPTCHA_TOKEN);
		return resp;
	}

	@ApiOperation(value = "Update the current user", notes = "The following properties can be updated: "
			+ "<ul><li>firstName</li>"
			+ "<li>lastName</li>"
			+ "<li>password</li>"
			+ "<li>email</li>"
			+ "</ul>"
			+ "Note: id is not not updatable and will be ignored")
	@RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
	@PreAuthorize("principal == decodeUserId(#userIdBase64)")
	public void updateUser(
			@PathVariable("id") String userIdBase64,
			@ApiParam(required = true) @RequestBody(required = true) @Validated({
					Default.class, ValidateOnUpdate.class }) UserRequest uReq,
			BindingResult bResult) throws ResourceNotFoundException {
		if (bResult.hasErrors()) {
			throw new BeanValidationException(bResult);
		}

		String userId = decodeUserId(userIdBase64);
		if (!userId.equals(core.getAuthUserId())) {
			throw new NotAuthorizedException(
					"You are not allowed to modify other users");
		}
		if (uReq.getPropMap().containsKey(UserRequest.ID)) {
			uReq.getPropMap().remove(UserRequest.ID); // ignore the id
		}
		core.getUserService().updateUser(userId, uReq.getPropMap());
	}

	@ApiOperation(value = "Get the user object for the given user id (base64 encoded)", notes = TEST_USER_ID_INFO)
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	public UserResponse getUser(@PathVariable("id") String userIdBase64) {
		log.info("Activiti user logged in: {}",
				org.activiti.engine.impl.identity.Authentication
						.getAuthenticatedUserId());
		// the id field in the url must be encoded in base64
		// browsers can natively encode/decode the id string btoa(idString)
		// atob(stringToDecode)
		String userId = decodeUserId(userIdBase64);
		User user = core.getUserService().getUser(userId);
		if (user == null) {
			throw new ResourceNotFoundException("No such user.");
		}
		UserResponse resp = new UserResponse(user);
		return resp;
	}

	@ApiOperation(value = "Get the group list(no description) for a given user", notes = "Returns the non-detailed information of all the groups "
			+ "that the given user belongs to.  " + TEST_USER_ID_INFO)
	@RequestMapping(method = RequestMethod.GET, value = "/{id}/groups")
	public List<GroupResponse> getUserGroups(
			@PathVariable("id") String userIdBase64) {
		String userId = decodeUserId(userIdBase64);
		// User user = uSer.getUser(userId);
		// if (user == null) {
		// throw new ResourceNotFoundException("No such user.");
		// }
		List<Group> groups = core.getUserService().filterGroupByUser(userId);
		List<GroupResponse> respList = InnerWrapperObj.valueOf(groups,
				GroupResponse.class);
		return respList;
	}

	@ApiOperation(value = "Count groups for a given user", notes = TEST_USER_ID_INFO)
	@RequestMapping(method = RequestMethod.GET, value = "/{id}/groups/_count")
	public SingleValueResponse countUserGroups(
			@PathVariable("id") String userIdBase64) {
		String userId = decodeUserId(userIdBase64);
		long count = core.getUserService().countGroupByUser(userId);
		return new SingleValueResponse(count);
	}

	@ApiOperation(value = "Upload the picture for the current user.", notes = "The user id must be the current users."
			+ TEST_USER_ID_INFO)
	@RequestMapping(value = "/{id}/picture", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("principal == decodeUserId(#userIdBase64)")
	public void setUserPicture(@PathVariable("id") String userIdBase64,
			@ApiParam(required = true) @RequestBody MultipartFile file) {
		String userId = decodeUserId(userIdBase64);
		if (!userId.equals(core.getAuthUserId())) {
			throw new NotAuthorizedException(
					"You are not allowed to modify others' picture");
		}
		if (!file.isEmpty()) {
			String contentType = file.getContentType();
			log.info("File recieved name={} size={} content-type={}",
					file.getName(), file.getSize(), contentType);
			if (!MediaType.IMAGE_JPEG_VALUE.equalsIgnoreCase(contentType)
					&& !MediaType.IMAGE_PNG_VALUE.equalsIgnoreCase(contentType)) {
				log.warn("wrong content type {}", contentType);
				throw new BadArgumentException(
						"Only jpg/png pictures are supported");
			}

			try {
				core.getUserService().setUserPicture(userId,
						file.getContentType(), file.getInputStream(), true);
			} catch (IOException e) {
				throw new DataPersistException(e.getMessage(), e);
			}
		}

	}

	@ApiOperation(value = "Load picture for a user.", notes = TEST_USER_ID_INFO)
	@RequestMapping(value = "/{id}/picture", method = RequestMethod.GET, produces = {
			MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public void getUserPicture(@PathVariable("id") String userIdBase64,
			@ApiIgnore HttpServletResponse response)
			throws BadArgumentException {
		String userId = decodeUserId(userIdBase64);
		BinaryPicture pic = core.getUserService().getUserPicture(userId);
		if (pic == null) {
			throw new BytesNotFoundException("User picture not set");
		}

		response.setContentType(pic.getMimeType());
		try {
			IOUtils.copy(pic.getInputStream(), response.getOutputStream());
		} catch (IOException e) {
			throw new BytesNotFoundException(e.getMessage(), e);
		}
	}

	@ApiOperation(value = "Set user info for a user", notes = "To delete an info entry, just set the value to null.  "
			+ TEST_USER_ID_INFO)
	@RequestMapping(value = "/{id}/info", method = RequestMethod.PATCH, consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("principal == decodeUserId(#userIdBase64)")
	public void setUserInfo(@PathVariable("id") String userIdBase64,
			@ApiParam(required = true) @RequestBody Map<String, String> userInfo) {
		String userId = decodeUserId(userIdBase64);
		if (!userId.equals(core.getAuthUserId())) {
			throw new NotAuthorizedException(
					"You are not allowed to modify others' info");
		}
		for (String key : userInfo.keySet()) {
			core.getUserService().setUserInfo(userId, key, userInfo.get(key));
		}
	}

	@ApiOperation(value = "Load all user info for a user", notes = TEST_USER_ID_INFO)
	@RequestMapping(value = "/{id}/info", method = RequestMethod.GET)
	public Map<String, String> getUserInfo(
			@PathVariable("id") String userIdBase64)
			throws BadArgumentException {
		String userId = decodeUserId(userIdBase64);
		Map<String, String> info = core.getUserService().getUserInfo(userId);
		return info;
	}

	@ApiOperation(value = "Retrieve subscription list for the a user.", notes = TEST_USER_ID_INFO)
	@RequestMapping(value = "/{id}/subscriptions", method = RequestMethod.GET)
	public Collection<SubscriptionResponse> getUserSubscriptions(
			@PathVariable("id") String userIdBase64)
			throws BadArgumentException {
		String userId = decodeUserId(userIdBase64);
		Collection<Subscription> subs = core.getEventService()
				.filterSubscriptionByUser(userId);
		return InnerWrapperObj.valueOf(subs, SubscriptionResponse.class);
	}

	@ApiOperation(value = "Count subscription for the a user.", notes = TEST_USER_ID_INFO)
	@RequestMapping(value = "/{id}/subscriptions/_count", method = RequestMethod.GET)
	public SingleValueResponse countUserSubscriptions(
			@PathVariable("id") String userIdBase64)
			throws BadArgumentException {
		String userId = decodeUserId(userIdBase64);
		long count = core.getEventService().countSubscriptionByUser(userId);
		return new SingleValueResponse(count);
	}

}
