#!/bin/bash
#
# MySQL to PostgreSQL/PostGIS Migration Converter
# Converts MySQL Flyway migration files to PostgreSQL format
#

set -e

SOURCE_DIR="/Users/hendisantika/IdeaProjects/indonesia-map/src/main/resources/db/migration"
TARGET_DIR="/Users/hendisantika/IdeaProjects/spring-boot-postgis/src/main/resources/db/migration"

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}==================================================${NC}"
echo -e "${GREEN}MySQL to PostgreSQL Migration Converter${NC}"
echo -e "${GREEN}==================================================${NC}"
echo ""

# Check if source directory exists
if [ ! -d "$SOURCE_DIR" ]; then
    echo -e "${RED}Error: Source directory not found: $SOURCE_DIR${NC}"
    exit 1
fi

# Create target directory if it doesn't exist
mkdir -p "$TARGET_DIR"

# Function to convert a single file
convert_file() {
    local input_file="$1"
    local output_file="$2"
    local basename=$(basename "$input_file")

    if [ ! -f "$input_file" ]; then
        echo -e "${YELLOW}⚠ Warning: File not found: $basename${NC}"
        return 1
    fi

    echo -e "${YELLOW}Converting: $basename → $(basename "$output_file")${NC}"

    # Apply conversions
    sed -e 's/`//g' \
        -e '/^SET NAMES utf8;$/d' \
        -e '/^SET time_zone = /d' \
        -e '/^SET foreign_key_checks = /d' \
        -e '/^SET sql_mode = /d' \
        -e 's/ ENGINE=MyISAM//g' \
        -e 's/ ENGINE=InnoDB//g' \
        -e 's/ DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci//g' \
        -e 's/ DEFAULT CHARSET=utf8//g' \
        -e 's/ CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci//g' \
        -e 's/ CHARACTER SET utf8 COLLATE utf8_general_ci//g' \
        -e 's/ AUTO_INCREMENT/ /g' \
        -e 's/INT AUTO_INCREMENT/SERIAL/g' \
        -e 's/tinyint(4)/SMALLINT/g' \
        -e 's/double/DOUBLE PRECISION/g' \
        -e 's/float/REAL/g' \
        -e 's/longtext/TEXT/g' \
        -e 's/COMMENT=/-- /g' \
        "$input_file" > "$output_file"

    echo -e "${GREEN}✓ Created: $(basename "$output_file") ($(du -h "$output_file" | cut -f1))${NC}"
}

# Convert wilayah data files (V2-V8)
echo -e "\n${GREEN}Converting wilayah data files...${NC}"
convert_file "$SOURCE_DIR/V2_22012026_2301__insert_wilayah_sumatera.sql" "$TARGET_DIR/V2_15022026_1020__insert_wilayah_sumatera.sql"
convert_file "$SOURCE_DIR/V3_22012026_2302__insert_wilayah_jawa_bali.sql" "$TARGET_DIR/V3_15022026_1030__insert_wilayah_jawa_bali.sql"
convert_file "$SOURCE_DIR/V4_22012026_2303__insert_wilayah_nusa_tenggara.sql" "$TARGET_DIR/V4_15022026_1040__insert_wilayah_nusa_tenggara.sql"
convert_file "$SOURCE_DIR/V5_22012026_2304__insert_wilayah_kalimantan.sql" "$TARGET_DIR/V5_15022026_1050__insert_wilayah_kalimantan.sql"
convert_file "$SOURCE_DIR/V6_22012026_2305__insert_wilayah_sulawesi.sql" "$TARGET_DIR/V6_15022026_1100__insert_wilayah_sulawesi.sql"
convert_file "$SOURCE_DIR/V7_22012026_2306__insert_wilayah_maluku.sql" "$TARGET_DIR/V7_15022026_1110__insert_wilayah_maluku.sql"
convert_file "$SOURCE_DIR/V8_22012026_2307__insert_wilayah_papua.sql" "$TARGET_DIR/V8_15022026_1120__insert_wilayah_papua.sql"

