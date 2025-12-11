# Task-tube
A simple, consistent workflow-task manager.

Contents:
- server
  - domain: core entities, value objects, and ports (interfaces)
  - application: use-cases/services implementing business logic
  - infrastructure: database repositories, Spring configuration, SQL DDL
  - api: Spring Boot REST API exposing HTTP endpoints
  - workers: Spring Boot app with scheduled background jobs

Database schema:
- See *server/infrastructure/src/main/resources/V1_init.sql* for table DDL (tasks, barriers).

## Configuration
- Both API and Workers use Spring Boot application.yml within their module and read DB settings from properties:
  - spring.datasource.url = ${postgresql.url}
  - spring.datasource.username = ${postgresql.admin}
  - spring.datasource.password = ${postgresql.password}
- Default values are provided via Maven properties in the root pom.xml:
  - postgresql.url = jdbc:postgresql://localhost:5432/task_tube
  - postgresql.admin = admin
  - postgresql.password = 12345

Workers configuration (server/workers/src/main/resources/application.yml)
- spring.application.jobs.tasks.scheduling.delay (fixedDelay ms)
- spring.application.jobs.tasks.scheduling.count
- spring.application.jobs.finalizing.delay, .count
- spring.application.jobs.locked.delay, .count, .timeoutSeconds
- spring.application.jobs.barriers.releasing.delay, .count
- spring.application.jobs.barriers.locked.delay, .count, .timeoutSeconds

## Run locally
### Prerequisites
- Java 21
- Maven 3.9+
- Docker (for Postgres)

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
