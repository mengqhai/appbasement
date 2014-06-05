package com.appbasement.service.crud;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import com.appbasement.component.IObjectPatcher;
import com.appbasement.component.beanprocessor.IBeanProcessorManager;
import com.appbasement.exception.ResourceNotFoundException;
import com.appbasement.persistence.IDaoRegistry;
import com.appbasement.persistence.IGenericDAO;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class CrudService implements ICrudService {

	@Autowired
	protected IDaoRegistry daoReg;

	@Autowired
	protected IBeanProcessorManager bMgr;

	@Autowired
	@Qualifier("strategyEnabledObjectPatcher")
	IObjectPatcher objectPatcher;

	public CrudService() {
	}

	@SuppressWarnings("unchecked")
	protected <T> IGenericDAO<T, Serializable> getDao(Class<T> type) {
		IGenericDAO<T, Serializable> dao = (IGenericDAO<T, Serializable>) daoReg
				.getDao(type);

		if (dao == null) {
			throw new IllegalArgumentException(
					"CRUD service is not capable to handle type "
							+ type.getName());
		}
		return dao;
	}

	protected Serializable getId(Object entity) {
		// always looking for the method named getId
		Method getId = ReflectionUtils.findMethod(entity.getClass(), "getId");
		return (Serializable) ReflectionUtils.invokeMethod(getId, entity);
	}

	@SuppressWarnings("unchecked")
	protected <T> void save(T entity) {
		IGenericDAO<T, Serializable> dao = getDao((Class<T>) entity.getClass());
		Object id = getId(entity);
		if (id == null) {
			dao.persist((T) entity);
		} else {
			dao.merge((T) entity);
		}
	}

	@Override
	public <T> void deleteById(Class<T> type, Serializable id)
			throws ResourceNotFoundException {
		IGenericDAO<T, Serializable> dao = getDao(type);
		T toDelete = dao.getReference(id);
		try {
			bMgr.doBeforeDelete(toDelete);
			dao.remove(toDelete);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException(e);
		}
	}

	@Override
	public <T> T getById(Class<T> type, Serializable id)
			throws ResourceNotFoundException {
		IGenericDAO<T, Serializable> dao = getDao(type);
		T entity = dao.findById(id);
		if (entity == null) {
			throw new ResourceNotFoundException();
		} else {
			return entity;
		}
	}

	@Override
	public <T> T getById(Class<T> type, Serializable id, String... eagerFields) {
		final T entity = getById(type, id);
		if (entity == null) {
			throw new ResourceNotFoundException();
		}

		for (String fieldName : eagerFields) {
			Field field = ReflectionUtils.findField(type, fieldName);
			field.setAccessible(true);
			Object proxy = ReflectionUtils.getField(field, entity);
			if (proxy == null) {
				continue;
			}
			daoReg.initialize(proxy);
		}
		return entity;
	}

	@Override
	public <T> Collection<T> getAll(Class<T> type) {
		IGenericDAO<T, Serializable> dao = getDao(type);
		return dao.findAll();
	}
	
	@Override
	public <T> Collection<T> getAll(Class<T> type, int first, int max) {
		IGenericDAO<T, Serializable> dao = getDao(type);
		return dao.findAll(first, max);
	}
	
	@Override
	public <T> Long getAllCount(Class<T> type) {
		IGenericDAO<T, Serializable> dao = getDao(type);
		return dao.getAllCount();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> void updateWithPatch(T patch) {
		Serializable id = getId(patch);
		if (id == null) {
			throw new IllegalArgumentException("Null id in patch");
		}
		try {
			T existing = getById((Class<T>) patch.getClass(), id);
			objectPatcher.patchObject(existing, patch);
			bMgr.doBeforeUpdateWithPatch(existing, patch);
			save(existing);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException();
		}
	}

	@Override
	public <T> void createWithIdRef(T entity) {
		try {
			objectPatcher.patchObject(entity, entity);
			bMgr.doBeforeCreate(entity);
			save(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException();
		}
	}

}
