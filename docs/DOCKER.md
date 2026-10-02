# OAuth image and callback configuration

This packages the existing Java 21 OAuth service. Full Compose, production
HTTPS, resource sizing, backup/restore and AWS deployment are separate gates.

## Local build (from this repository)

```powershell
.\mvnw.cmd --batch-mode clean verify
docker build --platform linux/amd64 -t technotes-oauth:local .
docker image inspect technotes-oauth:local --format '{{.Os}}/{{.Architecture}} user={{.Config.User}}'
docker run --rm --entrypoint java technotes-oauth:local -version
```

Expected runtime: linux/amd64, Java 21, UID/GID 10001. The last command does
not start OAuth or connect to PostgreSQL. Tests run separately from image packaging.

## Runtime inputs

- DB_URL, DB_USERNAME, DB_PASSWORD: PostgreSQL connection; Flyway owns migrations.
- AUTH_ISSUER: exact browser-facing issuer; local default http://localhost:9000.
- UI_ORIGIN: exact UI origin; local default http://localhost:5173.
- OAUTH_WEB_REDIRECT_URI: exact /auth/callback URL; local default
  http://localhost:5173/auth/callback. Use HTTPS outside loopback.
- OAUTH_KEYSTORE_PATH, OAUTH_KEYSTORE_PASSWORD, OAUTH_KEY_ALIAS,
  OAUTH_KEY_PASSWORD: existing persistent PKCS12 signing key.
- OWNER_EMAIL, OWNER_DISPLAY_NAME, OWNER_PASSWORD: initial private owner provisioning.
- JAVA_TOOL_OPTIONS: choose limits during full-stack measurement.

The configured callback replaces all callbacks on an existing technotes-web
JDBC client at startup, preserving client ID, scopes, grants and settings.
Unchanged callbacks do not write. One database must belong to one environment;
do not point a production instance at the laptop database. A deployment with
multiple instances must supply the same callback to each instance.

Mount the signing keystore read-only outside the image, readable by UID 10001.
Never copy keys, passwords, environment files or tokens into the build context.
Inside containers localhost refers to that container. The final Compose will
use its PostgreSQL service name and durable volumes. Preserve existing laptop
and Notes smoke data; do not delete volumes to initialize OAuth.

Production proxy/secure-cookie behavior, readiness checks, OAuth integration,
restart persistence and database restore must pass before public deployment.
