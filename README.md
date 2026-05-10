# QEats

A Spring Boot REST API backend for a food ordering platform. QEats allows users to discover nearby restaurants and place food orders, with data served from a MongoDB database seeded with geo-localized restaurant records.

> Built as part of the [Crio.Do](https://crio.do) backend engineering curriculum.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 8+ |
| Framework | Spring Boot 2.1.4 |
| Database | MongoDB (`restaurant-database`) |
| Build Tool | Gradle (multi-module, wrapper included) |
| Testing | JUnit 5 + Mockito 2.x |
| Code Quality | Checkstyle 7.8.1, PMD 6.10.0, SpotBugs 4.5.0 |
| Coverage | JaCoCo 0.8.5 |
| Utilities | Lombok 1.18.4, ModelMapper 2.3.2, Swagger 2.9.2 |

---

## Project Structure

```
QEats/
├── qeatsbackend/           # Main Spring Boot application module
│   └── src/
│       ├── main/java/      # Application source code
│       └── test/java/      # Unit & integration tests
├── __CRIO__/               # Crio platform config (checkstyle, PMD rules, git hooks)
├── gradle/wrapper/         # Gradle wrapper binaries
├── build.gradle            # Root multi-module build configuration
├── settings.gradle         # Gradle project settings
├── gradle.properties       # Gradle properties
├── coordinates.txt         # Geo-coordinates for local data seeding
├── setup_mongo.sh          # Seed MongoDB with standard restaurant data (~50 km radius)
├── setup_mongo_debug.sh    # Seed MongoDB with debug dataset (~200 km radius)
├── setup_mongo_performance.sh  # Seed MongoDB with performance/load dataset (~250 km radius)
└── gradlew / gradlew.bat   # Gradle wrapper executables
```

---

## Prerequisites

- Java 8 or higher
- MongoDB running locally (`mongod` service active)
- Python 3 with `pymongo` (installed automatically by setup scripts)
- Gradle (or use the included `./gradlew` wrapper)
- Git (SSH access to Crio's GitLab for shared restaurant data)

---

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/xtmat/QEats.git
cd QEats
```

### 2. Set your location coordinates

Edit `coordinates.txt` to set the geographic center for restaurant data seeding:

```bash
# coordinates.txt
latitude=12.978755
longitude=77.556782
```

Replace the values with your own latitude and longitude. The setup scripts use these to localize the restaurant dataset to your region.

### 3. Seed the MongoDB database

Make sure `mongod` is running, then run the appropriate setup script:

**Standard setup** (50 km radius, ~normal dataset):
```bash
chmod +x setup_mongo.sh
./setup_mongo.sh
```

**Debug setup** (200 km radius, debug-friendly dataset):
```bash
chmod +x setup_mongo_debug.sh
./setup_mongo_debug.sh
```

**Performance/load testing setup** (250 km radius, large dataset):
```bash
chmod +x setup_mongo_performance.sh
./setup_mongo_performance.sh
```

Each script will:
1. Verify MongoDB is running
2. Clone/pull the shared restaurant data resources from Crio's GitLab
3. Drop and recreate the `restaurant-database`
4. Restore the gzipped MongoDB dump
5. Install `pymongo` via pip3
6. Localize restaurant coordinates to your region

### 4. Build and run

```bash
cd qeatsbackend
../gradlew bootRun
```

The server starts on **port 8080** by default.

---

## Running Tests

```bash
./gradlew test
```

Test results are written to `~/.gradle/daemon/<buildId>/test-results/` (JUnit XML) and as HTML reports under `/tmp/external_build`. A summary is printed to the console after each run.

Note: tests have a 15-minute timeout per suite.

---

## API Overview

> Base URL: `http://localhost:8080`

QEats exposes REST endpoints for restaurant discovery and food ordering. Swagger UI is available at:

```
http://localhost:8080/swagger-ui.html
```

Typical endpoints include:

| Method | Endpoint | Description |
|---|---|---|
| GET | `/qeats/v1/restaurants` | Fetch restaurants near given coordinates |
| GET | `/qeats/v1/restaurants/{restaurantId}` | Get details for a specific restaurant |
| GET | `/qeats/v1/restaurants/{restaurantId}/menu` | Get menu for a restaurant |
| POST | `/qeats/v1/cart` | Create or update a user's cart |
| POST | `/qeats/v1/order` | Place an order |

*(Verify against the Swagger UI or source controllers for the full and current API surface.)*

---

## Code Quality

The build is configured with several static analysis tools (all set to `ignoreFailures = true` so they won't break the build):

- **Checkstyle** — style rules defined in `__CRIO__/checkstyle.xml`
- **PMD** — rules defined in `__CRIO__/ruleset.xml`
- **SpotBugs** — HTML reports generated under the build directory
- **JaCoCo** — code coverage reports at `/tmp/external_build/customJacocoReportDir`

---

## Development Notes

- **Lombok** is used extensively — ensure your IDE has the Lombok plugin installed (IntelliJ: Settings → Plugins → Lombok).
- Build output goes to `/tmp/external_build` (configured in `build.gradle`).
- A pre-push git hook is installed automatically on build via the `installGitHooks` task.

---
