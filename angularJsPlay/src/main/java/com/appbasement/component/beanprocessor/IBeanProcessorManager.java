package com.appbasement.component.beanprocessor;

import java.util.List;

import com.appbasement.exception.BeanProcessorException;

public interface IBeanProcessorManager {

	public abstract <T> void doBeforeDelete(T bean)
			throws BeanProcessorException;

	public abstract <T> void doBeforeUpdateWithPatch(T bean, T patch)
			throws BeanProcessorException;

	public abstract <T> void doBeforeCreate(T bean)
			throws BeanProcessorException;

	public abstract List<IBeanProcessor<?>> getBeanProcessors();

	public abstract IBeanProcessor<?> getBeanProcessor(Class<?> beanType);

}
