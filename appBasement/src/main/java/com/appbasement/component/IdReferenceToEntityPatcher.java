package com.appbasement.component;

import java.lang.reflect.Field;

public class IdReferenceToEntityPatcher extends DefaultPatchStrategy {

	@Override
	public <T> PatchedValue doPatch(Field field, T target, T patch) {
		return null;
	}
}
