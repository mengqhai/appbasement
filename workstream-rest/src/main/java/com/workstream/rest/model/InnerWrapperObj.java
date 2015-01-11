package com.workstream.rest.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.ConstructorUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.workstream.rest.exception.DataMappingException;

@JsonInclude(Include.NON_NULL)
public class InnerWrapperObj<T> {

	protected T inner;

	public InnerWrapperObj(T inner) {
		this.inner = inner;
	}

	@JsonIgnore
	public T getInner() {
		return inner;
	}

	public static <T, V extends InnerWrapperObj<T>> V valueOf(T inner,
			Class<? extends V> type) {
		try {
			return ConstructorUtils.invokeConstructor(type, inner);
		} catch (Exception e) {
			throw new DataMappingException(e);
		}
	}

	public static <T, V extends InnerWrapperObj<T>> List<V> valueOf(
			Collection<T> inners, Class<? extends V> type) {
		List<V> respList = new ArrayList<V>(inners.size());
		for (T inner : inners) {
			V resp = valueOf(inner, type);
			respList.add(resp);
		}
		return respList;
	}

}
