package com.appbasement.persistence;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public abstract class GenericJpaDAO<T, ID extends Serializable> implements
		IGenericDAO<T, ID> {

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

	public GenericJpaDAO<T, ID> setEm(EntityManager em) {
		this.em = em;
		return this;
	}

	@Override
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
		em.remove(entity);
	}

}
