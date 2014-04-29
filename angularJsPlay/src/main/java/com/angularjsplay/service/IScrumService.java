package com.angularjsplay.service;

import java.util.List;

import com.angularjsplay.exception.ScrumResourceNotFoundException;
import com.angularjsplay.model.IEntity;

public interface IScrumService {

	public abstract <T extends IEntity> void deleteById(Class<T> type, Long id)
			throws ScrumResourceNotFoundException;

	public abstract <T extends IEntity> List<T> getAll(Class<T> type);

	public abstract <T extends IEntity> T getById(Class<T> type, Long id)
			throws ScrumResourceNotFoundException;

	public abstract <T extends IEntity> void save(T entity);

}
