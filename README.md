# Project 05

Spring Boot Postgres - CQRS (Multiple Database)

[https://gitorko.github.io/post/distributed-locking-postgres](https://gitorko.github.io/post/distributed-locking-postgres)

### Version

Check version

```bash
$java --version
openjdk 21.0.3 2024-04-16 LTS
```

### Postgres DB

Start 2 Database Servers

```bash
docker-compose -f docker/docker-compose.yaml up -d
```

Enable replication after liquibase creates the tables, run this only after spring boot application is started.

```bash
docker-compose -f docker/enable-publication.yaml up -d
docker-compose -f docker/enable-subscription.yaml up -d
```

Command to verify replication

```bash
docker exec -it pg-source psql -U test -d source_db -c "SELECT * FROM pg_roles WHERE rolname = 'replicator';"
docker exec -it pg-source psql -U test -d source_db -c "SELECT * FROM pg_publication;"
docker exec -it pg-target psql -U test -d target_db -c "SELECT * FROM pg_subscription;"

docker exec -it pg-source psql -U test -d source_db -c "SELECT * FROM pg_replication_slots;"
docker exec -it pg-target psql -U test -d target_db -c "SELECT * FROM pg_stat_subscription;"

docker exec -it pg-source psql -U test -d source_db -c "SELECT * FROM pg_create_logical_replication_slot('employee_slot', 'pgoutput');"
```

Clean up

```bash
docker-compose -f docker/docker-compose.yaml down --rmi all --remove-orphans --volumes
```

### Dev

To run the backend in dev mode.

```bash
./gradlew clean build
./gradlew bootRun
```
