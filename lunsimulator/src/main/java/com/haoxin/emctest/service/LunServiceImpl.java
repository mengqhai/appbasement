package com.haoxin.emctest.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.haoxin.emctest.exception.LunNotFoundException;
import com.haoxin.emctest.model.Lun;
import com.haoxin.emctest.repository.LunRepository;


@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@Transactional(propagation = Propagation.REQUIRED, value = "transactionManager")
public class LunServiceImpl implements LunService {
	
	private static final Logger LOGGER = Logger.getLogger(LunServiceImpl.class); 
	
	@Autowired
	private LunRepository lunRepository;
	
	/**
	 * Create a new LUN with the given size/ 
	 * 
	 * @param size in KB
	 * 
	 * @return the created LUN object with the id generated.
	 */
	@Override
	public Lun createLun(long size) {
		Lun lun = new Lun(size);
		lunRepository.save(lun);
		return lun;
	}
	
	/**
	 * Find one LUN by id
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public Lun findOneLun(long id) {
		Lun lun = getLunRepository().findOne(id);
		if (lun ==null) {
			throw new LunNotFoundException("LUN not found");
		}
		return lun;
	}
	
	/**
	 * Update a LUN.
	 */
	@Override
	public Lun upateLun(Lun lun) {
		Lun targetLun = getLunRepository().findOne(lun.getId());
		if (targetLun == null) {
			throw new LunNotFoundException("LUN not found");
		}
		
		targetLun.setSize(lun.getSize()); // auto saved when transaction ends
		return targetLun;
	}
	
	/**
	 * Delete one LUN by id
	 * 
	 * @param id
	 */
	@Override
	public void deleteLun(Long id) {
		try {
			getLunRepository().delete(id);
		} catch (EmptyResultDataAccessException e) {
			LOGGER.warn("LUN not found for given id: "+id);
			throw new LunNotFoundException("LUN not found", e);
		}
	}
	
	@Override
	public List<Lun> findAllLuns() {
		Iterable<Lun> lunIterable = getLunRepository().findAll();
		if (lunIterable instanceof List) {
			return (List<Lun>)lunIterable;
		} else {
			List<Lun> lunList = new ArrayList<Lun>();
			lunIterable.forEach(lun -> {
				lunList.add(lun);
			});
			return lunList;
		}
	}

	/**
	 * @return the lunRepository
	 */
	public LunRepository getLunRepository() {
		return lunRepository;
	}

	/**
	 * @param lunRepository the lunRepository to set
	 */
	public void setLunRepository(LunRepository lunRepository) {
		this.lunRepository = lunRepository;
	}
	
	

}
