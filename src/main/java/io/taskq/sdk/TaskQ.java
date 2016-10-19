package io.taskq.sdk;

import net.sf.json.JSONObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public final class TaskQ {

	private static final String TASKQ_API_KEY_ENV_PROPERTY = "TASKQ_API_KEY";

	private String apiKey;
	private CloseableHttpClient httpClient = HttpClients.createDefault();
	private final String userAgent;

	public TaskQ() {
		apiKey = System.getenv(TASKQ_API_KEY_ENV_PROPERTY);
		final String version = getClass().getPackage().getImplementationVersion();
		userAgent = null == version ? "Java SDK" : "Java SDK " + version.trim();
	}

	public TaskQ setApiKey(final String apiKey) {
		this.apiKey = apiKey;
		return this;
	}

	public TaskQ setHttpClient(final CloseableHttpClient httpClient) throws IllegalArgumentException {
		if (null == httpClient) {
			throw new IllegalArgumentException("Wow, have you just tried to set null HttpClient?");
		}
		this.httpClient = httpClient;
		return this;
	}

	public TaskQTaskBuilder useUrl(final String url) {
		return new TaskQTaskBuilder(this, url);
	}

	void queue(final String url, final Map<String, String> params) {
		assertValidApiKey();
		try {
			final HttpPost post = new HttpPost("https://taskq.io/api/v1/tasks");
			post.addHeader("Authorization", "Bearer " + apiKey);
			post.addHeader("User-Agent", userAgent);
			post.setEntity(new StringEntity(buildJson(url, params), ContentType.APPLICATION_JSON));
			CloseableHttpResponse response = null;
			try {
				response = httpClient.execute(post);
				final int status = response.getStatusLine().getStatusCode();
				if (status >= 200 && status < 300) {
					return;
				}
				final Scanner scanner = new Scanner(response.getEntity().getContent()).useDelimiter("\\A");
				final String result = scanner.hasNext() ? scanner.next() : null;
				if (null == result) {
					throw new TaskQException("TaskQ.io error: " + status);
				}
				final JSONObject errorJson = JSONObject.fromObject(result);
				throw new TaskQException(String.format("TaskQ.io responded with %d error: '%s' (request ID: %s)", status, errorJson.getString("message"), errorJson.getString("request_id")));
			} finally {
				if (null != response) {
					try {
						response.close();
					} catch (final IOException ex) {
					}
				}
			}
		} catch (final Exception ex) {
			if (ex instanceof TaskQException) {
				throw (TaskQException) ex;
			}
			throw new TaskQException("Cannot send your task to TaskQ.io", ex);
		}
	}

	private String buildJson(final String url, final Map<String, String> params) {
		params.put("url", url);
		return JSONObject.fromObject(params).toString();
	}

	public void verify(final String authorization) throws TaskQException {
		assertValidApiKey();
		if (null == authorization) {
			throw new TaskQException("Authorization is null");
		}
		if (!authorization.startsWith("Bearer ")) {
			throw new TaskQException("Invalid Authorization; it should start with 'Bearer' prefix");
		}
		if (!authorization.equals("Bearer " + apiKey)) {
			throw new TaskQException("Invalid Authorization");
		}
	}

	private void assertValidApiKey() {
		if (null == apiKey) {
			throw new TaskQException("Make sure environment property '" + TASKQ_API_KEY_ENV_PROPERTY + "' is set, or set API key manually using TaskQ.setApiKey(...)");
		}
		if (apiKey.trim().length() < 3) {
			throw new TaskQException("API key is set, but is invalid. Please make sure it is correct. Check your '" + TASKQ_API_KEY_ENV_PROPERTY + "' environment property, or set API key manually using TaskQ.setApiKey(...)");
		}
	}
}
