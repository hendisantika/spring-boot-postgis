# MySQL to PostGIS Migration Guide
## Indonesia Administrative Boundaries Project

This guide explains how to migrate your Indonesia administrative boundaries data from MySQL to PostgreSQL/PostGIS.

---

## 📋 What Has Been Done

### ✅ Completed Migrations

The following schema and utility migrations have been converted and are ready to use:

1. **V1__create_wilayah_level_1_2_table.sql** - Base table for provinces and regencies
2. **V9__create_wilayah_level_3_boundaries_table.sql** - Sub-districts with PostGIS geometry
3. **V10__create_wilayah_level_4_boundaries_table.sql** - Villages with PostGIS geometry
4. **V11__create_boundary_verification_views.sql** - Verification and helper views
5. **V12__create_wilayah_level_3_4_table.sql** - Unified table with JSON boundaries
6. **V13__normalize_level_3_codes.sql** - Code normalization for sub-districts
7. **V14__normalize_level_4_codes.sql** - Code normalization for villages
8. **V15__populate_wilayah_level_3_kecamatan.sql** - Populate kecamatan data
9. **V16__populate_wilayah_level_4_desa.sql** - Populate village data

### 🔧 Project Configuration Updates

- **pom.xml**: Added PostgreSQL, PostGIS (Hibernate Spatial), and Flyway dependencies
- **application.properties**: Configured database connection and Flyway settings
- **compose.yaml**: Docker Compose setup with PostGIS and pgAdmin
- **convert_mysql_migrations.sh**: Automated conversion script for data files

---

## 🚀 Getting Started

### Step 1: Start PostgreSQL with PostGIS

```bash
# Start the database
docker compose up -d postgres

# Verify PostGIS is installed
docker compose exec postgres psql -U postgres -d indonesia_map -c "SELECT PostGIS_version();"

# (Optional) Start pgAdmin for GUI management
docker compose up -d pgadmin
# Access at: http://localhost:8080
# Login: admin@admin.com / admin
```

### Step 2: Convert Data Migration Files

The schema migrations are ready, but you need to convert the large data files:

```bash
# Run the conversion script
./convert_mysql_migrations.sh
```

This will convert:
- **Wilayah data** (V2-V8): ~22 MB total
- **Level 3 boundaries** (V17-V23): ~244 MB total
- **Level 4 boundaries** (V24-V30): ~549 MB total

### Step 3: Run Flyway Migrations

```bash
# Option 1: Using Maven
./mvnw flyway:migrate

# Option 2: Using Flyway CLI
flyway -configFiles=application.properties migrate

# Option 3: Run Spring Boot (auto-migration on startup)
./mvnw spring-boot:run
```

### Step 4: Verify the Migration

```bash
# Connect to database
docker compose exec postgres psql -U postgres -d indonesia_map

# Check tables
\dt

# Check PostGIS functions
SELECT tablename, indexname FROM pg_indexes WHERE indexname LIKE '%geom%';

# Sample query - Get province count
SELECT COUNT(*) FROM wilayah_level_1_2 WHERE LENGTH(kode) = 2;

# Sample spatial query - Get boundary as GeoJSON
SELECT kode, adm3_en, ST_AsGeoJSON(geom)::json
FROM idn_admbnda_adm3_bps_20200401
LIMIT 1;
```

---

## 📊 Data Overview

### Expected Record Counts

| Table | Records | Description |
|-------|---------|-------------|
| wilayah_level_1_2 | ~550 | Provinces and regencies |
| idn_admbnda_adm3_bps_20200401 | 7,069 | Sub-districts with boundaries |
| all_villages | 81,911 | Villages with boundaries |
| wilayah_level_3_4 | ~89,000 | Unified table (after populate) |

### File Sizes

| Migration Type | Files | Total Size | Status |
|---------------|-------|------------|--------|
| Schema | V1, V9-V16 | < 1 MB | ✅ Ready |
| Wilayah Data | V2-V8 | ~22 MB | ⚠️ Need conversion |
| Level 3 Boundaries | V17-V23 | ~244 MB | ⚠️ Need conversion |
| Level 4 Boundaries | V24-V30 | ~549 MB | ⚠️ Need conversion |

