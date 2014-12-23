package com.workstream.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.workstream.core.persistence.IUserXDAO;

@Service
@Transactional
public class UserService {

	@Autowired
	private IUserXDAO userDao;

	public void createUser(String name, String description) {
		
	}

}
