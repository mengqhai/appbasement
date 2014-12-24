package com.workstream.core.persistence;

import java.util.Collection;

import com.workstream.core.model.GroupX;
import com.workstream.core.model.Organization;

public interface IGroupXDAO extends IGenericDAO<GroupX, Long> {

	public abstract GroupX findByGroupId(String groupId);

	public abstract Collection<GroupX> filterFor(Organization org);

}
