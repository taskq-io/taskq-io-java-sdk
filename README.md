This is [TaskQ.io](https://taskq.io) SDK for Java.

This page explains how to use the SDK, but does not discuss all aspects of TaskQ.io. Please visit [TaskQ.io](https://taskq.io) to get a full picture.

# Installation

To install the SDK in your project using Maven, use:

```xml
<dependency>
	<groupId>io.taskq</groupId>
	<artifactId>taskq-io-java-sdk</artifactId>
	<version>0.0.1</version>
</dependency>
```

# Usage

## Queueing tasks

```java
TaskQ taskQ = new TaskQ();

// Optional - TaskQ will default to System.getenv("TASKQ_API_KEY") as set by Heroku
taskQ.setApiKey("1o2TzlloDCZK8PioXjocb5xm1A8GU5ItVR9u0ND682cKjy1GBH");

taskQ.useUrl("/tasks/sync_user")
	 .withParam("user_id", "L1mxeDbCIdv7COIUjuJ9")
	 .queue();
```

## Running tasks

The example above will make TaskQ execute `POST` request to `https://yourapp.herokuapp.com/tasks/sync_user` with JSON payload:

```json
{
	"user_id": "L1mxeDbCIdv7COIUjuJ9"
}
```

While handling tasks, **always** remember to check `Authorization` header; otherwise somebody else than TaskQ.io might be sending reqests to you! 
The SDK provides convenience method to do that:

```java
taskQ.verify(authorizationHeader);
```

`verify` method will throw an exception in case of invalid `Authorization` header.

Below is an example of running tasks using [Spring](https://spring.io/):

```java
@RestController
@RequestMapping("/tasks")
public class TasksController {

	private final TaskQ taskQ;
	
	@Autowired
	public TasksController(TaskQ taskQ) {
		this.taskQ = taskQ;
	}

	@RequestMapping(value="/sync_user", method=HttpMethod.GET)
	public void syncUser(@RequestBody UserIdDTO userIdDTO, @RequestHeader("Authorization") String authorization) {
		taskQ.verify(authorization);
		...
	}
}

public class UserIdDTO {
	
	private final String userId;
	
	@JsonCreator
	public UserIdDTO(@JsonProperty("user_id") String userId) {
		this.userId = userId;
	}
	
	public String getUserId() {
		return userId;
	}
}
```

Your feedback is very welcome! Please use Github's issue tracker to report issues, request features, etc.