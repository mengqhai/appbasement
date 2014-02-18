package com.appbasement.persistence;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.appbasement.model.Group;
import com.appbasement.model.User;

@Repository
@Transactional
public class GroupJpaDAO extends GenericJpaDAO<Group, Long> implements
		IGroupDAO {

	@Override
	public void remove(Group entity) {
		if (entity == null) {
			throw new IllegalArgumentException("Group is nulls");
		}

		// Maintain bidirectional many-to-many association between
		// Group and User (chase the pointer)
		// although the owning side of the association will auto remove
		// the reference entry in the join table
		// still need to remove the link from User for persistence context
		// synchronization
		// For preventing the concurrent modification exception:
		List<User> temp = new ArrayList<User>(entity.getUsers());
		for (User user : temp) {
			user.removeFromGroup(entity);
		}
		super.remove(entity);
	}

}
