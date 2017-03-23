package com.haoxin.emctest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.haoxin.emctest.model.Lun;
import com.haoxin.emctest.model.LunCreationReq;
import com.haoxin.emctest.repository.LunRepository;

/**
 * For testing the web layer.
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class LunSimulatorApplicationTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private LunRepository lunRepository;

	private Set<Long> toCleanUp = new HashSet<Long>();

	@SuppressWarnings("rawtypes")
	@Test
	public void testCreateAndGetLun() {

		List<LunCreationReq> lunCreationReqs = new ArrayList<LunCreationReq>();
		lunCreationReqs.add(new LunCreationReq(500));
		lunCreationReqs.add(new LunCreationReq(600));
		List<?> createdLunList = restTemplate.postForObject(getResourceUrl(), lunCreationReqs, List.class);
		Assert.assertEquals(2, createdLunList.size());

		createdLunList.forEach(lunObj -> {
			Assert.assertTrue(lunObj instanceof Map);
			long id = ((Number) ((Map) lunObj).get("id")).longValue();
			long size = ((Number) ((Map) lunObj).get("size")).longValue();
			Assert.assertTrue(size == 500 || size == 600);
			toCleanUp.add(id);

			Lun retrievedLun = restTemplate.getForObject(getResourceUrl() + id, Lun.class);
			Assert.assertEquals(size, retrievedLun.getSize());
		});
	}

	@Test
	public void testCreateInvalidParams() {
		List<LunCreationReq> lunCreationReqs = new ArrayList<LunCreationReq>();
		lunCreationReqs.add(new LunCreationReq(-500)); // negative size
		ResponseEntity<String> responseEntity = restTemplate.postForEntity(getResourceUrl(), lunCreationReqs,
				String.class);
		Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testDeleteLun() {
		List<LunCreationReq> lunCreationReqs = new ArrayList<LunCreationReq>();
		lunCreationReqs.add(new LunCreationReq(500));
		List<?> createdLunList = restTemplate.postForObject(getResourceUrl(), lunCreationReqs, List.class);

		Assert.assertEquals(1, createdLunList.size());
		createdLunList.forEach(lunObj -> {
			Assert.assertTrue(lunObj instanceof Map);
			long id = ((Number) ((Map) lunObj).get("id")).longValue();
			restTemplate.delete(getResourceUrl() + id);
			ResponseEntity<String> responseEntity = restTemplate.getForEntity(getResourceUrl() + id, String.class);
			// no longer there
			Assert.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
		});
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testUpdateLun() {
		List<LunCreationReq> lunCreationReqs = new ArrayList<LunCreationReq>();
		lunCreationReqs.add(new LunCreationReq(500));
		List<?> createdLunList = restTemplate.postForObject(getResourceUrl(), lunCreationReqs, List.class);

		Assert.assertEquals(1, createdLunList.size());
		createdLunList.forEach(lunObj -> {
			Assert.assertTrue(lunObj instanceof Map);
			long id = ((Number) ((Map) lunObj).get("id")).longValue();

			Lun toUpdate = new Lun(800);
			toUpdate.setId(id);
			restTemplate.put(getResourceUrl(), toUpdate);

			Lun lun = lunRepository.findOne(id);
			Assert.assertEquals(800, lun.getSize());
			toCleanUp.add(id);
		});
	}

	@After
	public void cleanup() {
		toCleanUp.forEach(id -> {
			lunRepository.delete(id);
		});
		toCleanUp.clear();
	}

	private String getResourceUrl() {
		return "http://localhost:" + port + "/lun/";
	}
}
