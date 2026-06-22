-- Add random review count and rating average to restaurants
UPDATE restaurants 
SET review_count = CASE WHEN review_count IS NULL OR review_count = 0 THEN FLOOR(50 + RAND() * 500) ELSE review_count + FLOOR(50 + RAND() * 500) END,
    rating_average = CASE WHEN rating_average IS NULL OR rating_average = 0 THEN ROUND(3.5 + RAND() * 1.5, 1) ELSE rating_average END
WHERE 1=1;
