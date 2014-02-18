package com.appbasement.persistence;

import com.appbasement.model.User;

public interface IUserDAO extends IGenericDAO<User, Long> {

	public User findByUsername(String username);

}
