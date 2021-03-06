package com.angularjsplay.persistence.util;

import com.angularjsplay.mvc.rest.ScrumRestConstants;
import com.appbasement.persistence.util.TestConstants;

public interface ScrumTestConstants extends TestConstants {

	public static final String DATA_SET_TASK = DATA_SET_HOME_DIR + "task.xml";

	public static final String DATA_SET_BACKLOG = DATA_SET_HOME_DIR
			+ "backlog.xml";

	public static final String DATA_SET_SPRINT = DATA_SET_HOME_DIR
			+ "sprint.xml";

	public static final String DATA_SET_PROJECT = DATA_SET_HOME_DIR
			+ "project.xml";

	public static final String DATA_SET_SMALL_TASK = DATA_SET_SMALL_DIR
			+ "task.xml";

	public static final String DATA_SET_SMALL_BACKLOG = DATA_SET_SMALL_DIR
			+ "backlog.xml";

	public static final String DATA_SET_SMALL_SPRINT = DATA_SET_SMALL_DIR
			+ "sprint.xml";

	public static final String DATA_SET_SMALL_PROJECT = DATA_SET_SMALL_DIR
			+ "project.xml";

	public static final String TABLE_TASK = "Task";

	public static final String TABLE_BACKLOG = "Backlog";

	public static final String TABLE_PROJECT = "Project";

	public static final String TABLE_SPRINT = "Sprint";

	public static final String URL_ROOT = "http://localhost:8081/angularJsPlay";

	public static final String URL_BASE_COMMON = URL_ROOT + "/"
			+ ScrumRestConstants.REST_ROOT + "/";

}
