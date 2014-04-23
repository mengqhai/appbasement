package com.appbasement.component;

import java.lang.reflect.Field;
import java.util.Map;

public interface IObjectPatcher {

	public <T> Map<Field, PatchedValue> patchObject(T target, T patch);

}
