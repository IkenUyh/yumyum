-- V45: Fix null and zero coordinates in user_addresses table to prevent shipping distance calculation errors.
UPDATE user_addresses
SET latitude = 10.87044000, longitude = 106.80217000
WHERE (latitude = 0 OR latitude IS NULL) OR (longitude = 0 OR longitude IS NULL);
