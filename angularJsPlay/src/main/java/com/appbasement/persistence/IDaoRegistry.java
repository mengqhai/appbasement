package com.appbasement.persistence;

import java.util.List;

public interface IDaoRegistry {

	public abstract List<IGenericDAO<?, ?>> getDaoList();

	public abstract IGenericDAO<?, ?> getDao(Class<?> entityType);

	public abstract boolean hasDaoFor(Class<?> entityType);

	public abstract void initialize(Object proxy);

}
