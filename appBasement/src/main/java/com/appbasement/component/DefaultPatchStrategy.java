package com.appbasement.component;

import static org.springframework.util.ReflectionUtils.getField;
import static org.springframework.util.ReflectionUtils.makeAccessible;
import static org.springframework.util.ReflectionUtils.setField;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Date;
import java.util.Collection;
import java.util.Map;

public class DefaultPatchStrategy implements IPatchStrategy {

	@Override
	public <T> PatchedValue doPatch(Field field, T target, T patch) {
		makeAccessible(field);
		Object newValue = getField(field, patch);
		Object oldValue = getField(field, target);
		boolean isDate = Date.class.isAssignableFrom(field.getType());
		boolean fieldModfied = false;
		if (!isDate && !newValue.equals(oldValue)) {
			fieldModfied = true;
		} else if (isDate) {
			Date newDate = (Date) newValue;
			Date oldDate = (Date) oldValue;
			if (oldDate != null) {
				fieldModfied = (newDate.getTime() != oldDate.getTime());
			}
		}

		if (fieldModfied) {
			setField(field, target, newValue);
			return new PatchedValue(oldValue, newValue);
		} else {
			return null;
		}
	}

	@Override
	public <T> boolean canPatchField(Field field, T target, T patch) {
		Class<?> type = field.getType();
		boolean isPrimitive = type.isPrimitive();
		boolean isCollection = Collection.class.isAssignableFrom(type);
		boolean isMap = type.isAssignableFrom(Map.class);
		boolean isArray = type.isArray();
		boolean isStatic = Modifier.isStatic(field.getModifiers());
		boolean isFinal = Modifier.isFinal(field.getModifiers());
		boolean isId = field.getAnnotation(javax.persistence.Id.class) != null;
		if (!(isPrimitive || isCollection || isMap || isArray || isStatic
				|| isFinal || isId)) {
			makeAccessible(field);
			return getField(field, patch) != null;
		} else {
			return false;
		}
	}

}
