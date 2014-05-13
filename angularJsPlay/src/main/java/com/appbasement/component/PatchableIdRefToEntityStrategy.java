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
import com.appbasement.util.AppReflectionUtils;

@Component
public class PatchableIdRefToEntityStrategy extends DefaultPatchStrategy {

	@Autowired
	private IDaoRegistry daoRegistry;

	@Override
	public <T> boolean canPatchField(Field field, T target, T patch) {
		boolean isPatchableIdRef = field
				.isAnnotationPresent(PatchableIdRef.class);
		if (!isPatchableIdRef) {
			return false;
		}

		boolean isEntityValue = daoRegistry.hasDaoFor(field.getType());
		if (!isEntityValue) {
			return false;
		}

		return DefaultPatchStrategy.defaultPatchingRule(field, target, patch);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public <T> PatchedValue doPatch(final Field outterField, final T target,
			T patch) {
		ReflectionUtils.makeAccessible(outterField);
		final Object idRef = ReflectionUtils.getField(outterField, patch);

		final PatchedValue pValue = new PatchedValue();

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
				Object entity = dao.getReference(id); // Makes sure the
														// reference exists
				daoRegistry.initialize(entity); // throws
												// EntityNotFoundException if
												// unable to find it
				pValue.setNewValue(entity);
				pValue.setOldValue(ReflectionUtils
						.getField(outterField, target));

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

				Class<?> toBeSetType = AppReflectionUtils
						.getActualClass(toBeSet);
				Method setter = ReflectionUtils.findMethod(setterHostClass,
						ann.setter(), toBeSetType);
				ReflectionUtils.invokeMethod(setter, setterHost, toBeSet);

			}
		}, new FieldFilter() {
			@Override
			public boolean matches(Field field) {
				return field.isAnnotationPresent(Id.class);
			}
		});
		return pValue;
	}

	@Override
	public int order() {
		return 10;
	}
}