# Convert Level 3 boundary data files (V9.1-V9.7)
echo -e "\n${GREEN}Converting Level 3 boundary data files...${NC}"
convert_file "$SOURCE_DIR/V9.1_23012026_1010__insert_wilayah_level_3_sumatera.sql" "$TARGET_DIR/V17_15022026_1130__insert_level_3_sumatera.sql"
convert_file "$SOURCE_DIR/V9.2_23012026_1020__insert_wilayah_level_3_jawa_bali.sql" "$TARGET_DIR/V18_15022026_1140__insert_level_3_jawa_bali.sql"
convert_file "$SOURCE_DIR/V9.3_23012026_1030__insert_wilayah_level_3_nusa_tenggara.sql" "$TARGET_DIR/V19_15022026_1150__insert_level_3_nusa_tenggara.sql"
convert_file "$SOURCE_DIR/V9.4_23012026_1040__insert_wilayah_level_3_kalimantan.sql" "$TARGET_DIR/V20_15022026_1200__insert_level_3_kalimantan.sql"
convert_file "$SOURCE_DIR/V9.5_23012026_1050__insert_wilayah_level_3_sulawesi.sql" "$TARGET_DIR/V21_15022026_1210__insert_level_3_sulawesi.sql"
convert_file "$SOURCE_DIR/V9.6_23012026_1060__insert_wilayah_level_3_maluku.sql" "$TARGET_DIR/V22_15022026_1220__insert_level_3_maluku.sql"
convert_file "$SOURCE_DIR/V9.7_23012026_1070__insert_wilayah_level_3_papua.sql" "$TARGET_DIR/V23_15022026_1230__insert_level_3_papua.sql"

# Convert Level 4 boundary data files (V10.1-V10.7)
echo -e "\n${GREEN}Converting Level 4 boundary data files...${NC}"
convert_file "$SOURCE_DIR/V10.1_23012026_1110__insert_wilayah_level_4_sumatera.sql" "$TARGET_DIR/V24_15022026_1240__insert_level_4_sumatera.sql"
convert_file "$SOURCE_DIR/V10.2_23012026_1120__insert_wilayah_level_4_jawa_bali.sql" "$TARGET_DIR/V25_15022026_1250__insert_level_4_jawa_bali.sql"
convert_file "$SOURCE_DIR/V10.3_23012026_1130__insert_wilayah_level_4_nusa_tenggara.sql" "$TARGET_DIR/V26_15022026_1300__insert_level_4_nusa_tenggara.sql"
convert_file "$SOURCE_DIR/V10.4_23012026_1140__insert_wilayah_level_4_kalimantan.sql" "$TARGET_DIR/V27_15022026_1310__insert_level_4_kalimantan.sql"
convert_file "$SOURCE_DIR/V10.5_23012026_1150__insert_wilayah_level_4_sulawesi.sql" "$TARGET_DIR/V28_15022026_1320__insert_level_4_sulawesi.sql"
convert_file "$SOURCE_DIR/V10.6_23012026_1160__insert_wilayah_level_4_maluku.sql" "$TARGET_DIR/V29_15022026_1330__insert_level_4_maluku.sql"
convert_file "$SOURCE_DIR/V10.7_23012026_1170__insert_wilayah_level_4_papua.sql" "$TARGET_DIR/V30_15022026_1340__insert_level_4_papua.sql"

echo -e "\n${GREEN}==================================================${NC}"
echo -e "${GREEN}Conversion Complete!${NC}"
echo -e "${GREEN}==================================================${NC}"
echo -e "\nConverted files are in: ${YELLOW}$TARGET_DIR${NC}"
echo -e "\n${GREEN}Summary:${NC}"
echo -e "  - Wilayah data: 7 files (V2-V8)"
echo -e "  - Level 3 boundaries: 7 files (V17-V23)"
echo -e "  - Level 4 boundaries: 7 files (V24-V30)"
echo -e "  - Total: 21 data files converted"
echo -e "\nNext steps:"
echo -e "1. Review the converted files"
echo -e "2. Set up PostgreSQL database: ${YELLOW}docker compose up -d${NC}"
echo -e "3. Run migrations: ${YELLOW}./mvnw flyway:migrate${NC}"
echo -e "4. Verify data with sample queries (see MIGRATION_GUIDE.md)"
echo ""
echo -e "${YELLOW}Note: Large files may take 10-30 minutes to import!${NC}"
echo ""
