package com.workstream.core.persistence;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
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
		return countFilteredFor(null, null);
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
		c.select(all);
		try {
			return em.createQuery(c).getSingleResult();
		} catch (NoResultException e) {
			log.debug("No entity matching {}={}", attributeName,
					attributeValue, e);
			return null;
		}
	}

	protected Long countFilteredFor(String attributeName,
			Serializable attributeValue) {
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
