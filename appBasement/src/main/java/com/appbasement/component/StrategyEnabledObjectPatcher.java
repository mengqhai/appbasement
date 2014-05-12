package com.appbasement.component;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.springframework.util.ReflectionUtils.FieldFilter;

@Component
public class StrategyEnabledObjectPatcher implements IObjectPatcher {

	/**
	 * The older of strategies matters.
	 */
	@Autowired
	private List<IPatchStrategy> strategies;

	/**
	 * Always have a default strategy to perform the patching.
	 */
	private IPatchStrategy defaultStrategy;

	public StrategyEnabledObjectPatcher() {
	}

	@PostConstruct
	public void init() {
		if (strategies != null && !strategies.isEmpty()) {
			Collections.sort(strategies);
			defaultStrategy = strategies.get(strategies.size() - 1);
		} else {
			throw new IllegalStateException("No strategy is added.");
		}
	}

	@Override
	public <T> Map<Field, PatchedValue> patchObject(final T target,
			final T patch) {
		final Map<Field, PatchedValue> patchedInfo = new HashMap<Field, PatchedValue>();

		ReflectionUtils.doWithFields(target.getClass(), new FieldCallback() {
			@Override
			public void doWith(Field field) throws IllegalArgumentException,
					IllegalAccessException {
				PatchedValue pValue = null;
				for (IPatchStrategy strategy : strategies) {
					if (!strategy.canPatchField(field, target, patch)) {
						continue;
					}
					pValue = strategy.doPatch(field, target, patch);
					if (pValue != null) {
						break;
					}
				}

				if (pValue != null) {
					patchedInfo.put(field, pValue);
				}
			}
		}, new FieldFilter() {
			@Override
			public boolean matches(Field field) {
				if (defaultStrategy.canPatchField(field, target, patch)) {
					return true;
				} else {
					// delegates to strategies to perform matching
					for (IPatchStrategy strategy : strategies) {
						if (strategy == defaultStrategy) {
							continue;
						}
						if (strategy.canPatchField(field, target, patch)) {
							return true;
						}
					}
					return false;
				}

			}
		});
		return patchedInfo;
	}

	public List<IPatchStrategy> getStrategies() {
		return strategies;
	}

}
