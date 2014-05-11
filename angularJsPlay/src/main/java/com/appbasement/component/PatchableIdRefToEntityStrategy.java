package com.appbasement.component;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.persistence.Id;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.springframework.util.ReflectionUtils.FieldFilter;

import com.appbasement.persistence.IDaoRegistry;
import com.appbasement.persistence.IGenericDAO;

@Component
public class PatchableIdRefToEntityStrategy extends DefaultPatchStrategy {

	@Autowired
	private IDaoRegistry daoRegistry;

	@Override
	public <T> boolean canPatchField(Field field, T target, T patch) {
		boolean defaultPatchable = super.canPatchField(field, target, patch);
		boolean isPatchableIdRef = field
				.isAnnotationPresent(PatchableIdRef.class);
		boolean isEntityValue = daoRegistry.hasDaoFor(field.getType());
		return defaultPatchable && isPatchableIdRef && isEntityValue;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public <T> PatchedValue doPatch(final Field outterField, final T target,
			T patch) {
		ReflectionUtils.makeAccessible(outterField);
		final Object idRef = ReflectionUtils.getField(outterField, patch);

		ReflectionUtils.doWithFields(idRef.getClass(), new FieldCallback() {
			@SuppressWarnings("unchecked")
			@Override
			public void doWith(Field patchField)
					throws IllegalArgumentException, IllegalAccessException {
				ReflectionUtils.makeAccessible(patchField);
				Serializable id = (Serializable) ReflectionUtils.getField(
						patchField, idRef);
				if (id == null) {
					throw new IllegalArgumentException("Null id in the patch");
				}
				IGenericDAO<?, Serializable> dao = (IGenericDAO<?, Serializable>) daoRegistry
						.getDao(idRef.getClass());
				Object entity = dao.getReference(id);

				PatchableIdRef ann = outterField
						.getAnnotation(PatchableIdRef.class);
				Class<?> setterHostClass = ann.setterHost();
				Object setterHost = null;
				Object toBeSet = null;
				if (setterHostClass.equals(target.getClass())) {
					setterHost = target;
					toBeSet = entity;
				} else if (setterHostClass.equals(outterField.getType())) {
					setterHost = entity;
					toBeSet = target;
				}

				if (setterHost == null) {
					throw new IllegalStateException("Neither "
							+ target.getClass() + " nor "
							+ patchField.getType() + " is the setterHost");
				}

				Method setter = ReflectionUtils.findMethod(setterHostClass,
						ann.setter(), toBeSet.getClass());
				ReflectionUtils.invokeMethod(setter, setterHost, toBeSet);
			}
		}, new FieldFilter() {
			@Override
			public boolean matches(Field field) {
				return field.isAnnotationPresent(Id.class);
			}
		});

		return null;
	}

	@Override
	public int order() {
		return 10;
	}
}
