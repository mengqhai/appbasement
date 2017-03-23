package com.haoxin.emctest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.haoxin.emctest.controller.LunController;
import com.haoxin.emctest.exception.LunNotFoundException;
import com.haoxin.emctest.model.Lun;
import com.haoxin.emctest.model.LunCreationReq;
import com.haoxin.emctest.model.ValidList;
import com.haoxin.emctest.repository.LunRepository;

/**
 * For testing the controllers.
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class LunControllerTest {

	@Autowired
	private LunController lunController;

	@Autowired
	private LunRepository lunRepository;

	private Set<Long> toCleanUp = new HashSet<Long>();

	@Test
	public void contextLoads() {
		Assert.assertNotNull(lunController);
	}

	@Test
	public void testCreateAndGetLun() {
		List<Lun> createdLuns = lunController.createLun(new ValidList<LunCreationReq>());
		Assert.assertFalse(createdLuns.iterator().hasNext());

		ValidList<LunCreationReq> lunCreationReqs = new ValidList<LunCreationReq>();
		lunCreationReqs.add(new LunCreationReq(500));
		lunCreationReqs.add(new LunCreationReq(600));
		createdLuns = lunController.createLun(lunCreationReqs);
		Assert.assertTrue(createdLuns.size() == 2);
		createdLuns.forEach(lun -> {
			toCleanUp.add(lun.getId());
			Assert.assertTrue(lun.getId() > 0);
			Assert.assertTrue(lun.getSize() == 500 || lun.getSize() == 600);

			// try to retrieve
			Lun retrievedLun = lunController.getOneLun(lun.getId());
			Assert.assertEquals(lun.getSize(), retrievedLun.getSize());

		});

		List<Lun> allLun = lunController.getLunList();
		Assert.assertTrue(allLun.size() >= 2);
	}

	@Test
	public void testDeleteLun() {
		ValidList<LunCreationReq> lunCreationReqs = new ValidList<LunCreationReq>();
		lunCreationReqs.add(new LunCreationReq(500));
		List<Lun> createdLun = lunController.createLun(lunCreationReqs);
		Assert.assertEquals(1, createdLun.size());
		Lun lun = createdLun.get(0);
		lunController.deleteOnLun(lun.getId());

		boolean notFound = false;
		try {
			lunController.getOneLun(lun.getId());
		} catch (LunNotFoundException e) {
			notFound = true;
		}
		Assert.assertTrue(notFound);
	}

	@Test
	public void testUpdateLun() {
		ValidList<LunCreationReq> lunCreationReqs = new ValidList<LunCreationReq>();
		lunCreationReqs.add(new LunCreationReq(500));
		List<Lun> createdLun = lunController.createLun(lunCreationReqs);
		Assert.assertEquals(1, createdLun.size());
		Lun lun = createdLun.get(0);

		lun.setSize(800);
		Assert.assertEquals(800, lunController.updateLun(lun).getSize());
		Assert.assertEquals(800, lunController.getOneLun(lun.getId()).getSize());
		toCleanUp.add(lun.getId());
	}

	@After
	public void cleanup() {
		toCleanUp.forEach(id -> {
			lunRepository.delete(id);
		});
		toCleanUp.clear();
	}

}
