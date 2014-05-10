package com.appbasement.persistence;

import java.util.List;

public interface IDaoRegistry {

	public abstract List<IGenericDAO<?, ?>> getDaoList();

}
