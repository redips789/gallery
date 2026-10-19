# Gallery

A web photo gallery: sign in, organise pictures into folders, upload pictures with a name, description and
tags, and search them. Written in 2017 as a training project and modernised in 2026.

## Features

- **Folders**: create, rename and delete folders. A folder can only be deleted once it's empty.
- **Pictures**: upload a picture into a folder (up to 5 MB). The app stores the original, a full-screen copy
  and a thumbnail. You can edit a picture's name, description and tags, or delete it.
- **Search**: find pictures by name, tag, size (e.g. `600x400`), upload date (e.g. `2017-04-06`) or any part of the
  description. Results are shown 8 per page.
- **Sign-in**: every page needs a signed-in user. Passwords are stored as BCrypt hashes.

## Tech stack

| | |
|---|---|
| Language | Java 21 |
| UI | ZK 10.3 (MVVM), Bootstrap 3.4.1 |
| Back end | Spring 7.0, Spring Security 7.1 (XML configuration) |
| Persistence | Jakarta Persistence 3.2, Hibernate 7.4 |
| Database | Oracle; H2 in memory for tests |
| Server | Any Jakarta EE 11 servlet container (Servlet 6.1); Jetty 12 for local runs |
| Tests | JUnit 6 |

## Project layout

| Module | Contents |
|---|---|
| `gallery-model` | JPA entities: `Folder`, `ImageDetails`, `Image` (picture data), `User` |
| `gallery-bl` | Repositories and services, Spring and Spring Security configuration (`META-INF/applicationContext.xml`), `persistence.xml` |
| `gallery-ui` | The web app (WAR): ZK pages (`*.zul`), view models, validators, image scaling |

## Requirements

- JDK 21 or newer
- Maven 3.9 or newer
- An Oracle database with the gallery tables (see [Database](#database))

## Configuration

The database connection is read from environment variables (or JVM system properties of the same name):

| Variable | Required | Default |
|---|---|---|
| `DB_USERNAME` | yes | |
| `DB_PASSWORD` | yes | |
| `DB_URL` | no | `jdbc:oracle:thin:@oracle.insoft.dev:1521:dev` |

The app won't start without `DB_USERNAME` and `DB_PASSWORD`.

## Build and run

Build everything and run the tests:

```sh
mvn clean install
```

The tests use an in-memory H2 database, so they don't need Oracle.

Run the app locally on Jetty:

```sh
# PowerShell
$env:DB_USERNAME="..."; $env:DB_PASSWORD="..."
mvn -pl gallery-ui jetty:run
```

```sh
# bash
DB_USERNAME=... DB_PASSWORD=... mvn -pl gallery-ui jetty:run
```

Then open <http://localhost:8080/>. To deploy elsewhere, use `gallery-ui/target/gallery-ui.war`.

## Database

The app does not create or change tables itself (except for hashing passwords, below). It expects these
tables and the `HIBERNATE_SEQUENCE` sequence, which supplies ids for every table:

| Table | Contents |
|---|---|
| `POV_FOLDER` | Folders; `NAME` is unique |
| `POV_IMAGE_DETAILS` | A picture's name, description, size, upload date and folder |
| `POV_IMAGE` | Picture data (original, full-screen copy, thumbnail) as BLOBs |
| `POV_TAG` | A picture's tags |
| `POV_USER` | Users; `USERNAME` is unique |

`gallery-bl/src/test/resources/schema.sql` creates the same tables for the tests and is the best reference
for their columns.

### Users and passwords

There is no sign-up page: users are added directly to `POV_USER`. Passwords are stored as BCrypt hashes,
for example `{bcrypt}$2a$10$...`, so the `PASSWORD` column must hold at least 68 characters.

To add a user, you can insert the password as plain text. On the next start the app hashes every
plain-text password it finds, so the user can then sign in with that password. A password that is still
plain text is never accepted at sign-in.

## Tests

`mvn test` runs:

- `gallery-bl`: repository and service tests against H2, including search, paging, transactions and passwords
- `gallery-ui`: tests for tag parsing, validators, image scaling and the ZK converters
