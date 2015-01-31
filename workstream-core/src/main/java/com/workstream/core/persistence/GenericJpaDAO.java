package com.workstream.core.persistence;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GenericJpaDAO<T, ID extends Serializable> implements
		IGenericDAO<T, ID> {

	private final Logger log = LoggerFactory.getLogger(GenericJpaDAO.class);

	@PersistenceContext
	private EntityManager em;

	private Class<T> persistentClass;

	@SuppressWarnings("unchecked")
	public GenericJpaDAO() {
		super();
		this.persistentClass = (Class<T>) ((ParameterizedType) this.getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public GenericJpaDAO(EntityManager em) {
		this();
		this.em = em;
	}

	public EntityManager getEm() {
		return em;
	}

	public IGenericDAO<T, ID> setEm(EntityManager em) {
		this.em = em;
		return this;
	}

	public Class<T> getPersistentClass() {
		return persistentClass;
	}

	@Override
	public T findById(ID id) {
		T entity = em.find(getPersistentClass(), id);
		return entity;
	}

	@Override
	public T findById(ID id, boolean pessimisticWriteLock) {
		T entity = em.find(getPersistentClass(), id,
				LockModeType.PESSIMISTIC_WRITE);
		return entity;
	}

	@Override
	/**
	 * Ordered by id desc
	 */
	public List<T> findAll() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> c = cb.createQuery(getPersistentClass());
		Root<T> from = c.from(getPersistentClass());
		c.select(from).orderBy(cb.desc(from.get("id")));
		return em.createQuery(c).getResultList();
	}

	/**
	 * Find all with pagination support.
	 */
	@Override
	public Collection<T> findAll(int first, int max) {
		return filterFor(null, null, first, max);
	}

	@Override
	public Long getAllCount() {
		return countFor(null, null);
	}

	@Override
	public T getReference(ID id) {
		return em.getReference(getPersistentClass(), id);
	}

	@Override
	public void persist(T entity) {
		em.persist(entity);
	}

	@Override
	public T merge(T entity) {
		return em.merge(entity);
	}

	@Override
	public boolean emContains(T entity) {
		return em.contains(entity);
	}

	@Override
	public T reattachIfNeeded(T entity, ID id) {
		if (!emContains(entity)) {
			return findById(id);
		} else {
			return entity;
		}
	}

	@Override
	public void remove(T entity) {
		if (entity == null) {
			throw new IllegalArgumentException("Entity is null");
		}
		if (!em.contains(entity)) {
			// the object is detached, must merge before remove
			entity = merge(entity);
		}
		em.remove(entity);
	}

	private <Y> Path<Y> parsePath(Root<?> all, String attributeName) {
		Path<Y> path = null;
		String[] fregs = attributeName.split("\\.");
		for (String freg : fregs) {
			if (path == null) {
				path = all.get(freg);
			} else {
				path = path.get(freg);
			}
		}
		return path;
	}

	/**
	 * Batch delete.
	 * 
	 * @param attributes
	 * @return
	 */
	protected int removeFor(Map<String, ? extends Serializable> attributes) {
		// JPA supports CriteriaDelete since 2.1
		// http://www.thoughts-on-java.org/2013/10/criteria-updatedelete-easy-way-to.html
		// needs at least Hibernate 4.3.0.beta4
		// but for now, let's use query
		if (attributes.isEmpty()) {
			log.warn("No filter condition for delete action, so nothing is deleted.");
			return 0;
		}

		Class<T> getClass = persistentClass;
		String entityName = getClass.getSimpleName();
		StringBuilder builder = new StringBuilder("delete from ").append(
				entityName).append(" as e where ");
		int attCount = 0;
		for (String attributeName : attributes.keySet()) {
			attCount++;
			builder.append("e.").append(attributeName).append("=");
			builder.append(":").append(attributeName);
			if (attCount < attributes.size()) {
				builder.append(" and ");
			}
		}

		Query q = getEm().createQuery(builder.toString());
		for (String attributeName : attributes.keySet()) {
			Serializable attributeValue = attributes.get(attributeName);
			q.setParameter(attributeName, attributeValue);
		}
		int deletedCount = q.executeUpdate();
		return deletedCount;
	}

	protected Collection<T> filterFor(
			Map<String, ? extends Serializable> attributes, int first, int max) {
		return filterFor(attributes, first, max, "id");
	}

	/**
	 * order by id desc
	 * 
	 * @param attributes
	 * @param first
	 * @param max
	 * @param descBy
	 * @return
	 */
	protected Collection<T> filterFor(
			Map<String, ? extends Serializable> attributes, int first, int max,
			String descBy) {
		Class<T> getClass = persistentClass;
		CriteriaBuilder cb = getEm().getCriteriaBuilder();
		CriteriaQuery<T> c = cb.createQuery(getClass);
		Root<T> all = c.from(getClass);
		Expression<Boolean> lastEx = null;
		for (String attributeName : attributes.keySet()) {
			Serializable attributeValue = attributes.get(attributeName);
			if (attributeName != null && !attributeName.equals("")) {
				Expression<Boolean> ex = cb.equal(
						parsePath(all, attributeName), attributeValue);
				if (lastEx == null) {
					lastEx = ex;
				} else {
					lastEx = cb.and(lastEx, ex);
				}
			}
		}
		c.where(lastEx);
		c.select(all);
		if (descBy != null) {
			// order by xxx desc
			c.orderBy(cb.desc(parsePath(all, descBy)));
		}
		return em.createQuery(c).setFirstResult(first).setMaxResults(max)
				.getResultList();
	}

	/**
	 * Query the entities by something like:
	 * "select b from Backlog as b where b.project.id=:projectId order by b.id desc"
	 * 
	 * @param attributeName
	 * @param attributeValue
	 * @param first
	 * @param max
	 * @return
	 */
	protected Collection<T> filterFor(String attributeName,
			Serializable attributeValue, int first, int max) {
		return filterFor(attributeName, attributeValue, first, max, "id");
	}

	protected Collection<T> filterFor(String attributeName,
			Serializable attributeValue, int first, int max, String descBy) {
		Class<T> getClass = persistentClass;
		CriteriaBuilder cb = getEm().getCriteriaBuilder();
		CriteriaQuery<T> c = cb.createQuery(getClass);
		Root<T> all = c.from(getClass);
		if (attributeName != null && !attributeName.equals("")) {
			Expression<Boolean> ex = cb.equal(parsePath(all, attributeName),
					attributeValue);
			c.where(ex);
		}
		c.select(all);
		if (descBy != null) {
			// order by xxx desc
			c.orderBy(cb.desc(parsePath(all, descBy)));
		}
		return em.createQuery(c).setFirstResult(first).setMaxResults(max)
				.getResultList();
	}

	/**
	 * Filter all the entities with a collection field that contains a given
	 * value object, order by id desc
	 * 
	 * @param collectionName
	 * @param elementValue
	 * @return
	 */
	protected Collection<T> filterForContains(String collectionName,
			Serializable elementValue) {
		Class<T> getClass = persistentClass;
		CriteriaBuilder cb = getEm().getCriteriaBuilder();
		CriteriaQuery<T> c = cb.createQuery(getClass);
		Root<T> all = c.from(getClass);
		// http://www.objectdb.com/java/jpa/query/jpql/collection#Criteria_Query_Collection_Expressions_
		if (collectionName != null && !collectionName.equals("")) {
			Expression<Collection<Serializable>> collection = all
					.get(collectionName);
			Predicate isMember = cb.isMember(elementValue, collection);
			c.where(isMember);
		}
		c.select(all).orderBy(cb.desc(all.get("id")));
		return em.createQuery(c).getResultList();
	}

	/**
	 * order by id desc
	 * 
	 * @param attributeName
	 * @param attributeValue
	 * @return
	 */
	protected T findFor(String attributeName, Serializable attributeValue) {
		Class<T> getClass = persistentClass;
		CriteriaBuilder cb = getEm().getCriteriaBuilder();
		CriteriaQuery<T> c = cb.createQuery(getClass);
		Root<T> all = c.from(getClass);
		if (attributeName != null && !attributeName.equals("")) {
			Expression<Boolean> ex = cb.equal(parsePath(all, attributeName),
					attributeValue);
			c.where(ex);
		}
		c.select(all).orderBy(cb.desc(all.get("id")));
		try {
			return em.createQuery(c).getSingleResult();
		} catch (NoResultException e) {
			log.debug("No entity matching {}={}", attributeName,
					attributeValue, e);
			return null;
		}
	}

	protected Long countFor(Map<String, ? extends Serializable> attributes) {
		Class<T> getClass = persistentClass;
		CriteriaBuilder cb = getEm().getCriteriaBuilder();
		CriteriaQuery<Long> c = cb.createQuery(Long.class);
		Root<T> all = c.from(getClass);
		Expression<Boolean> lastEx = null;
		for (String attributeName : attributes.keySet()) {
			Serializable attributeValue = attributes.get(attributeName);
			if (attributeName != null && !attributeName.equals("")) {
				Expression<Boolean> ex = cb.equal(
						parsePath(all, attributeName), attributeValue);
				if (lastEx == null) {
					lastEx = ex;
				} else {
					lastEx = cb.and(lastEx, ex);
				}
			}
		}
		c.where(lastEx);
		Expression<Long> count = cb.count(all);
		c.select(count);
		return em.createQuery(c).getSingleResult();
	}

	protected Long countFor(String attributeName, Serializable attributeValue) {
		Class<T> getClass = persistentClass;
		CriteriaBuilder cb = getEm().getCriteriaBuilder();
		CriteriaQuery<Long> c = cb.createQuery(Long.class);
		Root<T> all = c.from(getClass);
		Expression<Long> count = cb.count(all);
		if (attributeName != null && !attributeName.equals("")) {
			Expression<Boolean> ex = cb.equal(parsePath(all, attributeName),
					attributeValue);
			c.where(ex);
		}
		c.select(count);
		return em.createQuery(c).getSingleResult();
	}

}
