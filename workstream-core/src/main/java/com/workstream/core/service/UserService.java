package com.workstream.core.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.GroupQuery;
import org.activiti.engine.identity.NativeGroupQuery;
import org.activiti.engine.identity.NativeUserQuery;
import org.activiti.engine.identity.Picture;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.workstream.core.CoreConstants;
import com.workstream.core.exception.AttempBadStateException;
import com.workstream.core.exception.BadArgumentException;
import com.workstream.core.exception.BeanPropertyException;
import com.workstream.core.exception.BytesNotFoundException;
import com.workstream.core.exception.DataBadStateException;
import com.workstream.core.exception.DataPersistException;
import com.workstream.core.exception.ResourceNotFoundException;
import com.workstream.core.model.BinaryObj;
import com.workstream.core.model.BinaryObj.BinaryObjType;
import com.workstream.core.model.BinaryObj.BinaryReposType;
import com.workstream.core.model.GroupX;
import com.workstream.core.model.Organization;
import com.workstream.core.model.UserX;
import com.workstream.core.persistence.IBinaryObjDAO;
import com.workstream.core.persistence.IGroupXDAO;
import com.workstream.core.persistence.IOrganizationDAO;
import com.workstream.core.persistence.IUserXDAO;
import com.workstream.core.persistence.binary.BinaryPicture;
import com.workstream.core.utils.ThumbnailCreator;

@Service
@Transactional(propagation = Propagation.REQUIRED, value = CoreConstants.TX_MANAGER)
public class UserService {

	private final Logger log = LoggerFactory
			.getLogger(OrganizationService.class);

	public enum GroupType {
		ASSIGNMENT, PROCESS_DESIGNER, ADMIN
	}

	@Autowired
	private IUserXDAO userDao;

	@Autowired
	private IGroupXDAO groupDao;

	@Autowired
	private IOrganizationDAO orgDao;

	@Autowired
	private IdentityService idService;

	@Autowired
	private ManagementService mgmtService;

	@Autowired
	private IBinaryObjDAO binaryDao;

	private ThumbnailCreator thumbCreator = new ThumbnailCreator();

	protected void deleteUserX(String userId) {
		UserX userX = userDao.findByUserId(userId);
		if (userX != null) {
			userDao.remove(userX);
		}
	}

	/**
	 * Delete both the User(Activiti) and the UserX
	 * 
	 * @param userId
	 */
	public void removeUser(String userId) {
		idService.deleteUser(userId);
		deleteUserX(userId);
	}

	public User createUser(String id, String email, String name, String password) {
		User user = idService.newUser(id);
		user.setEmail(email);
		user.setFirstName(name);
		user.setPassword(password);
		idService.saveUser(user);
		log.info("User created in Activiti: {}", user.getId());

		UserX userX = new UserX();
		userX.setUserId(id);
		userDao.persist(userX);
		log.info("UserX created: {}", userX);
		return user;
	}

	/**
	 * Create both the User(Activiti) and the UserX, with an email address. The
	 * email address is used as user id.
	 * 
	 * @param email
	 * @param name
	 * @param password
	 */
	public User createUser(String email, String name, String password) {
		return createUser(email, email, name, password);
	}

	public boolean isUserIdUnique(String userId) {
		long count = idService.createUserQuery().userId(userId).count();
		return count == 0L;
	}

	public User getUser(String userId) {
		return idService.createUserQuery().userId(userId).singleResult();
	}

	public UserX getUserX(String userId) {
		return userDao.findByUserId(userId);
	}

	public void saveUser(User user) {
		idService.saveUser(user);
	}

	public void updateUser(String userId, Map<String, ? extends Object> props)
			throws BeanPropertyException {
		User user = getUser(userId);
		if (user == null) {
			throw new ResourceNotFoundException("No such user");
		}
		try {
			BeanUtils.populate(user, props);
		} catch (Exception e) {
			log.error("Failed to populate the props to user: {}", props, e);
			throw new BeanPropertyException(e);
		}
		saveUser(user);
	}

	public Collection<UserX> filterUserX(Organization org) {
		org = orgDao.reattachIfNeeded(org, org.getId());
		org.getUsers().size(); // make it eager
		return org.getUsers();
	}

	public List<User> filterUser(Organization org) {
		NativeUserQuery nq = idService.createNativeUserQuery();
		StringBuilder builder = new StringBuilder("select * from ").append(
				mgmtService.getTableName(User.class)).append(" where ID_ in (");
		Collection<UserX> userXes = filterUserX(org);
		int added = 0;
		for (UserX userX : userXes) {
			builder.append("'").append(userX.getUserId()).append("'");
			added++;
			if (added < userXes.size()) {
				builder.append(",");
			}
		}
		builder.append(")");
		log.debug("Filtering Activiti users with where clause: {}", builder);
		nq.sql(builder.toString());
		return nq.list();
	}

	public long countUserX(Organization org) {
		Collection<UserX> userXes = filterUserX(org);
		return userXes.size();
	}

	public List<User> filterUserByGroupId(String groupId) {
		UserQuery q = idService.createUserQuery();
		return q.memberOfGroup(groupId).list();
	}

