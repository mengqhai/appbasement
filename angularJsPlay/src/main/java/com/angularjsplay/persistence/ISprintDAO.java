package com.angularjsplay.persistence;

import java.util.Collection;

import com.angularjsplay.model.Sprint;
import com.appbasement.persistence.IGenericDAO;

public interface ISprintDAO extends IGenericDAO<Sprint, Long> {

	public abstract Long getSprintCountForProject(Long projectId);

	public abstract Collection<Sprint> getSprintsForProject(Long projectId,
			int first, int max);

	public abstract Collection<Sprint> getSprintsForProject(Long projectId);
}
