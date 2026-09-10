# Cloud Task Manager

Cloud Task Manager is a Spring Boot web application that stores tasks in MySQL. It provides Bootstrap pages for creating, viewing, editing, and deleting task records.

## Activity 2 improvements

- Centralized 404 and 500 error handling with a Bootstrap error page
- Server-side validation for the title, description, due date, and status
- Detailed controller and service logging without logging database credentials
- Automated service, controller, and model-validation tests
- Environment-based cloud configuration, a production profile, and a health endpoint

## Requirements

- Java 17
- MySQL 8 or later
- The `cloud_task_managerDB` schema

## Local configuration

The application reads its database password from the `DB_PASSWORD` environment variable. On Windows, save it once as a user environment variable:

```powershell
setx DB_PASSWORD "YOUR_MYSQL_PASSWORD"
```

Close and reopen Visual Studio Code after using `setx`. Do not place the real password in `application.properties` or commit it to Git.

Optional environment variables are:

- `DB_URL`: complete JDBC connection URL
- `DB_USERNAME`: database username; defaults to `root`
- `PORT`: web server port; defaults to `8080`
- `DDL_AUTO`: Hibernate schema mode; defaults to `update` locally
- `APP_LOG_LEVEL`: project logging level; defaults to `INFO`

## Run and test

Run all automated tests:

```powershell
.\mvnw.cmd clean test
```

Start the application:

```powershell
.\mvnw.cmd spring-boot:run
```

Open `http://localhost:8080`. The health endpoint is available at `http://localhost:8080/actuator/health`.

## Production profile

For a cloud environment, configure `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, and `PORT` in the provider's application settings. Activate the production profile with `SPRING_PROFILES_ACTIVE=prod`.

The production profile defaults `DDL_AUTO` to `validate`. For the first deployment to an empty cloud database, set `DDL_AUTO=update` so Hibernate can create the table, then change it to `validate` after the schema exists.
