package com.angularjsplay.e2e.rest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ProjectRestTest.class, BacklogRestTest.class })
public class ScrumRestTestSuit {
}
