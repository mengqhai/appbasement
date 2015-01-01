package com.workstream.core.persistence;

import java.util.Collection;

import com.workstream.core.model.Revision;

public interface IRevisionDAO extends IGenericDAO<Revision, Long> {

	public abstract int deleteFor(String objType, String objId);

	public abstract Collection<Revision> filterFor(String objType, String objId);

}
