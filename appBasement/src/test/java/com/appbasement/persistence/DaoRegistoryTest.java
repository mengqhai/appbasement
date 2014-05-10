package com.appbasement.persistence;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:context/context-placeholder.xml",
		"classpath:context/context-persistence.xml" })
public class DaoRegistoryTest {

	@Autowired
	private IDaoRegistry reg;

	@Test
	public void testGetDaoList() {
		Assert.assertNotNull(reg);
		List<IGenericDAO<?, ?>> daoList = reg.getDaoList();

		Assert.assertNotNull(daoList);
		Assert.assertFalse(daoList.isEmpty());
	}

}
