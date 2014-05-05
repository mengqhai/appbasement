package com.angularjsplay.e2e.util;

import java.lang.reflect.Array;

import org.junit.Assert;
import org.springframework.web.client.RestTemplate;

import com.angularjsplay.model.IEntity;

public class RestTestUtils {

	@SuppressWarnings("unchecked")
	public static <T extends IEntity> void assertPagibleChildren(
			RestTemplate rest, String commonUrl, Class<T> childType,
			Long count, Long idOfFirst) {
		Class<T[]> arrayClass = (Class<T[]>) Array.newInstance(childType, 0)
				.getClass();
		T[] entities = rest.getForObject(commonUrl, arrayClass);
		Long actualCount = rest.getForObject(commonUrl + "?count", Long.class);
		Assert.assertEquals(count, actualCount);
		Assert.assertEquals(count, Long.valueOf(entities.length));
		if (count > 0) {
			Assert.assertEquals(idOfFirst, entities[0].getId());
		}

		// test paging
		int max = 3; // 3 items per page
		T[] pagedBacklogs = rest.getForObject(commonUrl
				+ "?first={first}&max={max}", arrayClass, max, max);
		if (count > max) {
			Long expectedFirstId = entities[max].getId();
			int expectedItemCount = (entities.length < max * 2) ? (entities.length - max)
					: max;
			Assert.assertEquals(expectedFirstId, pagedBacklogs[0].getId());
			Assert.assertEquals(entities[max], pagedBacklogs[0]);
			Assert.assertEquals(expectedItemCount, pagedBacklogs.length);
		} else {
			Assert.assertEquals(0, pagedBacklogs.length);
		}
	}
}
