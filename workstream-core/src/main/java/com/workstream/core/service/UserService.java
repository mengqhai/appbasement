package com.workstream.core.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.GroupQuery;
import org.activiti.engine.identity.NativeGroupQuery;
import org.activiti.engine.identity.User;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.workstream.core.CoreConstants;
import com.workstream.core.model.GroupX;
import com.workstream.core.model.Organization;
import com.workstream.core.model.UserX;
import com.workstream.core.persistence.IGroupXDAO;
import com.workstream.core.persistence.IUserXDAO;

@Service
@Transactional(propagation = Propagation.REQUIRED, value = CoreConstants.TX_MANAGER)
public class UserService {

	private final Logger log = LoggerFactory
			.getLogger(OrganizationService.class);

	@Autowired
	private IUserXDAO userDao;

	@Autowired
	private IGroupXDAO groupDao;

	@Autowired
	private IdentityService idService;

	@Autowired
	private ManagementService mgmtService;

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

	/**
	 * Create both the User(Activiti) and the UserX
	 * 
	 * @param email
	 * @param name
	 * @param password
	 */
	public User createUser(String email, String name, String password) {
		User user = idService.newUser(email);
		user.setEmail(email);
		user.setFirstName(name);
		user.setPassword(password);
		idService.saveUser(user);
		log.info("User created in Activiti: {}", user.getId());

		UserX userX = new UserX();
		userX.setUserId(email);
		userDao.persist(userX);
		log.info("UserX created: {}", userX);
		return user;
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

	public void updateUser(String userId, Map<String, ? extends Object> props) {
		User user = getUser(userId);
		try {
			BeanUtils.populate(user, props);
		} catch (Exception e) {
			log.error("Failed to populate the props to user: {}", props, e);
			throw new RuntimeException(e);
		}
		saveUser(user);
	}

	/**
	 * Create both the Group(Activiti) & GroupX
	 * 
	 */
	public Group createGroup(Organization org, String name, String description) {
		GroupX groupX = new GroupX();
		groupX.setOrg(org);
		org.getGroups().add(groupX);
		groupX.setDescription(description);
		groupDao.persist(groupX);

		String groupId = groupX.generateGroupId();
		groupX.setGroupId(groupId);

		Group group = idService.newGroup(groupId);
		group.setName(name);
		group.setType("assignment");
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

	/**
	 * Remove both the Group(Activiti) and GroupX
	 * 
	 * @param id
	 */
	public void removeGroup(GroupX groupX) {
		String groupId = groupX.getGroupId();
		idService.deleteGroup(groupId);
		groupDao.remove(groupX);
	}

	public List<Group> filterGroup(Organization org) {
		// Collection<GroupX> groupXes = filterGroupX(org);
		// GroupQuery q = idService.createGroupQuery();
		NativeGroupQuery nq = idService.createNativeGroupQuery();
		StringBuilder builder = new StringBuilder("select * from ").append(
				mgmtService.getTableName(Group.class)).append(
				" where ID_ like '");
		builder.append(org.getId()).append("|%'");
		log.info("Filtering Activiti groups with where clause: {}", builder);
		return nq.sql(builder.toString()).list();
	}
	
	

}
