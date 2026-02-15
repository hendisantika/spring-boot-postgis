-- Initialize PostGIS extension
-- This script runs automatically when the container starts for the first time

CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS postgis_topology;

-- Verify PostGIS installation
SELECT PostGIS_version();

-- Create a test query to verify spatial capabilities
-- This can be commented out in production
DO $$
BEGIN
    RAISE NOTICE 'PostGIS % installed successfully', (SELECT PostGIS_version());
END $$;