---

## 🔑 Key MySQL → PostGIS Changes

### Data Types

```sql
-- MySQL → PostgreSQL
AUTO_INCREMENT          → SERIAL
tinyint(4)             → SMALLINT
double                 → DOUBLE PRECISION
float                  → REAL
longtext               → TEXT
DECIMAL(18,11)         → NUMERIC(18,11)
```

### Geometry

```sql
-- MySQL Spatial
GEOMETRY                → GEOMETRY(Geometry, 4326)
SPATIAL KEY            → CREATE INDEX ... USING GIST

-- Example:
-- MySQL:
CREATE TABLE t (geom GEOMETRY NOT NULL, SPATIAL KEY idx_geom (geom));

-- PostgreSQL:
CREATE TABLE t (geom GEOMETRY(Geometry, 4326) NOT NULL);
CREATE INDEX idx_geom ON t USING GIST(geom);
```

### Settings Removed

```sql
-- These MySQL-specific settings are removed:
SET NAMES utf8;
SET time_zone = '+00:00';
SET foreign_key_checks = 0;
SET sql_mode = 'NO_AUTO_VALUE_ON_ZERO';
```

---

## 🎯 Common PostGIS Queries

### Spatial Queries

```sql
-- 1. Get boundary as GeoJSON
SELECT kode, nama_indonesia,
       ST_AsGeoJSON(geom)::json as geojson
FROM v_wilayah_level_3_boundaries
WHERE kode = '11.01.01';

-- 2. Find location by coordinates (longitude, latitude)
SELECT kode, nama_indonesia
FROM v_wilayah_level_3_boundaries
WHERE ST_Contains(
    geom,
    ST_SetSRID(ST_MakePoint(106.8456, -6.2088), 4326)
);

-- 3. Calculate area in square kilometers
SELECT kode, adm3_en,
       ST_Area(geom::geography) / 1000000 as area_km2
FROM idn_admbnda_adm3_bps_20200401
LIMIT 10;

-- 4. Get centroid (center point)
SELECT kode, adm3_en,
       ST_Y(ST_Centroid(geom)) as latitude,
       ST_X(ST_Centroid(geom)) as longitude
FROM idn_admbnda_adm3_bps_20200401
LIMIT 10;

-- 5. Distance between two points (in meters)
SELECT ST_Distance(
    ST_SetSRID(ST_MakePoint(106.8456, -6.2088), 4326)::geography,
    ST_SetSRID(ST_MakePoint(107.6191, -6.9175), 4326)::geography
) as distance_meters;

-- 6. Find nearest boundaries to a point
SELECT kode, adm3_en,
       ST_Distance(
           geom::geography,
           ST_SetSRID(ST_MakePoint(106.8456, -6.2088), 4326)::geography
       ) as distance_meters
FROM idn_admbnda_adm3_bps_20200401
ORDER BY distance_meters
LIMIT 5;
```

### Statistics Queries

```sql
-- 1. View migration statistics
SELECT * FROM v_boundary_statistics;

-- 2. Count by province
SELECT
    SUBSTRING(kode, 1, 2) as province_code,
    provinsi,
    COUNT(*) as village_count
FROM v_wilayah_level_4_boundaries
GROUP BY SUBSTRING(kode, 1, 2), provinsi
ORDER BY village_count DESC;

-- 3. Verify data completeness
SELECT
    'Provinces' as level,
    COUNT(*) as count
FROM wilayah_level_1_2
WHERE LENGTH(kode) = 2

UNION ALL

SELECT
    'Regencies' as level,
    COUNT(*) as count
FROM wilayah_level_1_2
WHERE LENGTH(kode) = 5;
```

---

## 🛠️ Manual Data Conversion (Alternative)

If the automated script doesn't work for some files, you can convert manually:

### Using sed

```bash
# Convert a single file
sed -e 's/`//g' \
    -e '/^SET NAMES/d' \
    -e '/^SET time_zone/d' \
    -e '/^SET foreign_key_checks/d' \
    -e '/^SET sql_mode/d' \
    source.sql > target.sql
