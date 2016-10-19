package io.taskq.sdk;

import org.junit.Test;

public class TaskQTest {

	@Test
	public void positive() {
		final TaskQ taskQ = new TaskQ().setApiKey("lows6ysX7D482sAb87p0j5oYsvdFBroDtSOlN5tEbaL1Dv7X4gz0G25B0XG7Xcrh");
		taskQ.useUrl("/tasks/unit-tests")
				.withParam("sample_param", "sample value")
				.withParam("param2", "lol")
				.queue();
	}

	@Test
	public void positive_no_params() {
		final TaskQ taskQ = new TaskQ().setApiKey("lows6ysX7D482sAb87p0j5oYsvdFBroDtSOlN5tEbaL1Dv7X4gz0G25B0XG7Xcrh");
		taskQ.useUrl("/tasks/unit-tests").queue();
	}

	@Test(expected = TaskQException.class)
	public void unauthorized() {
		final TaskQ taskQ = new TaskQ().setApiKey("invalid");
		taskQ.useUrl("/tasks/unit-tests")
				.withParam("sample_param", "sample value")
				.withParam("param2", "lol")
				.queue();
	}
}