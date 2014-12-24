package com.workstream.core.service;


import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.identity.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.workstream.core.conf.ApplicationConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationConfiguration.class)
public class UserServiceTest {
	@Autowired
	UserService service;
	
	String userId = "mqhnow1@sina.com";
	
	@Before
	public void before() {
		service.removeUser(userId);
	}

	@Test
	public void testCreateUser() {
		service.createUser(userId, "孟庆华", "passw0rd");
		User user  = service.getUser(userId);
		Assert.assertEquals(userId, user.getEmail());
		Assert.assertEquals("passw0rd", user.getPassword());
		Assert.assertEquals("孟庆华", user.getFirstName());
	}
	
	@Test
	public void testUpdateUser() {
		service.deleteUserX(userId);
		service.createUser(userId, "孟庆华", "passw0rd");
		Map<String, String> props = new HashMap<String, String>();
		props.put("firstName", "Mikkie");
		props.put("password", "Welcome1");
		service.updateUser(userId, props);
		
		User user = service.getUser(userId);
		Assert.assertEquals("Mikkie", user.getFirstName());
		Assert.assertEquals("Welcome1", user.getPassword());
	}

}
