package com.appbasement.service.user;

import java.util.List;

import com.appbasement.model.User;

public interface IAppUserService {

	public abstract List<User> getAllUsers();

	public abstract void saveUser(User user);

	public abstract User getUserById(Long id);

}
