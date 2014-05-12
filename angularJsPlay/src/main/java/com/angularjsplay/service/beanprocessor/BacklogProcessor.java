package com.angularjsplay.service.beanprocessor;

import org.springframework.stereotype.Component;

import com.angularjsplay.exception.ScrumValidationException;
import com.angularjsplay.model.Backlog;
import com.angularjsplay.model.Sprint;
import com.appbasement.component.beanprocessor.GenericBeanProcessor;

@Component
public class BacklogProcessor extends GenericBeanProcessor<Backlog> {

	private void validateBacklog(Backlog backlog) {
		Long projectId = backlog.getProjectId();
		Long sprintId = backlog.getSprintId();
		// validation before commit
		if (sprintId != null) {
			Sprint sprint = backlog.getSprint();
			if (!sprint.getProjectId().equals(backlog.getProjectId())) {
				throw new ScrumValidationException("Sprint " + sprintId
						+ " doesn't belong to project " + projectId);
			}
		}

	}

	@Override
	public void doBeforeCreate(Backlog bean) {
		validateBacklog(bean);
	}

	@Override
	public void doBeforeUpdateWithPatch(Backlog bean, Backlog patch) {
		// unset sprintId
		if (patch.isRemoveSprint()) {
			bean.setSprintId(null);
		}
		validateBacklog(bean);
	}

}
