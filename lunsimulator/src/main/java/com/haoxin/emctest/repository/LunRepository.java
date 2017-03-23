package com.haoxin.emctest.repository;

import org.springframework.data.repository.CrudRepository;

import com.haoxin.emctest.model.Lun;

/**
 * No need to implement, Spring Data JPA creates the implementation on the fly.
 *
 */
public interface LunRepository extends CrudRepository<Lun, Long> {
	
}
