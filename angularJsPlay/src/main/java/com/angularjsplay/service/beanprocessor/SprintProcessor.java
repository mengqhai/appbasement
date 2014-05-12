package com.angularjsplay.service.beanprocessor;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.angularjsplay.exception.ScrumValidationException;
import com.angularjsplay.model.Backlog;
import com.angularjsplay.model.Project;
import com.angularjsplay.model.Sprint;
import com.angularjsplay.persistence.IProjectDAO;
import com.appbasement.component.beanprocessor.GenericBeanProcessor;

@Component
public class SprintProcessor extends GenericBeanProcessor<Sprint> {

	@Autowired
	IProjectDAO projectDao;

	public void validateSprint(Sprint bean) {
		if (bean.getStartAt() != null && bean.getEndAt() != null
				&& bean.getStartAt().getTime() > bean.getEndAt().getTime()) {
			throw new ScrumValidationException(
					"Sprint startAt is later than endAt.");
		}
	}

	private void changeBacklogProjectForSprint(Sprint sprint, Long newProjectId) {
		if (newProjectId != null) {
			// sprint.project is changed
			Project project = projectDao.getReference(newProjectId);
			// project.addSprintToProject(sprint);
			// also change the projectIds of all sub-backlogs
			Collection<Backlog> backlogs = sprint.getBacklogs();
			for (Backlog b : backlogs) {
				project.addBacklogToProject(b);
			}
		}
	}

	@Override
	public void doBeforeCreate(Sprint bean) {
		validateSprint(bean);
	}

	@Override
	public void doBeforeUpdateWithPatch(Sprint bean, Sprint patch) {
		validateSprint(bean);
		Long projectId = patch.getProjectId();
		changeBacklogProjectForSprint(bean, projectId);
	}

}
