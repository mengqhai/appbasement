package com.appbasement.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DaoRegistory implements IDaoRegistry {

	@Autowired
	protected List<IGenericDAO<?, ?>> daoList;

	protected Map<Class<?>, IGenericDAO<?, ?>> regMap = new HashMap<Class<?>, IGenericDAO<?, ?>>();

	public DaoRegistory() {
	}

	@Override
	public List<IGenericDAO<?, ?>> getDaoList() {
		return daoList;
	}

	@PostConstruct
	public void init() {
		for (IGenericDAO<?, ?> dao : daoList) {
			Class<?> entityType = dao.getPersistentClass();
			regMap.put(entityType, dao);
		}
	}

	@Override
	public IGenericDAO<?, ?> getDao(Class<?> entityType) {
		return regMap.get(entityType);
	}
	
	@Override
	public boolean hasDaoFor(Class<?> entityType) {
		return regMap.containsKey(entityType);
	}
	
	@Override
	public void initialize(Object proxy) {
		Hibernate.initialize(proxy);
	}

}
