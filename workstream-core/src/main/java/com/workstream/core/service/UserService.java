package com.workstream.core.service;

import java.util.Map;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.User;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.workstream.core.CoreConstants;
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

	public void createUser(String email, String name, String password) {
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

}
