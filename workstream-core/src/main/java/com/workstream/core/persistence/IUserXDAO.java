package com.workstream.core.persistence;

import com.workstream.core.model.UserX;

public interface IUserXDAO extends IGenericDAO<UserX, Long>{

	public abstract UserX findByUserId(String userId);

}
