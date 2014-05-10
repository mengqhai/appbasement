package com.appbasement.persistence;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DaoRegistory implements IDaoRegistry {
	
	@Autowired
	protected List<IGenericDAO<?,?>> daoList;

	@Override
	public List<IGenericDAO<?, ?>> getDaoList() {
		return daoList;
	}
	
	
	
}
