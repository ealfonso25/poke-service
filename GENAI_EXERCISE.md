# 🤖 Generative AI Technical Exercise: Task Management API Scaffold

This document outlines the precision prompting, architectural auditing, and defensive software engineering principles applied to scaffold an enterprise-ready Task Management RESTful API leveraging Generative AI.

---

## 1. High-Precision Structural Prompt

To ensure the Generative AI (Cursor / Claude Code) adhered strictly to **Clean Architecture**, **SOLID principles**, and **Java 25 features** without generating tightly-coupled monolithic code, the following architectural context prompt was engineered:

> **Prompt:**
> "Act as a Lead Software Architect specializing in Java 25, Spring Boot 4.1.0, and Clean Architecture. Generate the full implementation scaffold for a simple Task Management system supporting standard CRUD operations for a `Task` entity.
>
> Attributes: `id`, `title`, `description`, `status` (as a domain Enum), `due_date`, and `userId`. Assume a basic `User` entity already exists.
>
> Strict Architectural Rules:
> 1. Decouple the project into two distinct root directories: `internal` (Pure Java domain layer, absolutely zero Spring or framework imports) and `infrastructure` (Spring Boot details, web controllers, and data adapters).
> 2. Inside `internal`, structure the `Task` domain model as an immutable Java Record, expose a `TaskRepositoryPort` interface, and write completely independent use case classes: `CreateTaskUseCase`, `GetTaskUseCase`, `UpdateTaskUseCase`, and `DeleteTaskUseCase`.
> 3. Inside `infrastructure`, provide a centralized `@RestControllerAdvice` for global exception mapping and a REST Controller that injects the use case boundary interactors.
> 4. Enforce strict defensive programming. Throw explicit domain exceptions if a resource is missing (404) or data payloads are malformed (400). For security, assume the active `userId` is supplied via a simulated HTTP Header named `X-User-Id` to keep authentication decoupled."

---

## 2. Generated Code Sample (Domain Layer Execution)

The AI successfully generated the boundary interactors. Below is a representative code sample of the core **Domain Layer Use Case** executing business rule orchestrations independently of any database engine or framework:

### `internal/task/usecase/UpdateTaskUseCase.java`
```java
package com.tuproyecto.task.internal.task.usecase;

import com.tuproyecto.task.internal.task.domain.Task;
import com.tuproyecto.task.internal.task.domain.exceptions.TaskNotFoundException;
import com.tuproyecto.task.internal.task.domain.exceptions.InvalidTaskPayloadException;
import com.tuproyecto.task.internal.task.ports.output.TaskRepositoryPort;
import java.time.LocalDate;

public class UpdateTaskUseCase {
    private final TaskRepositoryPort repositoryPort;

    public UpdateTaskUseCase(TaskRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    public Task execute(String taskId, Task request, String currentUserId) {
        // 1. Initial Structural Validation (Defensive Check for 400 Bad Request)
        if (request.title() == null || request.title().trim().isEmpty()) {
            throw new InvalidTaskPayloadException("Task title is mandatory and cannot be blank.");
        }
        
        if (request.dueDate() != null && request.dueDate().isBefore(LocalDate.now())) {
            throw new InvalidTaskPayloadException("The task due date cannot be set in the past.");
        }

        // 2. Resource Existence Verification (Defensive Check for 404 Not Found)
        Task existingTask = repositoryPort.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        // 3. Multi-Tenant Scoped Security Validation (BOLA Protection)
        if (!existingTask.userId().equals(currentUserId)) {
            throw new SecurityException("Unauthorized: You do not possess ownership permissions to mutate this task resource.");
        }

        // 4. Return persistent mutated entity state
        return repositoryPort.save(request);
    }
}
```

---

## 3. Engineering Audit, Verification, and Enhancements

AI-generated code requires human oversight. As a Lead Engineer, I rigorously audited, corrected, and enhanced the AI outputs based on three core production-level criteria:

### A. AI Suggestion Validation
*   **Domain Isolation Audit:** Checked package imports across the `internal/` scope to verify complete agnosticism. No `org.springframework.*` references leaked into the business core.
*   **Java 25 Protocol Compliance:** Confirmed the AI utilized modern data modeling (Java Records instead of archaic boilerplate-heavy getters/setters classes) to enforce deep immutability during runtime context switching.
*   **Error Mapping Integrity:** Verified that custom exceptions thrown within the use case bounds successfully bubbled up and mapped to real HTTP Status Codes (`400` and `404`) in the infrastructure's `@RestControllerAdvice`.

### B. Human Interventions & Architectural Adjustments
While the AI produced a functional blueprint, it committed **two critical architectural violations** that required immediate manual correction:
1.  **Entity Leakage Prevention:** In its initial iteration, the AI attempted to pass a Spring Data `@Entity` class directly into the use case constructor. I intervened and split this into two entities: a pure `Task` Record for domain mechanics and a `TaskEntity` for persistence mapping, bridging them via a mapper inside the infrastructure adapter.
2.  **Decoupling DI Container:** The AI tried to decorate domain classes with Spring's `@Service` and `@Autowired` annotations. I manually purged those framework dependencies from the domain and created a factory `@Configuration` class in the infrastructure layer to wire the beans cleanly.

### C. Advanced Edge Cases, Validations, and Security Hardening
To transform the basic scaffold into an enterprise-grade secure API, I instructed the AI to incorporate defensive mechanics handling real-world threat vectors:
*   **Temporal Chronology Edge Case:** Implemented defensive code checking `due_date` chronologies against `LocalDate.now()`. This guarantees that task creation rejects past timelines with an explicit `400 Bad Request`.
*   **Broken Object Level Authorization (BOLA/IDOR) Hardening:** A common AI flaw is allowing database mutations if a user simply guesses a `taskId`. I forced an explicit equality match between `existingTask.userId()` and `currentUserId`. If a mismatch occurs, the execution chain aborts immediately, mitigating potential cross-tenant data leaks.
*   **Partial Updates Payload Safe-Merging:** Configured persistence-level mapping guards ensuring that during `PATCH` operations, blank or null attributes in the incoming JSON request do not erase existing data values inside the storage engine, applying a safe object-merge algorithm.
