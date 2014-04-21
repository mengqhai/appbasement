package com.appbasement.component;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.springframework.util.ReflectionUtils.FieldFilter;
import static org.springframework.util.ReflectionUtils.*;

@Component
public class ObjectPatcher implements IObjectPatcher {

	@Override
	public <T> void patchObject(final T target, final T patch) {
		ReflectionUtils.doWithFields(target.getClass(), new FieldCallback() {
			@Override
			public void doWith(Field field) throws IllegalArgumentException,
					IllegalAccessException {
				makeAccessible(field);
				Object newValue = getField(field, patch);
				setField(field, target, newValue);
			}
		}, new FieldFilter() {
			@Override
			public boolean matches(Field field) {
				Class<?> type = field.getType();
				boolean isPrimitive = type.isPrimitive();
				boolean isCollection = type.isAssignableFrom(Collection.class);
				boolean isMap = type.isAssignableFrom(Map.class);
				boolean isArray = type.isArray();
				if (!(isPrimitive || isCollection || isMap || isArray)) {
					makeAccessible(field);
					return getField(field, patch) != null;
				} else {
					return false;
				}

			}
		});
	}

}
