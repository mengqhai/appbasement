package com.appbasement;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.appbasement.component.ObjectPatcherTest;
import com.appbasement.model.GroupTest;
import com.appbasement.model.TemplateTest;
import com.appbasement.model.UserTest;
import com.appbasement.persistence.GroupJpaDAOTest;
import com.appbasement.persistence.TemplateJpaDAOTest;
import com.appbasement.persistence.UserJpaDAOTest;
import com.appbasement.service.email.EmailServiceTest;
import com.appbasement.service.template.TemplateServiceTest;

@RunWith(Suite.class)
@SuiteClasses({ GroupJpaDAOTest.class, UserJpaDAOTest.class,
		TemplateJpaDAOTest.class, GroupTest.class, TemplateTest.class,
		UserTest.class, EmailServiceTest.class, TemplateServiceTest.class,
		ObjectPatcherTest.class })
public class AppBasementTestSuit {

}
