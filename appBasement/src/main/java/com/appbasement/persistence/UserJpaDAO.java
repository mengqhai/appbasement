package com.appbasement.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.appbasement.model.Group;
import com.appbasement.model.User;

@Repository
@Transactional
public class UserJpaDAO extends GenericJpaDAO<User, Long> implements IUserDAO {

	@Override
	public void remove(User entity) {

		if (entity == null) {
			throw new IllegalArgumentException("User is null");
		}
		// Maintain bidirectional many-to-many association between
		// Group and User (chase the pointer)
		// For preventing the concurrent modification exception:
		List<Group> temp = new ArrayList<Group>(entity.getGroups());
		for (Group group : temp) {
			entity.removeFromGroup(group);
		}
		super.remove(entity);
	}

	@Override
	public User findByUsername(String username) {
		if (username == null) {
			throw new IllegalArgumentException("Null username");
		}
		Query q = getEm().createNamedQuery(User.QUERY_BY_USERNAME, User.class);
		q.setParameter("username", username);
		return (User) q.getSingleResult();
	}

}
