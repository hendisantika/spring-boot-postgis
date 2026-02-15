# PostGIS Migration Files - Indonesia Administrative Boundaries

## Overview

These migration files have been converted from MySQL to PostgreSQL/PostGIS format. The schema is designed to store Indonesian administrative boundaries at multiple levels with spatial geometry support.

## Converted Files

### Schema Migrations (Completed ✓)
- `V1__create_wilayah_level_1_2_table.sql` - Provinces and regencies
- `V9__create_wilayah_level_3_boundaries_table.sql` - Sub-districts with PostGIS geometry
- `V10__create_wilayah_level_4_boundaries_table.sql` - Villages with PostGIS geometry
- `V11__create_boundary_verification_views.sql` - Helpful views for data verification
- `V12__create_wilayah_level_3_4_table.sql` - Unified table with JSON boundaries
- `V13__normalize_level_3_codes.sql` - Normalize sub-district codes
- `V14__normalize_level_4_codes.sql` - Normalize village codes
- `V15__populate_wilayah_level_3_kecamatan.sql` - Populate kecamatan data
- `V16__populate_wilayah_level_4_desa.sql` - Populate village data

### Data Migrations (Need Conversion ⚠️)

The following large data files from the MySQL project need to be converted:

#### Wilayah Data (V2-V8)
- `V2_22012026_2301__insert_wilayah_sumatera.sql` (5.7 MB)
- `V3_22012026_2302__insert_wilayah_jawa_bali.sql` (2.5 MB)
- `V4_22012026_2303__insert_wilayah_nusa_tenggara.sql` (2.5 MB)
- `V5_22012026_2304__insert_wilayah_kalimantan.sql` (2.9 MB)
- `V6_22012026_2305__insert_wilayah_sulawesi.sql` (5.2 MB)
- `V7_22012026_2306__insert_wilayah_maluku.sql` (1.9 MB)
- `V8_22012026_2307__insert_wilayah_papua.sql` (2.0 MB)

#### Boundary Data (V9.1-V10.7)
- Level 3 (Sub-districts): V9.1 through V9.7 (~250 MB total)
- Level 4 (Villages): V10.1 through V10.7 (~550 MB total)

## Key MySQL to PostGIS Conversions

### Data Type Changes
```sql
-- MySQL → PostgreSQL
AUTO_INCREMENT → SERIAL
tinyint(4) → SMALLINT
double → DOUBLE PRECISION
float → REAL
longtext → TEXT
DECIMAL(x,y) → NUMERIC(x,y)
```

### Geometry Changes
```sql
-- MySQL
GEOMETRY → GEOMETRY(Geometry, 4326)
SPATIAL KEY `idx` (`geom`) → CREATE INDEX idx ON table USING GIST(geom)
```

### Settings
```sql
-- MySQL (remove these)
SET NAMES utf8;
SET time_zone = '+00:00';
SET foreign_key_checks = 0;
SET sql_mode = 'NO_AUTO_VALUE_ON_ZERO';

-- PostgreSQL (already enabled in schema migrations)
CREATE EXTENSION IF NOT EXISTS postgis;
```

### Identifier Syntax
```sql
-- MySQL uses backticks
`table_name`

-- PostgreSQL uses no quotes or double quotes
table_name
"table_name"
```

## Converting Large Data Files

For the large INSERT data files (V2-V8 and V9.1-V10.7), you can use this approach:

### Option 1: Automated Conversion Script

```bash
# Create a conversion script
cat > convert_mysql_to_postgres.sh << 'EOF'
#!/bin/bash
# Convert MySQL INSERT statements to PostgreSQL format

input_file="$1"
output_file="$2"

if [ -z "$input_file" ] || [ -z "$output_file" ]; then
    echo "Usage: $0 input.sql output.sql"
    exit 1
fi

sed -e "s/\`//g" \
    -e "/^SET NAMES/d" \
    -e "/^SET time_zone/d" \
    -e "/^SET foreign_key_checks/d" \
    -e "/^SET sql_mode/d" \
    -e "s/ENGINE=MyISAM//" \
    -e "s/ENGINE=InnoDB//" \
    -e "s/ DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci//" \
    -e "s/ DEFAULT CHARSET=utf8//" \
    -e "s/ CHARACTER SET utf8 COLLATE utf8_general_ci//" \
    "$input_file" > "$output_file"

echo "Converted: $input_file → $output_file"
EOF

chmod +x convert_mysql_to_postgres.sh

# Convert each file
./convert_mysql_to_postgres.sh \
  /path/to/source/V2_22012026_2301__insert_wilayah_sumatera.sql \
  V2__insert_wilayah_sumatera.sql
```

