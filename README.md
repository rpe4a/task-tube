# Task-tube
Simple consistent workflow-task manager.

The application contains:
- server:
  - ui
  - api
  - workers
- client
  - Java SDK

## Persistence
Use SQL database to get ACID.

###  Postgresql
To connect to local database use docker:
```
docker run --name postgres-container -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=12345 -e POSTGRES_DB=task_tube -p 5432:5432 -d postgres:latest
```