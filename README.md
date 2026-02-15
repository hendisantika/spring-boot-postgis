# Spring Boot PostGIS - Indonesia Administrative Boundaries

Interactive map application for exploring Indonesia's administrative boundaries (provinces, cities/regencies, sub-districts, and villages) powered by **Spring Boot**, **PostGIS**, and **Leaflet.js**.

## Features

- Interactive map with boundary visualization using Leaflet.js
- Hierarchical drill-down: Provinsi → Kabupaten/Kota → Kecamatan → Desa/Kelurahan
- Search across all administrative levels
- GeoJSON boundary rendering on map selection
- HTMX-powered dynamic UI with Thymeleaf templates

## Tech Stack

| Layer       | Technology                                    |
|-------------|-----------------------------------------------|
| Framework   | Spring Boot 4.0.2, Java 25                    |
| Database    | PostgreSQL + PostGIS                          |
| ORM         | Hibernate Spatial 7.0                         |
| Migrations  | Flyway                                        |
| Frontend    | Thymeleaf, HTMX 2.0, Leaflet 1.9.4, W3.CSS   |
| Build       | Maven                                         |

## Data Source

Administrative boundary data from [Alf-Anas/batas-administrasi-indonesia](https://github.com/Alf-Anas/batas-administrasi-indonesia) (June 2023):

| Level | Description       | Table                      | Records |
|-------|-------------------|----------------------------|---------|
| 1     | Provinsi          | `wilayah_level_1_2`        | 38      |
| 2     | Kabupaten/Kota    | `wilayah_level_1_2`        | 514     |
| 3     | Kecamatan         | `idn_admbnda_adm3_2023`   | 7,275   |
| 4     | Desa/Kelurahan    | `all_villages_2023`        | 83,518  |

## Prerequisites

- Java 25+
- PostgreSQL with PostGIS extension
- Git LFS (for large migration SQL files)
- Maven 3.9+

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/hendisantika/spring-boot-postgis.git
cd spring-boot-postgis
git lfs pull
```

### 2. Set up the database

```bash
psql -U postgres -p 5433 -c "CREATE DATABASE indonesia_map;"
psql -U postgres -p 5433 -d indonesia_map -c "CREATE EXTENSION IF NOT EXISTS postgis;"
```

### 3. Run the application

```bash
./mvnw spring-boot:run
```

Flyway will automatically run all migrations on startup. Open [http://localhost:8080](http://localhost:8080) in your browser.

## Configuration

Default database settings in `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/indonesia_map
spring.datasource.username=postgres
spring.datasource.password=postgres
```

## Project Structure

```
src/main/java/id/my/hendisantika/postgis/
├── controller/
│   ├── HomeController.java          # Home page and map view
│   └── WilayahController.java       # Wilayah API and fragment endpoints
├── dto/
│   └── BoundaryData.java            # Boundary GeoJSON response DTO
├── entity/
│   ├── SubDistrict.java             # Kecamatan entity (idn_admbnda_adm3_2023)
│   ├── Village.java                 # Desa/Kelurahan entity (all_villages_2023)
│   ├── WilayahLevel12.java          # Provinsi & Kabupaten entity
│   └── WilayahLevel34.java          # Kecamatan & Desa unified view entity
├── repository/
│   ├── SubDistrictRepository.java
│   ├── VillageRepository.java
│   ├── WilayahLevel12Repository.java
│   └── WilayahLevel34Repository.java
└── service/
    └── WilayahService.java          # Business logic for all admin levels

src/main/resources/
├── db/migration/                    # Flyway migrations (V1-V46)
├── templates/                       # Thymeleaf templates
│   ├── layout.html
│   ├── index.html
│   └── fragments/
└── static/js/map.js                 # Leaflet map initialization
```

## API Endpoints

| Method | Path                                  | Description                          |
|--------|---------------------------------------|--------------------------------------|
| GET    | `/`                                   | Home page with interactive map       |
| GET    | `/wilayah/provinsi`                   | List all provinces (HTMX fragment)   |
| GET    | `/wilayah/kabupaten/{provinsiKode}`   | Cities by province (HTMX fragment)   |
| GET    | `/wilayah/kecamatan/{kabupatenKode}`  | Sub-districts by city (HTMX fragment)|
| GET    | `/wilayah/desa/{kecamatanKode}`       | Villages by sub-district (HTMX)      |
| GET    | `/wilayah/search?keyword=...`         | Search across all levels             |
| GET    | `/wilayah/detail/{kode}`              | Detail info for a region             |
| GET    | `/wilayah/api/boundary/{kode}`        | GeoJSON boundary data (JSON)         |
| GET    | `/wilayah/api/all`                    | All provinces with metadata (JSON)   |

## License

This project is open source.
