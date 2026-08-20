# Pokédex Full-Stack Sync (Clean Architecture & Event-Driven)

This project is a modular, production-ready Full-Stack enterprise solution built to query, locally replicate, and enrich dataset entries from the official [PokeAPI](https://pokeapi.co). The system leverages **Clean Architecture (Hexagonal Architecture)** on the Backend to guarantee total technology independence and framework decoupling, combined with an **Event-Driven Design** pattern to guarantee async performance, high-throughput, and immediate UI reactivity.

---

## 🛠️ Technology Stack

### Backend (Core & API)
*   **Java 25** (Utilizing modern features like Records and optimized runtime components).
*   **Spring Boot 4.1.0** (Core framework for infrastructure orchestration and dependency injection).
*   **Spring Data MongoDB** (Polyglot persistence layer for rapid, schema-less local document replication).
*   **Caffeine Cache** (High-performance in-memory cache provider to optimize heavy resource list pagination).
*   **Lombok** (Boilerplate code reduction via compile-time annotations).

### Frontend (User Interface)
*   **React 19** / **TypeScript** (Decoupled client app with strict, static type-safety structures).
*   **Vite** (Next-generation lightning-fast frontend tooling and building server).
*   **Tailwind CSS** (Utility-first CSS framework for reactive, mobile-first layouts).
*   **Lucide React** (Modern, clean, and lightweight vector icon pack).

---

## 🏗️ Architectural Overview

### Backend (Hexagonal Structure)
The backend enforces the **Dependency Inversion Principle (SOLID)**, establishing a rigid barrier where core business rules remain untouched by infrastructure definitions, databases, or frameworks:

```text
com.bla_middleware.poke_service
│
├── internal/                     # Pure Domain Layer (Core Business Rules - NO SPRING IMPORTS)
│   └── pokemon/
│       ├── domain/               # Business Entities, Data Records, and Domain Exceptions
│       ├── ports/
│       │   └── output/           # Output Ports (Repository/Client interface contracts)
│       └── usecase/              # Pure Use Case Interactors (Browse, Details, Update)
│
└── infrastructure/               # Infrastructure Layer (Tech Details - Depends on Domain)
    ├── adapters/
    │   ├── cache/                # Event-driven in-memory cache evictors & listeners
    │   ├── external/pokeapi/     # HTTP Client wrapper utilizing Spring RestClient for PokeAPI
    │   ├── persistence/mongo/    # MongoDB database repository adapter implementation
    │   └── rest/                 # REST Query/Command Controllers and Global Exception Handlers
    └── config/                   # Spring @Configuration Beans (CORS, Thread Pools, Use Case instantiation)
```

### Async Event-Driven Replication Mechanism
To adhere to high-performance user experience criteria, pagination and details data retrieval execute via decoupled asynchrony:

1.  **Cache Hit:** The UI requests a page. If present in **Caffeine's** memory container, it resolves instantly in `0ms`.
2.  **Local Persistence Read:** On a cache miss, **MongoDB** is queried. If documents exist locally, they map and return.
3.  **Batch Replication Event:** If the requested page is empty in the database, the backend fetches minimal resources from PokeAPI, streams a swift placeholder list response to the UI, and simultaneously fires a `PokemonBatchReplicationEvent`.
4.  **Async Task Thread Pool:** A dedicated background `ThreadPoolTaskExecutor` grabs the event, downloads individual data sheets sequentially from PokeAPI on a background thread, and commits them to MongoDB without blocking the user's active thread.
5.  **Reactive Cache Invalidation:** When issuing custom local payload updates (`PATCH`), the database updates, and a `PokemonUpdatedEvent` fires, triggering a strict `@CacheEvict` sequence to wipe page cache pools and guarantee real-time data consistency.

---

## ⚙️ Environment Configuration & Deployment

### Prerequisites
*   **Docker** and **Docker Compose** installed on your host machine.
*   **Java 25 SDK** (or higher) configured in your system path.
*   **Node.js v18** (or higher) and `npm` package manager.

### 1. Launch the Local Infrastructure (Docker)
Open a terminal at the root directory where the `docker-compose.yml` file is saved and execute:

```bash
docker compose up -d
```
*This starts a local **MongoDB 6.0** container bound to the standard port `27017` backed by an independent persistent storage volume.*

### 2. Configure Backend Environment Properties (`application.properties`)
Verify that your Spring Boot configuration properties point to your active Docker container credentials:

```properties
spring.data.mongodb.uri=mongodb://admin:adminpassword@localhost:27017/pokemon_db?authSource=admin
spring.cache.type=caffeine
```

### 3. Run the Backend Application
Boot up the backend application via your preferred IDE or directly through the terminal terminal scripts:
```bash
./mvnw spring-boot:run
```
*The Spring Boot server will initialize and begin listening for requests at `http://localhost:8080`.*

### 4. Build and Run the Frontend Application (React)
Open a new terminal shell inside the frontend project directory and execute the following deployment sequence:

```bash
cd pokemon-ui
npm install
npm run dev -- --force
```
*Vite's local development server will spin up, making the reactive Pokédex UI accessible at `http://localhost:5173`.*

---

## 🎯 API Endpoint Documentation

All REST responses append modern **CORS** permission blocks natively mapped to authorize traffic coming from the React development server root (`http://localhost:5173`).

### 1. Browse Paginated Pokémon List (User Story 01)
*   **HTTP Method & Route:** `GET /api/v1/pokemon`
*   **Query Parameters:** `page` (default: 0), `size` (default: 10)
*   **Details:** Pulls from internal Caffeine memory cache or MongoDB. If a database miss occurs, it queries PokeAPI and automatically wakes up the background replication routine.

### 2. View Comprehensive Detailed Data (User Story 02)
*   **HTTP Method & Route:** `GET /api/v1/pokemon/{nameOrId}`
*   **Details:** Resolves locally if data exists. If not found, it triggers a real-time fetch to the PokeAPI server, queues an internal asynchronous document replica save, and returns the response mapped to the core domain structure.

### 3. Modify Attributes on Local Database (User Story 04)
*   **HTTP Method & Route:** `PATCH /api/v1/pokemon/{id}`
*   **Headers Required:** `Content-Type: application/json`
*   **JSON Payload Schema:**
    ```json
    {
      "id": "25",
      "name": "pikachu",
      "imageUrl": "https://...",
      "description": "...",
      "evolutionaryLineage": [],
      "localizedName": "Pikachu Special Latam Edition",
      "geographicalMetadata": "Power Grid, Server Rack 4",
      "internalTags": ["EPIC", "REPLICATED_OK"]
    }
    ```
*   **Defensive Logic & Edge Case Handling:**
    *   Throws a **`400 Bad Request`** status if mandatory fields like `localizedName` or `geographicalMetadata` arrive blank, malformed, or missing.
    *   Throws a **`404 Not Found`** status code if the user attempts to run a mutation payload on a Pokémon ID that does not exist inside the local database.
    *   Automatically fires cache purge listeners upon completion to update pagination data instantly on the React client app.
