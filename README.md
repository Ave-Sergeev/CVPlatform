## CVPlatform
Preparing a project for MVP. Technologies are tested there for further use.
At this point, the core logic has been replaced with a simple CRUD for the user profile.

The project uses:
1) Authentication using KeyCloak
2) Working with PostgreSQL as a database via Quill
3) Migrations are carried out via Liquibase
4) Working with configuration (via ZIO-Config)
5) HTTP and gPRC endpoints (via ZIO-HTTP, ZIO-gRPC)
6) Logging (via ZIO-Logging + SLF4J2)
7) Working with Redis (via ZIO-Redis)
8) Handler layer for RPC and REST (with trace annotation and metrics counting) (via ZIO-Aspect)
9) Collecting metrics for Prometheus (via ZIO-Metrics)
10) Using ULID as a more advanced and compatible analogue of UUID
11) And other technologies

### Local deployment

To deploy the project locally, you need to:
1) Go to the `/docker` directory.
2) Add your KeyCloak to the `docker-compose.yml` file (there is no configuration for it by default).
3) Run containers of dependent services using the `docker compose up` command.
4) Override Environment variables for the DB(PostgreSQL), Keycloak and Redis.
5) Run the project from the `Main.scala` file.

### API
...
