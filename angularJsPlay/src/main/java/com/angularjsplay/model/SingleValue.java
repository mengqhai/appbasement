package com.angularjsplay.model;

/**
 * A wrapper object to make the single valued json response body as an object
 * instead of primitive values like 20.
 * 
 * @author qinghai
 * 
 */
public class SingleValue<T> {

	protected T value;
	
	public SingleValue() {
	}

	public SingleValue(T value) {
		this.value = value;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}
}
