package com.workstream.rest.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

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
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotRemovableValidator.class)
public @interface NotRemovable {

	String message() default "{com.workstream.rest.validation.NotRemovable}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	String[] value() default {};

}
