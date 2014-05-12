package com.appbasement.component.beanprocessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.appbasement.exception.BeanProcessorException;
import com.appbasement.exception.BeanProcessorNotFoundException;

@Component
public class BeanProcessorManager implements IBeanProcessorManager {

	@Autowired
	protected List<IBeanProcessor<?>> processors;

	protected Map<Class<?>, IBeanProcessor<?>> reg = new HashMap<Class<?>, IBeanProcessor<?>>();

	public BeanProcessorManager() {
	}

	@PostConstruct
	public void init() {
		for (IBeanProcessor<?> p : processors) {
			Class<?> beanType = p.getBeanType();
			reg.put(beanType, p);
		}
	}

	@Override
	public IBeanProcessor<?> getBeanProcessor(Class<?> beanType) {
		return reg.get(beanType);
	}

	@Override
	public List<IBeanProcessor<?>> getBeanProcessors() {
		return processors;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> void doBeforeCreate(T bean) throws BeanProcessorException {
		IBeanProcessor<T> processor = (IBeanProcessor<T>) getBeanProcessor(bean
				.getClass());
		if (processor == null) {
			throw new BeanProcessorNotFoundException(bean.getClass().getName());
		}
		processor.doBeforeCreate(bean);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> void doBeforeUpdateWithPatch(T bean, T patch)
			throws BeanProcessorException {
		IBeanProcessor<T> processor = (IBeanProcessor<T>) getBeanProcessor(bean
				.getClass());
		if (processor == null) {
			throw new BeanProcessorNotFoundException(bean.getClass().getName());
		}
		processor.doBeforeUpdateWithPatch(bean, patch);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> void doBeforeDelete(T bean) throws BeanProcessorException {
		IBeanProcessor<T> processor = (IBeanProcessor<T>) getBeanProcessor(bean
				.getClass());
		if (processor == null) {
			throw new BeanProcessorNotFoundException(bean.getClass().getName());
		}
		processor.doBeforeDelete(bean);
	}

}
