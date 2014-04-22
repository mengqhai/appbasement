package com.appbasement;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.appbasement.model.GroupTest;
import com.appbasement.model.TemplateTest;
import com.appbasement.model.UserTest;
import com.appbasement.persistence.GroupJpaDAOTest;
import com.appbasement.persistence.UserJpaDAOTest;

@RunWith(Suite.class)
@SuiteClasses({ GroupJpaDAOTest.class, UserJpaDAOTest.class, GroupTest.class,
		TemplateTest.class, UserTest.class })
public class AppBasementTestSuit {

}