	public long countUserByGroupId(String groupId) {
		UserQuery q = idService.createUserQuery();
		return q.memberOfGroup(groupId).count();
	}

	public List<User> filterUser(Group group) {
		return filterUserByGroupId(group.getId());
	}

	public List<User> filterUsers(GroupX groupX) {
		return filterUserByGroupId(groupX.getGroupId());
	}

	/**
	 * Create both the Group(Activiti) & GroupX
	 * 
	 */
	public Group createGroup(Organization org, String name, String description) {
		return createGroup(org, name, description, null);
	}

	public Group createGroup(Organization org, String name, String description,
			GroupType type) {
		GroupX groupX = new GroupX();
		groupX.setOrg(org);

		groupX.setDescription(description);
		groupDao.persist(groupX);

		String groupId = groupX.generateGroupId();
		groupX.setGroupId(groupId);
		org.getGroups().add(groupX); // must do this here, to prevent hashCode
										// from changing

		Group group = idService.newGroup(groupId);
		group.setName(name);
		if (type != null) {
			group.setType(type.toString());
		} else {
			group.setType(GroupType.ASSIGNMENT.toString());
		}
		idService.saveGroup(group);
		return group;
	}

	public GroupX getGroupX(String groupId) {
		return groupDao.findByGroupId(groupId);
	}

	public GroupX getGroupX(Long groupXId) {
		return groupDao.findById(groupXId);
	}

	public Group getGroup(GroupX groupX) {
		String groupId = groupX.getGroupId();
		return getGroup(groupId);
	}

	public Group getGroup(String groupId) {
		GroupQuery q = idService.createGroupQuery();
		Group group = q.groupId(groupId).singleResult();
		return group;
	}

	public Collection<GroupX> filterGroupX(Organization org) {
		return groupDao.filterFor(org);
	}

	public Collection<GroupX> filterGroupXByOrgId(Long orgId) {
		Organization org = orgDao.getReference(orgId);
		return filterGroupX(org);
	}

	public void updateGroup(String groupId, Map<String, Object> props)
			throws BeanPropertyException {
		Group group = getGroup(groupId);
		if (group == null) {
			return;
		}
		try {
			BeanUtils.populate(group, props);
		} catch (Exception e) {
			log.error("Failed to populate the props to group: {}", props, e);
			throw new BeanPropertyException(
					"Failed to populate the props to group", e);
		}
		idService.saveGroup(group);
	}

	public void updateGroupX(String groupId, Map<String, Object> props) {
		GroupX groupX = getGroupX(groupId);
		if (groupX == null) {
			return;
		}
		try {
			BeanUtils.populate(groupX, props);
		} catch (Exception e) {
			log.error("Failed to populate the props to groupX: {}", props, e);
			throw new BeanPropertyException(
					"Failed to populate the props to groupX", e);
		}
		log.debug("Successfully update groupX: {}", groupX);
	}

	/**
	 * Remove both the Group(Activiti) and GroupX
	 * 
	 * @param id
	 */
	public void removeGroup(GroupX groupX) {
		groupX = groupDao.reattachIfNeeded(groupX, groupX.getId());
		if (!groupX.getOrg().getGroups().contains(groupX)) {
			throw new IllegalStateException(
					"The group's org doesn't contain the group itself! Have you did any thing that changed the group's hash code?");
		}

		groupX.getOrg().getGroups().remove(groupX); // must, orphanRemoval =
													// true
		String groupId = groupX.getGroupId();
		idService.deleteGroup(groupId);
		groupDao.remove(groupX);
	}

	public List<Group> filterGroup(Long orgId) {
		NativeGroupQuery nq = idService.createNativeGroupQuery();
		StringBuilder builder = new StringBuilder("select * from ").append(
				mgmtService.getTableName(Group.class)).append(
				" where ID_ like '");
		builder.append(orgId).append("|%'");
		log.debug("Filtering Activiti groups with where clause: {}", builder);
		return nq.sql(builder.toString()).list();
	}

	public List<Group> filterGroup(Organization org) {
		// Collection<GroupX> groupXes = filterGroupX(org);
		// GroupQuery q = idService.createGroupQuery();
		return filterGroup(org.getId());
	}

	public List<Group> filterGroupByUser(String userId) {
		return idService.createGroupQuery().groupMember(userId).list();
	}

	public List<Group> filterGroupByUser(String userId, GroupType type) {
		return idService.createGroupQuery().groupMember(userId)
				.groupType(type.toString()).list();
	}

	public List<Group> filterGroupByUser(String userId, GroupType type,
			Long orgId) {
		List<Group> groups = filterGroupByUser(userId, type);
		List<Group> result = new ArrayList<Group>(groups.size());
		for (Group group : groups) {
			if (group.getId().startsWith(orgId + "|")) {
				result.add(group);
			}
		}
		return result;
	}

	public long countGroupByUser(String userId) {
		return idService.createGroupQuery().groupMember(userId).count();
	}

