package io.taskq.sdk;

import java.util.HashMap;
import java.util.Map;

public final class TaskQTaskBuilder {

	private final TaskQ taskQ;
	private final String url;
	private final Map<String, String> params = new HashMap<String, String>();

	TaskQTaskBuilder(final TaskQ taskQ, final String url) {
		this.taskQ = taskQ;
		this.url = url;
	}

	public TaskQTaskBuilder withParam(final String name, final String value) throws IllegalArgumentException {
		if ("url".equalsIgnoreCase(name)) {
			throw new IllegalArgumentException("'url' parameter is not allowed");
		}
		params.put(name, value);
		return this;
	}

	public void queue() throws TaskQException {
		taskQ.queue(url, params);
	}
}
