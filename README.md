# Persistent Chap Web Application (pchat)

This sample persistent chat application uses Quarkus.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.


## Running the application in dev mode


### Prerequisites

* Java 17+

* Maven 3.8.x+

* Docker or Podman (for container build)

* A PostgreSQL database instance (ensure it's accessible and update application.properties with its connection details).

### Running postgres db locally in a container
> **_NOTE:_**  Application expects to have a postgres db running on localhost on port 5432 when running locally
This requires registering with registry.redhat.io to be able to pull the appropriate UBI backed container image

**Usage:**
```bash

# Run Red Hat UBI based container image with Postgres
podman run -d --name postgresql_database -e POSTGRESQL_USER=pchat_user -e POSTGRESQL_PASSWORD=RedH@tPassw0rd -e POSTGRESQL_DATABASE=pchat -p 5432:5432 registry.redhat.io/rhel8/postgresql-16
```

You can then run your application in dev mode that enables live coding using:

```shell script
mvn quarkus:dev
```
### Accessing the web application:
Open your web browser and navigate to http://localhost:8080/.

You can open multiple browser tabs with different usernames to test the chat functionality. Messages will be persisted in the PostgreSQL database.

## Packaging and running the application

The application can be packaged using:

```shell script
mvn package
```

It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
mvn package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
mvn package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
mvn package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/pchat-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/maven-tooling>.

## Related Guides

- Hibernate ORM with Panache ([guide](https://quarkus.io/guides/hibernate-orm-panache)): Simplify your persistence code for Hibernate ORM via the active record or the repository pattern
- WebSockets Next ([guide](https://quarkus.io/guides/websockets-next-reference)): Implementation of the WebSocket API with enhanced efficiency and usability
- Agroal - DB connection pool ([guide](https://quarkus.io/guides/datasource)): JDBC Datasources and connection pooling
- JDBC Driver - PostgreSQL ([guide](https://quarkus.io/guides/datasource)): Connect to the PostgreSQL database via JDBC
- REST Jackson ([guide](https://quarkus.io/guides/rest#json-serialisation)): Jackson serialization support for Quarkus REST. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it
