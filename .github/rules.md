# Protocolo 360 - Development Rules

## Tech Stack
- Front-end: Angular 21
- Back-end: Java 25 (LTS)
- Framework: Spring Boot 4
- Build Tool: Maven (standard)
- ORM: Spring Data JPA
- Database: PostgreSQL (Neon.tech)
- Security: Spring Security + JWT
- Validations: Hibernate Validator (Bean Validation)

## ☕ Protocolo 360 - Backend Development Rules (Spring Boot 4)
## 🏗️ Architectural Principles
- Layered Architecture: Strictly enforce the Controller -> Service -> Repository flow.
- Statelessness: All authentication must be stateless using JWT. Server-side sessions are forbidden.
- Auditability: Every @Entity MUST extend BaseEntity to inherit createdAt and updatedAt.
- Logic Placement: Keep business logic (TDEE, Macros, math) in Services; keep Controllers lean.

## 🚀 Java 25 & Modern Spring Standards
- Records for DTOs: Use Java Records for all request and response payloads. Avoid traditional classes for data transfer.
- Virtual Threads: Leverage @EnableVirtualThreads (Standard in Spring Boot 4) for high-concurrency I/O.
- Modern Syntax: Use modern switch expressions and Java 25 flexible constructor bodies for validation.
- Jakarta EE 11: Use jakarta.validation.constraints for strict DTO validation (e.g., @NotBlank, @Min).

## 💾 Database & Persistence (PostgreSQL/Neon)
- JPA Auditing: Use @CreatedDate and @LastModifiedDate on BaseEntity fields.
- Soft Deletes: Implement using an active boolean column combined with @SQLDelete and @Where(clause = "active = true").

## Naming Conventions:
- Database: snake_case for all table and column names.
- Java: camelCase for all fields and methods.
- Migrations: All schema changes MUST be managed via Flyway (src/main/resources/db/migration).

## 🛡️ Security & API Design
- RESTful Verbs: Strictly follow semantic HTTP verbs (GET, POST, PUT, PATCH, DELETE).
- Error Handling: Use @RestControllerAdvice to return ProblemDetail (RFC 7807) standardized responses.
- RBAC: Protect sensitive endpoints using @PreAuthorize("hasRole('ADMIN')").
- Versioning: All API routes must be prefixed with /api/v1/.

## 🤖 Copilot Generation Preferences
## Dependency Injection
- Always use Constructor Injection with private final fields.
- Use @RequiredArgsConstructor from Lombok to generate the constructor.
- Forbidden: Field injection via @Autowired.

## Entity Template
- Use Lombok (@Data, @Builder, @NoArgsConstructor, @AllArgsConstructor) for entities.
- Ensure @EqualsAndHashCode(callSuper = true) is present on entities extending BaseEntity.

## Testing Standards
- Use JUnit 5 and AssertJ for expressive assertions.
- Use @DataJpaTest for repository layer and @WebMvcTest for controller layer testing.
