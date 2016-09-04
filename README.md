This is [TaskQ.io](https://taskq.io) SDK for Java.

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

// Optional - TaskQ will default to System.getProperty("TASKQ_API_KEY") as set by Heroku
taskQ.setApiKey("1o2TzlloDCZK8PioXjocb5xm1A8GU5ItVR9u0ND682cKjy1GBH");

taskQ.useUrl("/tasks/sync_user")
	 .withParam("user_id", "L1mxeDbCIdv7COIUjuJ9 value")
	 .queue();
```

## Running tasks

The example above will make TaskQ execute `POST` request to `https://yourapp.herokuapp.com/tasks/sync_user` with JSON payload:

```json
{
	"user_id": "L1mxeDbCIdv7COIUjuJ9"
}
```

**Always** remember to check `Authorization` header; otherwise somebody else than TaskQ might be sending reqests to you! 
The SDK provides convenience method to do that:

```javascript
TaskQ taskQ = new TaskQ();
...
taskQ.verify(authorizationHeader);
```

Example using [Spring](https://spring.io/):

```javascript
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