### Option 2: Manual sed Commands

```bash
cd /Users/hendisantika/IdeaProjects/indonesia-map/src/main/resources/db/migration

# For each data file:
sed -e 's/`//g' \
    -e '/^SET NAMES/d' \
    -e '/^SET time_zone/d' \
    -e '/^SET foreign_key_checks/d' \
    -e '/^SET sql_mode/d' \
    V2_22012026_2301__insert_wilayah_sumatera.sql > \
    /Users/hendisantika/IdeaProjects/spring-boot-postgis/src/main/resources/db/migration/V2__insert_wilayah_sumatera.sql
```

### Option 3: Direct Database Import

If you have a running MySQL database with the data:

```bash
# Export from MySQL as CSV
mysql -u user -p database_name -e "SELECT * FROM wilayah_level_1_2" \
  --batch --raw > wilayah_data.csv

# Import to PostgreSQL
psql -U user -d database_name -c "\COPY wilayah_level_1_2 FROM 'wilayah_data.csv' WITH CSV HEADER"
```

### Option 4: Use Geometry Import Tools

For the boundary geometry files, consider using PostGIS native tools:

```bash
# If original shapefiles are available
shp2pgsql -I -s 4326 input.shp idn_admbnda_adm3_bps_20200401 | psql -d database_name

# Or use ogr2ogr
ogr2ogr -f "PostgreSQL" PG:"dbname=database_name user=username" input.shp \
  -nln idn_admbnda_adm3_bps_20200401 -lco GEOMETRY_NAME=geom -lco SPATIAL_INDEX=GIST
```

## Migration Order

1. Run schema migrations first (V1, V9, V10, V11, V12)
2. Import wilayah data (V2-V8) - after converting
3. Import boundary data for Level 3 (V9.1-V9.7) - after converting
4. Run normalization for Level 3 (V13)
5. Import boundary data for Level 4 (V10.1-V10.7) - after converting
6. Run normalization for Level 4 (V14)
7. Populate unified tables (V15, V16)

## Database Setup

```bash
# Create PostgreSQL database
createdb indonesia_map

# Enable PostGIS (this is also done in V1 migration)
psql -d indonesia_map -c "CREATE EXTENSION postgis;"

# Run Flyway migration
./mvnw flyway:migrate
```

## Testing PostGIS Functions

After migration, test with these queries:

```sql
-- Get boundary as GeoJSON
SELECT kode, nama_indonesia,
       ST_AsGeoJSON(geom) as geojson
FROM v_wilayah_level_3_boundaries
WHERE kode = '11.01.01';

-- Find location by point (longitude, latitude)
SELECT kode, nama_indonesia
FROM v_wilayah_level_3_boundaries
WHERE ST_Contains(geom, ST_SetSRID(ST_MakePoint(106.8456, -6.2088), 4326));

-- Calculate area in square kilometers
SELECT kode, adm3_en,
       ST_Area(geom::geography) / 1000000 as area_km2
FROM idn_admbnda_adm3_bps_20200401
LIMIT 10;

-- Get centroid coordinates
SELECT kode, adm3_en,
       ST_Y(ST_Centroid(geom)) as latitude,
       ST_X(ST_Centroid(geom)) as longitude
FROM idn_admbnda_adm3_bps_20200401
LIMIT 10;
```

## Performance Tips

1. **Indexes**: All spatial indexes use GIST (already created in migrations)
2. **Geometry vs Geography**: Use `geography` type for accurate area calculations
3. **SRID 4326**: All geometries use WGS84 coordinate system
4. **Vacuum**: Run `VACUUM ANALYZE` after large data imports

## Notes

- All coordinates are in WGS84 (EPSG:4326) format
- The `path` column in `wilayah_level_3_4` stores JSON coordinate arrays as TEXT
- The `geom` column in boundary tables stores native PostGIS geometry
- Expected total records: ~89,000 administrative regions

## Source Data

- HDX Indonesia Administrative Boundaries (COD-AB)
- BPS (Badan Pusat Statistik) 2020-04-01 data
- Kepmendagri No 300.2.2-2138 Tahun 2025

## License

MIT License - See original source files for details
