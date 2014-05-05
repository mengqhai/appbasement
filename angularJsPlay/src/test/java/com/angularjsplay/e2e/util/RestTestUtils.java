package com.angularjsplay.e2e.util;

import java.io.IOException;
import java.lang.reflect.Array;

import org.junit.Assert;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.angularjsplay.model.Backlog;
import com.angularjsplay.model.IEntity;
import com.angularjsplay.mvc.rest.error.RestError;

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

	public static void assertBacklogsEqual(Backlog expected, Backlog backlog) {
		Assert.assertEquals(expected, backlog);
		Assert.assertEquals(expected.getId(), backlog.getId());
		Assert.assertEquals(expected.getName(), backlog.getName());
		Assert.assertEquals(expected.getDesc(), backlog.getDesc());
		Assert.assertEquals(expected.getEstimation(), backlog.getEstimation());
		Assert.assertEquals(expected.getPriority(), backlog.getPriority());
		Assert.assertEquals(expected.getProjectId(), backlog.getProjectId());
		Assert.assertEquals(expected.getCreatedAt().getTime(), expected
				.getCreatedAt().getTime());
		Assert.assertEquals(expected.getSprintId(), backlog.getSprintId());
	}

	public static void assertRestError(RestTemplate rest,
			HttpMethod method, String url, Object request, HttpStatus status) {
		rest.setErrorHandler(new DefaultResponseErrorHandler() {
			@Override
			public void handleError(ClientHttpResponse response)
					throws IOException {
				// do nothing
			}
		});

		ResponseEntity<RestError> response = null;
		switch (method) {
		case POST:
			response = rest.postForEntity(url, request, RestError.class);
			break;
		case GET:
			response = rest.getForEntity(url, RestError.class);
			break;
		default:
			Assert.fail("Unsupported method:" + method);
		}

		Assert.assertEquals(status, response.getStatusCode());
		RestError error = response.getBody();
		Assert.assertEquals(response.getStatusCode().value(), error.getCode());
		Assert.assertNotNull(error.getMessage());
	}
}
