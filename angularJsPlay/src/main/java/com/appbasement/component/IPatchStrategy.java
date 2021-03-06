package com.appbasement.component;

import java.lang.reflect.Field;

public interface IPatchStrategy extends Comparable<IPatchStrategy> {

	public <T> boolean canPatchField(Field field, T target, T patch);

	public <T> PatchedValue doPatch(Field field, T target, T patch);

	public int order();

}
