package com.appbasement.persistence.util;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

public class UtUtil {

	public static Date parseTimestamp(String timeStampStr) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.parse(timeStampStr);
		} catch (ParseException e) {
			throw new RuntimeException("Unable to parse timestamp:"
					+ timeStampStr, e);
		}
	}

	public static Object[] mergeArray(Object[]... arrays) {
		List<Object> list = new LinkedList<Object>();
		for (Object[] array : arrays) {
			list.addAll(Arrays.asList(array));
		}
		return (Object[]) list.toArray(new Object[list.size()]);
	}

	/**
	 * Set the {@link Field field} with the given {@code name} on the provided
	 * {@link Object target object} to the supplied {@code value}.
	 * 
	 * <p>
	 * This method traverses the class hierarchy in search of the desired field.
	 * In addition, an attempt will be made to make non-{@code public} fields
	 * <em>accessible</em>, thus allowing one to set {@code protected},
	 * {@code private}, and <em>package-private</em> fields.
	 * 
	 * @param target
	 *            the target object on which to set the field
	 * @param name
	 *            the name of the field to set
	 * @param value
	 *            the value to set
	 * @param type
	 *            the type of the field (may be {@code null})
	 * @see ReflectionUtils#findField(Class, String, Class)
	 * @see ReflectionUtils#makeAccessible(Field)
	 * @see ReflectionUtils#setField(Field, Object, Object)
	 */
	public static void setField(Object target, String name, Object value,
			Class<?> type) {
		Assert.notNull(target, "Target object must not be null");
		Field field = ReflectionUtils.findField(target.getClass(), name, type);

		// SPR-9571: inline Assert.notNull() in order to avoid accidentally
		// invoking
		// toString() on a non-null target.
		if (field == null) {
			throw new IllegalArgumentException(String.format(
					"Could not find field [%s] of type [%s] on target [%s]",
					name, type, target));
		}
		ReflectionUtils.makeAccessible(field);
		ReflectionUtils.setField(field, target, value);
	}

	/**
	 * Get the field with the given {@code name} from the provided target
	 * object.
	 * 
	 * <p>
	 * This method traverses the class hierarchy in search of the desired field.
	 * In addition, an attempt will be made to make non-{@code public} fields
	 * <em>accessible</em>, thus allowing one to get {@code protected},
	 * {@code private}, and <em>package-private</em> fields.
	 * 
	 * @param target
	 *            the target object on which to set the field
	 * @param name
	 *            the name of the field to get
	 * @return the field's current value
	 * @see ReflectionUtils#findField(Class, String, Class)
	 * @see ReflectionUtils#makeAccessible(Field)
	 * @see ReflectionUtils#setField(Field, Object, Object)
	 */
	public static Object getField(Object target, String name) {
		Assert.notNull(target, "Target object must not be null");
		Field field = ReflectionUtils.findField(target.getClass(), name);
		Assert.notNull(field, "Could not find field [" + name + "] on target ["
				+ target + "]");
		ReflectionUtils.makeAccessible(field);
		return ReflectionUtils.getField(field, target);
	}

}
