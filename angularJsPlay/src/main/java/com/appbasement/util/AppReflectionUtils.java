package com.appbasement.util;

import com.appbasement.exception.AppBasementRTException;

public class AppReflectionUtils {

	@SuppressWarnings("unchecked")
	public static <T> Class<T> getActualClass(T obj) {
		Class<T> beanType = (Class<T>) obj.getClass();
		Class<T> actualBeanType = beanType;
		if (beanType.getName().contains("_$$_javassist")) {
			// javassist proxy
			String className = beanType.getName().split("_\\$\\$_javassist")[0];
			try {
				actualBeanType = (Class<T>) Class.forName(className);
			} catch (ClassNotFoundException e) {
				throw new AppBasementRTException(e);
			}
		}
		return actualBeanType;
	}

}
