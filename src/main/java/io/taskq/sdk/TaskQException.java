package io.taskq.sdk;

public final class TaskQException extends RuntimeException {

	public TaskQException(final String message) {
		super(message);
	}

	public TaskQException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
