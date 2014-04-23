package com.appbasement.component;

public class PatchedValue {
	
	private Object oldValue;
	
	private Object newValue;
	
	public PatchedValue() {
	}

	public PatchedValue(Object oldValue, Object newValue) {
		super();
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public Object getOldValue() {
		return oldValue;
	}

	public void setOldValue(Object oldValue) {
		this.oldValue = oldValue;
	}

	public Object getNewValue() {
		return newValue;
	}

	public void setNewValue(Object newValue) {
		this.newValue = newValue;
	}
}
