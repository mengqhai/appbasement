package com.haoxin.emctest.service;

import java.util.List;

import com.haoxin.emctest.model.Lun;

public interface LunService {

	List<Lun> findAllLuns();

	Lun createLun(long size);

	void deleteLun(Long id);

	Lun findOneLun(long id);

	Lun upateLun(Lun lun);


}
