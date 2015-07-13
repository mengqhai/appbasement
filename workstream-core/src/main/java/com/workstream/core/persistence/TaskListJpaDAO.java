package com.workstream.core.persistence;

import java.util.Collection;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.workstream.core.CoreConstants;
import com.workstream.core.model.Project;
import com.workstream.core.model.TaskList;

@Repository
@Transactional(propagation = Propagation.REQUIRED, value = CoreConstants.TX_MANAGER)
public class TaskListJpaDAO extends GenericJpaDAO<TaskList, Long> implements
		ITaskListDAO {

	@Override
	public Collection<TaskList> filterFor(Project project, int first, int max) {
		return filterFor("project", project, first, max, null);
	}

}
