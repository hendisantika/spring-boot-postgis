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

    echo -e "${YELLOW}Converting: $basename${NC}"

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

    echo -e "${GREEN}✓ Created: $(basename "$output_file")${NC}"
}

# Convert wilayah data files (V2-V8)
echo -e "\n${GREEN}Converting wilayah data files...${NC}"

# Map old version numbers to new sequential numbers with proper date format
declare -A WILAYAH_FILES=(
    ["V2_22012026_2301__insert_wilayah_sumatera.sql"]="V2_15022026_1020__insert_wilayah_sumatera.sql"
    ["V3_22012026_2302__insert_wilayah_jawa_bali.sql"]="V3_15022026_1030__insert_wilayah_jawa_bali.sql"
    ["V4_22012026_2303__insert_wilayah_nusa_tenggara.sql"]="V4_15022026_1040__insert_wilayah_nusa_tenggara.sql"
    ["V5_22012026_2304__insert_wilayah_kalimantan.sql"]="V5_15022026_1050__insert_wilayah_kalimantan.sql"
    ["V6_22012026_2305__insert_wilayah_sulawesi.sql"]="V6_15022026_1100__insert_wilayah_sulawesi.sql"
    ["V7_22012026_2306__insert_wilayah_maluku.sql"]="V7_15022026_1110__insert_wilayah_maluku.sql"
    ["V8_22012026_2307__insert_wilayah_papua.sql"]="V8_15022026_1120__insert_wilayah_papua.sql"
)

for source_name in "${!WILAYAH_FILES[@]}"; do
    target_name="${WILAYAH_FILES[$source_name]}"
    if [ -f "$SOURCE_DIR/$source_name" ]; then
        convert_file "$SOURCE_DIR/$source_name" "$TARGET_DIR/$target_name"
    else
        echo -e "${YELLOW}⚠ Warning: File not found: $source_name${NC}"
    fi
done

# Convert Level 3 boundary data files (V9.1-V9.7)
echo -e "\n${GREEN}Converting Level 3 boundary data files...${NC}"

declare -A LEVEL3_FILES=(
    ["V9.1_23012026_1010__insert_wilayah_level_3_sumatera.sql"]="V17_15022026_1130__insert_level_3_sumatera.sql"
    ["V9.2_23012026_1020__insert_wilayah_level_3_jawa_bali.sql"]="V18_15022026_1140__insert_level_3_jawa_bali.sql"
    ["V9.3_23012026_1030__insert_wilayah_level_3_nusa_tenggara.sql"]="V19_15022026_1150__insert_level_3_nusa_tenggara.sql"
    ["V9.4_23012026_1040__insert_wilayah_level_3_kalimantan.sql"]="V20_15022026_1200__insert_level_3_kalimantan.sql"
    ["V9.5_23012026_1050__insert_wilayah_level_3_sulawesi.sql"]="V21_15022026_1210__insert_level_3_sulawesi.sql"
    ["V9.6_23012026_1060__insert_wilayah_level_3_maluku.sql"]="V22_15022026_1220__insert_level_3_maluku.sql"
    ["V9.7_23012026_1070__insert_wilayah_level_3_papua.sql"]="V23_15022026_1230__insert_level_3_papua.sql"
)

for source_name in "${!LEVEL3_FILES[@]}"; do
    target_name="${LEVEL3_FILES[$source_name]}"
    if [ -f "$SOURCE_DIR/$source_name" ]; then
        convert_file "$SOURCE_DIR/$source_name" "$TARGET_DIR/$target_name"
    else
        echo -e "${YELLOW}⚠ Warning: File not found: $source_name${NC}"
    fi
done

# Convert Level 4 boundary data files (V10.1-V10.7)
echo -e "\n${GREEN}Converting Level 4 boundary data files...${NC}"

declare -A LEVEL4_FILES=(
    ["V10.1_23012026_1110__insert_wilayah_level_4_sumatera.sql"]="V24_15022026_1240__insert_level_4_sumatera.sql"
    ["V10.2_23012026_1120__insert_wilayah_level_4_jawa_bali.sql"]="V25_15022026_1250__insert_level_4_jawa_bali.sql"
    ["V10.3_23012026_1130__insert_wilayah_level_4_nusa_tenggara.sql"]="V26_15022026_1300__insert_level_4_nusa_tenggara.sql"
    ["V10.4_23012026_1140__insert_wilayah_level_4_kalimantan.sql"]="V27_15022026_1310__insert_level_4_kalimantan.sql"
    ["V10.5_23012026_1150__insert_wilayah_level_4_sulawesi.sql"]="V28_15022026_1320__insert_level_4_sulawesi.sql"
    ["V10.6_23012026_1160__insert_wilayah_level_4_maluku.sql"]="V29_15022026_1330__insert_level_4_maluku.sql"
    ["V10.7_23012026_1170__insert_wilayah_level_4_papua.sql"]="V30_15022026_1340__insert_level_4_papua.sql"
)

for source_name in "${!LEVEL4_FILES[@]}"; do
    target_name="${LEVEL4_FILES[$source_name]}"
    if [ -f "$SOURCE_DIR/$source_name" ]; then
        convert_file "$SOURCE_DIR/$source_name" "$TARGET_DIR/$target_name"
    else
        echo -e "${YELLOW}⚠ Warning: File not found: $source_name${NC}"
    fi
done

echo -e "\n${GREEN}==================================================${NC}"
echo -e "${GREEN}Conversion Complete!${NC}"
echo -e "${GREEN}==================================================${NC}"
echo -e "\nConverted files are in: ${YELLOW}$TARGET_DIR${NC}"
echo -e "\nNext steps:"
echo -e "1. Review the converted files"
echo -e "2. Set up PostgreSQL database with PostGIS"
echo -e "3. Run: ${YELLOW}./mvnw flyway:migrate${NC}"
echo -e "4. Verify data with sample queries (see README.md)"
echo ""
echo -e "${YELLOW}Note: Large files may take time to convert and import!${NC}"
echo ""
