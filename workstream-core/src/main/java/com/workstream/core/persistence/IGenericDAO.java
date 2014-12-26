package com.workstream.core.persistence;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface IGenericDAO<T, ID extends Serializable> {
	
	/**
	 * With a OPTIMISTIC lock mode as default
	 * @param id
	 * @return
	 */
	public T findById(ID id);
	
	public T findById(ID id, boolean pessimisticWriteLock);
	
	public T getReference(ID id);
	
	public List<T> findAll();
	
	public void persist(T entity);
	
	public T merge(T entity);
	
	public void remove(T entity);
	
	public Class<T> getPersistentClass();

	public abstract Long getAllCount();

	public abstract Collection<T> findAll(int first, int max);

	public abstract boolean emContains(T entity);

	public abstract T reattachIfNeeded(T entity, ID id);

}
