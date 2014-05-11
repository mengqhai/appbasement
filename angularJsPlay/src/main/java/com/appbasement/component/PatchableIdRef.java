package com.appbasement.component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies a field is patchable by converting the value to an exiting entity
 * (refered by the id in the value) in the persistence layer.
 * 
 * 
 * @author liuli
 * 
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PatchableIdRef {
	Class<?> setterHost();

	String setter();
}
