package com.workstream.rest.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.workstream.rest.model.MapPropObj;

/**
 * See
 * http://docs.spring.io/spring/docs/current/spring-framework-reference/html/
 * validation.html#validation-beanvalidation and
 * https://docs.jboss.org/hibernate
 * /validator/4.0.1/reference/en/html/validator-customconstraints.html
 * 
 * @author qinghai
 * 
 */
public class NotRemovableValidator implements
		ConstraintValidator<NotRemovable, MapPropObj> {

	private String[] attributeKeys;

	@Override
	public void initialize(NotRemovable constraintAnnotation) {
		this.attributeKeys = constraintAnnotation.value();
	}

	@Override
	public boolean isValid(MapPropObj value, ConstraintValidatorContext context) {
		boolean valid = true;
		for (String key : attributeKeys) {
			if (value.getPropMap().containsKey(key)
					&& (value.getPropMap().get(key) == null)) {
				valid = false;
				break;
			}
		}
		return valid;
	}
}