```

### Using MySQL Export

```bash
# Export from MySQL as INSERT statements
mysqldump -u user -p database_name table_name \
  --no-create-info --skip-triggers \
  --complete-insert > output.sql

# Then apply sed conversions above
```

### Direct Database Migration

```bash
# 1. Export from MySQL
mysqldump -u user -p indonesia_map > mysql_dump.sql

# 2. Convert SQL file
sed [conversions] mysql_dump.sql > postgres_ready.sql

# 3. Import to PostgreSQL
psql -U postgres -d indonesia_map < postgres_ready.sql
```

---

## 📁 Project Structure

```
spring-boot-postgis/
├── src/main/
│   ├── java/
│   └── resources/
│       ├── db/migration/
│       │   ├── V1__create_wilayah_level_1_2_table.sql
│       │   ├── V2__insert_wilayah_sumatera.sql (after conversion)
│       │   ├── V9__create_wilayah_level_3_boundaries_table.sql
│       │   └── ... (other migrations)
│       └── application.properties
├── init-scripts/
│   └── 01-init.sql
├── compose.yaml
├── convert_mysql_migrations.sh
├── MIGRATION_GUIDE.md (this file)
├── pom.xml
└── README.md (in db/migration/)
```

---

## ⚠️ Important Notes

### Migration Order

**Critical**: Migrations must run in this order:

1. Schema creation (V1, V9, V10, V11, V12)
2. Data import (V2-V8 for wilayah)
3. Boundary data Level 3 (V17-V23)
4. Normalize Level 3 codes (V13)
5. Boundary data Level 4 (V24-V30)
6. Normalize Level 4 codes (V14)
7. Populate unified tables (V15, V16)

### Performance

- Large data imports may take 10-30 minutes
- Creating GIST spatial indexes is intensive
- Consider running VACUUM ANALYZE after import
- Increase `work_mem` for better performance during import

### SRID 4326

All geometries use SRID 4326 (WGS84):
- Latitude: -90 to +90
- Longitude: -180 to +180
- Standard for GPS and web mapping

---

## 🐛 Troubleshooting

### Flyway Checksum Errors

```bash
# Reset Flyway history (development only!)
docker compose exec postgres psql -U postgres -d indonesia_map \
  -c "DROP SCHEMA IF EXISTS flyway_schema_history CASCADE;"

# Re-run migrations
./mvnw flyway:migrate
```

### Out of Memory During Import

```properties
# Add to application.properties
spring.jpa.properties.hibernate.jdbc.batch_size=50
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
```

### Slow Spatial Queries

```sql
-- Verify GIST indexes exist
SELECT tablename, indexname
FROM pg_indexes
WHERE indexdef LIKE '%USING gist%';

-- Run ANALYZE to update statistics
ANALYZE idn_admbnda_adm3_bps_20200401;
ANALYZE all_villages;
```

---

## 📚 Resources

- [PostGIS Documentation](https://postgis.net/documentation/)
- [Flyway Documentation](https://flywaydb.org/documentation/)
- [Hibernate Spatial](https://docs.jboss.org/hibernate/orm/current/userguide/html_single/Hibernate_User_Guide.html#spatial)
- [HDX Indonesia Data](https://data.humdata.org/dataset/cod-ab-idn)

---

## ✅ Checklist

- [ ] PostgreSQL with PostGIS running
- [ ] Dependencies added to pom.xml
- [ ] Database configuration in application.properties
- [ ] Schema migrations converted (V1, V9-V16)
- [ ] Data migrations converted (V2-V8, V17-V30)
- [ ] Flyway migrations successful
- [ ] Data verified with sample queries
- [ ] Spatial indexes created and working
- [ ] Application connects successfully

---

## 🎉 Next Steps

1. **Run the conversion script**: `./convert_mysql_migrations.sh`
2. **Start the database**: `docker compose up -d`
3. **Run migrations**: `./mvnw flyway:migrate`
4. **Verify data**: Use sample queries above
5. **Build your Spring Boot entities** to work with the spatial data
6. **Create REST APIs** to expose the boundary data

Good luck with your migration! 🚀
