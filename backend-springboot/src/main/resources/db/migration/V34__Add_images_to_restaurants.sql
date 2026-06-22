-- V34__Add_images_to_restaurants.sql

-- Add placeholder images for restaurants that don't have one
-- Using picsum.photos with the restaurant ID as the seed so the image is consistent for each restaurant
UPDATE restaurants 
SET image_url = CONCAT('https://picsum.photos/seed/', id, '/400/300') 
WHERE image_url IS NULL;
