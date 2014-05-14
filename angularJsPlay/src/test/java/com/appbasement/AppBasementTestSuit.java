package com.appbasement;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.appbasement.model.GroupTest;
import com.appbasement.persistence.GroupJpaDAOTest;
import com.appbasement.persistence.UserJpaDAOTest;

@RunWith(Suite.class)
@SuiteClasses({ GroupJpaDAOTest.class, UserJpaDAOTest.class, GroupTest.class})
public class AppBasementTestSuit {

}
