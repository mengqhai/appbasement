package com.appbasement.service.crud;

import java.io.Serializable;
import java.util.List;

import com.appbasement.exception.ResourceNotFoundException;

public interface ICrudService {

	public abstract <T> void deleteById(Class<T> type, Serializable id)
			throws ResourceNotFoundException;

	public abstract <T> List<T> getAll(Class<T> type);

	public abstract <T> T getById(Class<T> type, Serializable id)
			throws ResourceNotFoundException;

	public abstract <T> void updateWithPatch(T patch);

	public abstract <T> void createWithIdRef(T entity);

	public abstract <T> T getById(Class<T> type, Serializable id, String... eagerFields);

}
