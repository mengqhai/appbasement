package com.angularjsplay.e2e.util;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.angularjsplay.model.Backlog;
import com.angularjsplay.model.IEntity;
import com.angularjsplay.model.Task;
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

	public static void assertTaskEqual(Task expected, Task actual) {
		Assert.assertEquals(expected, actual);
		Assert.assertEquals(expected.getId(), actual.getId());
		Assert.assertEquals(expected.getName(), actual.getName());
		Assert.assertEquals(expected.getDesc(), actual.getDesc());
		Assert.assertEquals(expected.getEstimation(), actual.getEstimation());
		Assert.assertEquals(expected.getRemaining(), actual.getRemaining());
		Assert.assertEquals(expected.getState(), actual.getState());
		Assert.assertEquals(expected.getCreatedAt().getTime(), actual
				.getCreatedAt().getTime());
		Assert.assertEquals(expected.getOwnerId(), actual.getOwnerId());
		Assert.assertEquals(expected.getBacklogId(), actual.getBacklogId());
	}

	public static void assertRestError(final RestTemplate rest,
			final HttpMethod method, String url, Object request,
			final HttpStatus status) {
		rest.setErrorHandler(new DefaultResponseErrorHandler() {
			@Override
			public void handleError(ClientHttpResponse response)
					throws IOException {
				Assert.assertEquals(status, response.getStatusCode());
			}
		});

		RestError error = null;
		try {
			switch (method) {
			case POST:
				error = rest.postForObject(url, request, RestError.class);
				break;
			case GET:
				error = rest.getForObject(url, RestError.class);
				break;
			case DELETE:
			case PUT:
				HttpHeaders headers = new HttpHeaders();
				final List<MediaType> jsonType = new ArrayList<MediaType>();
				jsonType.add(MediaType.APPLICATION_JSON);
				headers.setAccept(jsonType);
				headers.setContentType(MediaType.APPLICATION_JSON);
				HttpEntity<Object> requestEntity = new HttpEntity<Object>(
						request, headers);

				error = rest.exchange(url, method, requestEntity,
						RestError.class).getBody();
				break;
			default:
				Assert.fail("Unsupported method:" + method);
			}
		} catch (HttpMessageNotReadableException e) {
			Assert.fail("Response is not RestError");
		}

		Assert.assertNotNull(error);
		Assert.assertEquals(status.value(), error.getCode());
		Assert.assertNotNull(error.getMessage());
	}
}
