package com.appbasement.component;

import java.io.Serializable;
import java.lang.reflect.Field;

import javax.persistence.Id;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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

	@Override
	public <T> PatchedValue doPatch(Field field, T target, final T patch) {
		ReflectionUtils.doWithFields(patch.getClass(), new FieldCallback() {
			@Override
			public void doWith(Field field) throws IllegalArgumentException,
					IllegalAccessException {
				Serializable id = (Serializable) ReflectionUtils.getField(
						field, patch);
				if (id == null) {
					throw new IllegalArgumentException("Null id in the patch");
				}
				IGenericDAO<?, Serializable> dao = (IGenericDAO<?, Serializable>) daoRegistry
						.getDao(patch.getClass());
				Object entity = dao.getReference(id);
				
				
				
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
