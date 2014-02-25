package com.appbasement.service.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.appbasement.model.Group;
import com.appbasement.model.User;
import com.appbasement.persistence.IGroupDAO;
import com.appbasement.persistence.IUserDAO;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class AppUserService implements IAppUserService {

	@Autowired
	protected IUserDAO userDao;

	@Autowired
	protected IGroupDAO groupDao;

	@Override
	public List<User> getAllUsers() {
		return userDao.findAll();
	}

	@Override
	public void saveUser(User user) {
		if (user.getId() == null) {
			userDao.persist(user);
		} else {
			userDao.merge(user);
		}
	}

	@Override
	public User getUserById(Long id) {
		return userDao.findById(id);
	}

	@Override
	public void deleteUserById(Long id) {
		User userToDelete = userDao.getReference(id);
		userDao.remove(userToDelete);
	}

	@Override
	public void removeUserFromGroup(Long userId, Long groupId) {
		User user = userDao.findById(userId);
		Group group = groupDao.getReference(groupId);
		user.removeFromGroup(group);
	}

	@Override
	public User getUserWithEagerGroups(Long userId) {
		User user = userDao.getUserWithEagerGroups(userId);
		return user;
	}

	@Override
	public List<Group> getAllGroups() {
		return groupDao.findAll();
	}

	@Override
	public void addUserToGroup(Long userId, Long... groupIds) {
		User user = userDao.getUserWithEagerGroups(userId); // to reduce the
															// select statements

		for (Long groupId : groupIds) {
			Group group = groupDao.findById(groupId);
			user.addToGroup(group);
		}
	}

}
