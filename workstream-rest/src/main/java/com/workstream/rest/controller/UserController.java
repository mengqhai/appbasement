package com.workstream.rest.controller;

import static com.workstream.rest.utils.RestUtils.decodeUserId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.Picture;
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
import org.springframework.web.multipart.MultipartFile;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.workstream.core.exception.BadArgumentException;
import com.workstream.core.exception.DataPersistException;
import com.workstream.core.exception.ResourceNotFoundException;
import com.workstream.core.service.CoreFacadeService;
import com.workstream.rest.RestConstants;
import com.workstream.rest.exception.BadStateException;
import com.workstream.rest.exception.BytesNotFoundException;
import com.workstream.rest.exception.NotAuthorizedException;
import com.workstream.rest.model.GroupResponse;
import com.workstream.rest.model.UserRequest;
import com.workstream.rest.model.UserResponse;

@Api(value = "users", description = "User related operations", position = 2)
@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

	private final static Logger log = LoggerFactory
			.getLogger(UserController.class);

	public static final String TEST_USER_ID_INFO = "test user id: <br/> "
			+ "<ul><li>mqhnow1@sina.com: <b>bXFobm93MUBzaW5hLmNvbQ==</b></li> "
			+ "<li>projectTester@sina.com:<b>cHJvamVjdFRlc3RlckBzaW5hLmNvbQ==</b></li>"
			+ "</ul>";

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
			@ApiParam(required = true) @RequestBody UserRequest uReq,
			@RequestParam String captcha, @ApiIgnore HttpSession session) {
		if (captcha == null || captcha.equals("")) {
			throw new BadArgumentException("No captcha");
		}
		String token = (String) session
				.getAttribute(RestConstants.CAPTCHA_TOKEN);
		if (token == null) {
			throw new BadStateException("Captcha not ready");
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
		List<GroupResponse> respList = new ArrayList<GroupResponse>(
				groups.size());
		for (Group group : groups) {
			GroupResponse resp = new GroupResponse(group);
			respList.add(resp);
		}
		return respList;
	}

	@ApiOperation(value = "Upload the picture for the current user.", notes = "The user id must be the current users.")
	@RequestMapping(value = "/{id}/picture", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public void setUserPicture(@PathVariable("id") String userIdBase64,
			@ApiParam(required = true) @RequestBody MultipartFile file) {
		String userId = decodeUserId(userIdBase64);
		if (!userId.equals(core.getAuthUserId())) {
			throw new NotAuthorizedException(
					"You are not allowed to modify others' picture");
		}
		if (!file.isEmpty()) {
			log.info("File recieved name={} size={} content-type={}",
					file.getName(), file.getSize(), file.getContentType());
		}

		try {
			core.getUserService().setUserPicture(userId, file.getContentType(),
					file.getBytes());
		} catch (IOException e) {
			throw new DataPersistException(e.getMessage(), e);
		}
	}

	@ApiOperation(value = "Load picture for the current user.")
	@RequestMapping(value = "/{id}/picture", method = RequestMethod.GET, produces = {
			MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public byte[] getUserPicture(@PathVariable("id") String userIdBase64) {
		String userId = decodeUserId(userIdBase64);
		Picture pic = core.getUserService().getUserPicture(userId);
		if (pic == null || pic.getBytes() == null) {
			throw new BytesNotFoundException("User picture not set");
		}
		return pic.getBytes();
	}

}
