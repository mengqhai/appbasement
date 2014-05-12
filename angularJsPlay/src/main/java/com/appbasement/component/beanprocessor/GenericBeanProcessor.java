package com.appbasement.component.beanprocessor;

import java.lang.reflect.ParameterizedType;

public class GenericBeanProcessor<T> implements IBeanProcessor<T> {

	protected Class<T> beanType;

	@SuppressWarnings("unchecked")
	public GenericBeanProcessor() {
		beanType = (Class<T>) ((ParameterizedType) this.getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
	}

	@Override
	public Class<T> getBeanType() {
		return beanType;
	}

	@Override
	public void doBeforeCreate(T bean) {
	}

	@Override
	public void doBeforeUpdateWithPatch(T bean, T patch) {
	}

	@Override
	public void doBeforeDelete(T bean) {
	}

}
