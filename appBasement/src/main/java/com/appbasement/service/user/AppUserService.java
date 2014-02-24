package com.appbasement.service.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

}
