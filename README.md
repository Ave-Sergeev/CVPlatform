# CertVerifyPlatform
Preparing a project for MVP. Technologies are tested there for further use.
At this point, the core logic has been replaced with a simple CRUD for the user profile.

The project uses:
1) Working with configuration (via ZIO-Config)
2) Authentication using KeyCloak
3) HTTP and gPRC endpoints (via ZIO-http, ZIO-grpc)
3) Logging (via ZIO-Logging + SLF4J2)
4) Working with the database (via ZIO-Quill)
5) Working with Redis (via ZIO-Redis)
6) Handler layer for PRC and REST (with trace annotation) (via ZIO-Aspect)
