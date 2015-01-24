package com.workstream.rest.model;

public class SingleValueResponse extends InnerWrapperObj<Object> {

	public SingleValueResponse(Object inner) {
		super(inner);
	}

	public Object getValue() {
		return inner;
	}

}
