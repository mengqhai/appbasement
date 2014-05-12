package com.appbasement.component.beanprocessor;

/**
 * 
 * @author qinghai
 * 
 * @param <T>
 *            The type of bean the processor handles.
 */
public interface IBeanProcessor<T> {

	public Class<T> getBeanType();

	public void doBeforeCreate(T bean);

	public void doBeforeUpdateWithPatch(T bean, T patch);
	
	public void doBeforeDelete(T bean);

}
