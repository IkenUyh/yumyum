package com.uit.fooddelivery_api.modules.search;

import com.uit.fooddelivery_api.modules.restaurant.entities.Restaurant;
import com.uit.fooddelivery_api.modules.restaurant.repositories.RestaurantRepository;
import com.uit.fooddelivery_api.modules.search.services.RestaurantSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ElasticsearchIndexInitializer implements CommandLineRunner {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantSearchService restaurantSearchService;

    @Override
    public void run(String... args) {
        log.info("Checking Elasticsearch index sync state for restaurants...");
        try {
            List<Restaurant> restaurants = restaurantRepository.findAll();
            log.info("Found {} restaurants in MySQL. Syncing to Elasticsearch...", restaurants.size());
            for (Restaurant restaurant : restaurants) {
                restaurantSearchService.syncRestaurant(restaurant);
            }
            log.info("Elasticsearch restaurants sync completed successfully.");
        } catch (Exception e) {
            log.error("Failed to run initial sync to Elasticsearch (is ES down?): {}", e.getMessage());
        }
    }
}
