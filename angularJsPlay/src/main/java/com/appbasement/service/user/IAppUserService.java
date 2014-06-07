package com.appbasement.service.user;

import java.util.List;

import com.appbasement.model.Group;
import com.appbasement.model.User;

public interface IAppUserService {

	public abstract List<User> getAllUsers();

	public abstract void saveUser(User user);

	public abstract User getUserById(Long id);

	public abstract void deleteUserById(Long id);

	public abstract void removeUserFromGroup(Long userId, Long groupId);

	public abstract User getUserWithEagerGroups(Long userId);

	public abstract List<Group> getAllGroups();

	public abstract void addUserToGroup(Long userId, Long... groupIds);

	public abstract void saveGroup(Group group);

	public abstract Group getGroupById(Long id);

	public abstract Group getGroupWithEagerUsers(Long groupId);

	public abstract void deleteGroupById(Long id);

	public abstract boolean isUsernameUnique(String username);

}
