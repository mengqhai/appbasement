package com.appbasement.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.appbasement.model.Group;
import com.appbasement.model.User;

@Repository("groupDao")
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

	@Override
	public Group getGroupWithEagerUser(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("Null id");
		}

		Group result = null;
		TypedQuery<Group> q = getEm()
				.createQuery(
						"select distinct g from Group as g left join fetch g.users as u where g.id=:id",
						Group.class);
		q.setParameter("id", id);
		try {
			result = (Group) q.getSingleResult();
		} catch (NoResultException e) {
			// not found, do nothing
		}
		return result;
	}

}