	public boolean isUserInGroup(String userId, String groupId) {
		long groupMatching = idService.createGroupQuery().groupId(groupId)
				.groupMember(userId).count();
		return (groupMatching > 0);
	}

	public void addUserToGroup(UserX userX, GroupX groupX)
			throws DataBadStateException, DataPersistException {
		groupX = groupDao.reattachIfNeeded(groupX, groupX.getId());
		// check if user already in the group
		if (isUserInGroup(userX.getUserId(), groupX.getGroupId())) {
			throw new AttempBadStateException(
					"Unable to add user to group.  User already in the group.");
		}

		// check if the user and group is in the same org
		Collection<Organization> orgs = orgDao.filterByUserX(userX);
		boolean belongsToGroupOrg = false;
		for (Organization org : orgs) {
			if (groupX.getOrg().getId().equals(org.getId())) {
				belongsToGroupOrg = true;
				break;
			}
		}
		if (!belongsToGroupOrg) {
			log.error("User {} doesn't belong to group's org. {}", userX,
					groupX.getId());
			throw new AttempBadStateException(
					"Unable to add user to group.  User doesn't belong to group's org");
		}
		try {
			idService.createMembership(userX.getUserId(), groupX.getGroupId());
		} catch (Exception e) {
			log.error("Failed the persist the group({})-user({}) relationship",
					groupX.getGroupId(), userX.getUserId(), e);
			throw new DataPersistException(
					"Failed the persist the group-user relationship", e);
		}
	}

	public void addUserToGroup(String userId, GroupX groupX) {
		UserX userX = userDao.findByUserId(userId);
		if (userX == null) {
			throw new ResourceNotFoundException("No such user");
		}
		addUserToGroup(userX, groupX);
	}

	public void addUserToGroup(UserX userX, String groupId) {
		GroupX groupX = groupDao.findByGroupId(groupId);
		addUserToGroup(userX, groupX);
	}

	public void addUserToGroup(String userId, String groupId) {
		GroupX groupX = groupDao.findByGroupId(groupId);
		if (groupX == null) {
			throw new ResourceNotFoundException("No such group");
		}
		addUserToGroup(userId, groupX);
	}

	public void removeUserFromGroup(String userId, String groupId) {
		idService.deleteMembership(userId, groupId);
	}

	public void login(String userId) {
		idService.setAuthenticatedUserId(userId);
	}

	public void logout() {
		idService.setAuthenticatedUserId(null);
	}

	public BinaryPicture getUserPicture(String userId) {
		Picture pic = idService.getUserPicture(userId);
		if (pic == null) {
			throw new BytesNotFoundException("No such picture");
		}
		BinaryObj binary = binaryDao.getBinaryObjByTarget(
				BinaryObjType.USER_PICTURE, userId);
		InputStream binaryContent = binaryDao.getContentStream(binary);
		BinaryPicture bPic = new BinaryPicture(pic, binaryContent);
		// decorated Picture with input stream
		return bPic;
	}

	public void setUserPicture(String userId, String mimeType, InputStream is,
			boolean onlySaveThumb) {
		if (!mimeType.startsWith("image")) {
			throw new BadArgumentException("Wrong mine type: " + mimeType);
		}
		Picture oldPicture = idService.getUserPicture(userId);
		if (oldPicture != null) {
			binaryDao.deleteBinaryObjByTarget(BinaryObjType.USER_PICTURE,
					userId);
			// unable to delete the activiti Picture
			// https://github.com/Activiti/Activiti/issues/497
		}
		BinaryObj binary = BinaryObj.newBinaryObj(BinaryObjType.USER_PICTURE,
				userId, BinaryReposType.FILE_SYSTEM_REPOSITORY, mimeType, null);
		if (onlySaveThumb) {
			// optional step
			thumbCreator.setMaxHeight(160);
			thumbCreator.setMaxWidth(160);
			InputStream thumbIs = thumbCreator.createSquareThumbnail(is);
			binaryDao.persistInputStreamToContent(thumbIs, binary);
			mimeType = "image/png";
		} else {
			binaryDao.persistInputStreamToContent(is, binary);
		}

		Long bId = binary.getId();
		byte[] bytes = { bId.byteValue() };
		Picture pic = new Picture(bytes, mimeType);
		idService.setUserPicture(userId, pic);
		// need to do so, to make user.isPictureSet()
	}

	public Map<String, String> getUserInfo(String userId) {
		Map<String, String> result = new HashMap<String, String>();
		List<String> keys = getUserInfoKeys(userId);
		for (String key : keys) {
			result.put(key, getUserInfo(userId, key));
		}
		return result;
	}

	public String getUserInfo(String userId, String key) {
		return idService.getUserInfo(userId, key);
	}

	public void setUserInfo(String userId, String key, String value) {
		if (value != null) {
			idService.setUserInfo(userId, key, value);
		} else {
			idService.deleteUserInfo(userId, key);
		}

	}

	public boolean checkPassword(String userId, String password) {
		return idService.checkPassword(userId, password);
	}

	public List<String> getUserInfoKeys(String userId) {
		return idService.getUserInfoKeys(userId);
	}
}
