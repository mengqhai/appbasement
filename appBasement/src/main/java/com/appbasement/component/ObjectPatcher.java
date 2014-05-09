package com.appbasement.component;

import static org.springframework.util.ReflectionUtils.getField;
import static org.springframework.util.ReflectionUtils.makeAccessible;
import static org.springframework.util.ReflectionUtils.setField;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Date;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.springframework.util.ReflectionUtils.FieldFilter;

@Component
public class ObjectPatcher implements IObjectPatcher {

	@Override
	public <T> Map<Field, PatchedValue> patchObject(final T target,
			final T patch) {
		final Map<Field, PatchedValue> patchedInfo = new HashMap<Field, PatchedValue>();

		ReflectionUtils.doWithFields(target.getClass(), new FieldCallback() {
			@Override
			public void doWith(Field field) throws IllegalArgumentException,
					IllegalAccessException {
				makeAccessible(field);
				Object newValue = getField(field, patch);
				Object oldValue = getField(field, target);
				boolean isDate = Date.class.isAssignableFrom(field.getType());
				boolean updated = false;
				if (!isDate && !newValue.equals(oldValue)) {
					updated = true;
				} else if (isDate) {
					Date newDate = (Date) newValue;
					Date oldDate = (Date) oldValue;
					if (oldDate != null) {
						updated = (newDate.getTime() != oldDate.getTime());
					}
				}

				if (updated) {
					patchedInfo
							.put(field, new PatchedValue(oldValue, newValue));
					setField(field, target, newValue);
				}
			}
		}, new FieldFilter() {
			@Override
			public boolean matches(Field field) {
				Class<?> type = field.getType();
				boolean isPrimitive = type.isPrimitive();
				boolean isCollection = Collection.class.isAssignableFrom(type);
				boolean isMap = type.isAssignableFrom(Map.class);
				boolean isArray = type.isArray();
				boolean isStatic = Modifier.isStatic(field.getModifiers());
				boolean isFinal = Modifier.isFinal(field.getModifiers());
				boolean isId = field.getAnnotation(javax.persistence.Id.class) != null;
				if (!(isPrimitive || isCollection || isMap || isArray
						|| isStatic || isFinal || isId)) {
					makeAccessible(field);
					return getField(field, patch) != null;
				} else {
					return false;
				}

			}
		});

		return patchedInfo;
	}

}
