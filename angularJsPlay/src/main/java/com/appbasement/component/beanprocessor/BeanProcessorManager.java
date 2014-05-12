package com.appbasement.component.beanprocessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.appbasement.exception.BeanProcessorException;
import com.appbasement.exception.BeanProcessorNotFoundException;
import com.appbasement.util.AppReflectionUtils;

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

	@SuppressWarnings("unchecked")
	@Override
	public <T> IBeanProcessor<T> getBeanProcessor(T bean) {
		Class<T> actualBeanType = AppReflectionUtils.getActualClass(bean);
		return (IBeanProcessor<T>) reg.get(actualBeanType);
	}

	@Override
	public List<IBeanProcessor<?>> getBeanProcessors() {
		return processors;
	}

	@Override
	public <T> void doBeforeCreate(T bean) throws BeanProcessorException {
		IBeanProcessor<T> processor = getBeanProcessor(bean);
		if (processor == null) {
			throw new BeanProcessorNotFoundException(bean.getClass().getName());
		}
		processor.doBeforeCreate(bean);
	}

	@Override
	public <T> void doBeforeUpdateWithPatch(T bean, T patch)
			throws BeanProcessorException {
		IBeanProcessor<T> processor = getBeanProcessor(bean);
		if (processor == null) {
			throw new BeanProcessorNotFoundException(bean.getClass().getName());
		}
		processor.doBeforeUpdateWithPatch(bean, patch);
	}

	@Override
	public <T> void doBeforeDelete(T bean) throws BeanProcessorException {
		IBeanProcessor<T> processor = getBeanProcessor(bean);
		if (processor == null) {
			throw new BeanProcessorNotFoundException(bean.getClass().getName());
		}
		processor.doBeforeDelete(bean);
	}

}
