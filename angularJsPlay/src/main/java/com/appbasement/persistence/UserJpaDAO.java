package com.appbasement.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.appbasement.model.Group;
import com.appbasement.model.User;

@Repository("userDao")
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
		User result = null;
		try {
			result = (User) q.getSingleResult();
		} catch (NoResultException e) {
			// not found, do nothing
		}

		return result;
	}

	@Override
	public User getUserWithEagerGroups(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("Null id");
		}

		User result = null;
		TypedQuery<User> q = getEm()
				.createQuery(
						"select distinct u from User as u left join fetch u.groups as g where u.id=:id",
						User.class);
		q.setParameter("id", id);
		try {
			result = (User) q.getSingleResult();
		} catch (NoResultException e) {
			// not found, do nothing
		}
		return result;
	}

}
