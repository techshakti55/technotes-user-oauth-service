# technotes-user-oauth-service

Generated privately in the browser by TechNotes Initializr. This is a scaffold, not a finished service.

Java 21; Boot 3.5.16; Cloud 2025.0.3 when selected.

1. Extract to a new folder and open pom.xml in IntelliJ. Choose JDK 21.
2. Reload Maven; dependency downloads require internet.
3. For SQL: install the selected database locally, create the database in application.yml, and set DB_USERNAME/DB_PASSWORD in Run Configuration. DB_URL is optional. Add Flyway migrations before JPA entities.
4. For MongoDB: start local MongoDB, optionally set MONGODB_URI.
5. Build on Windows with `.\mvnw.cmd clean package`; Linux: `chmod +x mvnw && ./mvnw clean package`.
6. Run the Application main class after local prerequisites are ready.

No business tests are generated. Implement and test your APIs. OAuth requires PKCE configuration, persistent signing keys/authorization state, private owner provisioning and /users/me; see the fixed contract for the OAuth preset. No signup API is generated. Gateway routes require configuration.

Update the generator source through GitHub; existing generated services do not update automatically.
