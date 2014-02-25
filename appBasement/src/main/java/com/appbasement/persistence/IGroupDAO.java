package com.appbasement.persistence;

import com.appbasement.model.Group;

public interface IGroupDAO extends IGenericDAO<Group, Long> {

	public abstract Group getGroupWithEagerUser(Long id);

}
