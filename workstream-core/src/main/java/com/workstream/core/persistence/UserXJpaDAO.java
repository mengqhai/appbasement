package com.workstream.core.persistence;

import java.util.Collection;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.workstream.core.CoreConstants;
import com.workstream.core.model.UserX;

@Repository
@Transactional(propagation = Propagation.REQUIRED, value = CoreConstants.TX_MANAGER)
public class UserXJpaDAO extends GenericJpaDAO<UserX, Long> implements
		IUserXDAO {

	@Override
	public UserX findByUserId(String userId) {
		Collection<UserX> result = filterFor("userId", userId, 0, 1);
		if (result.isEmpty()) {
			return null;
		} else {
			return result.iterator().next();
		}
	}

}
