# CertVerifyPlatform
Project preparation for MVP. Technologies are being developed there for further use.
At the moment, the main logic has been replaced by a simple CRUD for the user profile.

The project uses:
1) Working with configuration (via ZIO-Config)
2) Authentication (via Middleware in ZIO-HTTP) using KeyCloak
3) Logging (via ZIO-Logging + SLF4J2)
4) Working with the database (via ZIO-Quill)
5) Working with Redis (via ZIO-Redis)

Planned to add:
1) Adding the ability to work via gRPC API
2) Carrying out requests through Handler with error handling + metrics + logging (via ZIO-Aspect + Metrics)
