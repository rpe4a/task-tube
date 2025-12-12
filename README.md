# Task-Tube

Task-Tube is a distributed workflow-task manager designed to handle asynchronous job processing. It provides a robust server-side implementation for creating, distributing, and managing the lifecycle of tasks through a simple and powerful REST API.

The system is built with a modern Java stack and follows the principles of Clean Architecture (Hexagonal) to ensure maintainability, scalability, and separation of concerns.

## Core Concepts

The Task-Tube model is built around two primary concepts: **Tubes** and **Tasks**.

- **Tube:** A named queue or channel where tasks are submitted. Workers can subscribe to one or more tubes to retrieve tasks for processing. A tube acts as a FIFO (First-In, First-Out) queue.

- **Task:** A single unit of work that needs to be executed. A task is pushed into a tube and then popped by a worker. Once popped, the worker is responsible for reporting the task's progress through its lifecycle.

### Task Lifecycle

A task progresses through the following states:
1.  **CREATED:** A client submits a task. It is waiting to be popped.
2.  **SCHEDULED:** Task waits to be popped. A worker retrieves a task from the tube. The task is now locked for that worker.
3.  **STARTED:** The worker signals that it has started processing the task.
4.  **PROCESSING:** The worker signals that it has completed the main work.
5.  **FINISHED:** The worker reports the successful completion of the task.
6.  **FAILED:** If an error occurs, the worker reports the task as failed, providing a reason.
7.  **ABORTED:** If task spends all retries.
8.  **COMPLETED:** If task has finished and waited all children.
9.  **CANCELED:** If task cannot get all COMPLETED children or waiting tasks.

### Barriers for Task Coordination

Barriers are a powerful mechanism to manage complex workflows and create dependencies between tasks. A barrier acts as a gate that a task must wait for before it can proceed to the next stage of its lifecycle. Task-Tube supports two types of barriers: `Start Barriers` and `Finish Barriers`.

#### Start Barrier
A `Start Barrier` prevents a task from being scheduled (i.e., being picked up by a worker) until all the tasks it depends on have completed.

-   **Use Case:** Imagine you have a workflow where `Task B` can only begin after `Task A` has finished successfully. You can create `Task B` with a `Start Barrier` that waits for `Task A`. `Task B` will remain in a `CREATED` state until `Task A` is complete, at which point the barrier is released and `Task B` becomes available for workers to `pop`.

#### Finish Barrier
A `Finish Barrier` is used to implement a "fork-join" pattern. It allows a parent task to spawn multiple child tasks and then wait for all of them to complete before the parent task itself is considered complete.

-   **Use Case:** Imagine a video processing task (`Parent Task`) that splits a video into multiple chunks and assigns each chunk to a separate transcoding task (`Child Tasks`). The `Parent Task` can create a `Finish Barrier` that waits for all `Child Tasks`. The parent task will enter a waiting state until all children are done, at which point the barrier is released, and the parent can be marked as `COMPLETED`.

## Architecture

The project follows a Hexagonal (Ports and Adapters) architecture. This separates the core business logic from infrastructure concerns, making the system modular and easier to test and evolve.

The server is divided into the following Maven modules:

-   `domain`: Contains the core business entities, value objects, and output port interfaces (repositories). This module has no external dependencies.
-   `application`: Orchestrates the business logic. It contains the application services (use cases) that implement the input ports defined in the `port.in` package.
-   `infrastructure`: Provides the concrete implementations of the output ports defined in the `domain` module. This includes database repositories (using Spring Data JPA and PostgreSQL), configuration, and other infrastructure-related code.
-   `api`: An input adapter that exposes the application services via a RESTful API using Spring Boot. It handles HTTP requests, authentication, and serialization.
-   `workers`: An input adapter for running background jobs, such as cleaning up stale tasks or managing internal queues.

## Getting Started

### Prerequisites

-   **Java 21** or later
-   **Maven 3.9** or later
-   **PostgreSQL**: A running instance is required for data persistence.

### Database schema:
- See **server/infrastructure/src/main/resources/V1_init.sql** for table DDL (tasks, barriers).


### Configuration

1.  Clone the repository.
2.  Configure the database connection in `server/infrastructure/src/main/resources/application.yml`. Update the following properties if necessary:
    ```yaml
    spring:
      datasource:
        url: jdbc:postgresql://localhost:5432/task_tube
        username: admin
        password: 12345
    ```
3.  The database schema will be automatically created on the first run via Flyway migrations located in `server/infrastructure/src/main/resources/db/migration`.

### Build

To build the project and run tests, execute the following command from the root directory:

```shell
mvn clean package
```

### Running the Application

You can run the REST API and the background workers separately.

### Start Postgres (default credentials)
- docker run --name task-tube-postgres -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=12345 -e POSTGRES_DB=task_tube -p 5432:5432 -d postgres:latest

### Build the project
- mvn -q -DskipTests package

### Run API service
- From repository root:
    - mvn -q -pl server/api -am spring-boot:run
    - or java -jar server/api/target/api-0.0.1-SNAPSHOT.jar

### Run Workers service
- From repository root:
    - mvn -q -pl server/workers -am spring-boot:run
    - or java -jar server/workers/target/workers-0.0.1-SNAPSHOT.jar

## Future Development

-   **Java SDK:** A client-side Java SDK will be developed to simplify interaction with the Task-Tube API, providing an easy-to-use library for both submitting tasks and building workers.
