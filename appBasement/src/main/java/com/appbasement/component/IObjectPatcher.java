package com.appbasement.component;

public interface IObjectPatcher {

	public <T> void patchObject(T target, T patch);

}
