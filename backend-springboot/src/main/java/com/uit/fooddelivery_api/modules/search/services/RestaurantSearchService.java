package com.uit.fooddelivery_api.modules.search.services;

import com.uit.fooddelivery_api.modules.restaurant.entities.Restaurant;
import com.uit.fooddelivery_api.modules.search.documents.RestaurantDocument;
import com.uit.fooddelivery_api.modules.search.repositories.RestaurantSearchRepository;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantSearchService {

    private final RestaurantSearchRepository searchRepository;

    public void syncRestaurant(Restaurant restaurant) {
        try {
            GeoPoint geoPoint = null;
            if (restaurant.getLatitude() != null && restaurant.getLongitude() != null) {
                geoPoint = new GeoPoint(restaurant.getLatitude().doubleValue(), restaurant.getLongitude().doubleValue());
            }
            RestaurantDocument doc = RestaurantDocument.builder()
                    .id(restaurant.getId().toString())
                    .name(restaurant.getName())
                    .address(restaurant.getAddress())
                    .location(geoPoint)
                    .build();
            searchRepository.save(doc);
            log.info("Successfully synced restaurant {} to Elasticsearch", restaurant.getId());
        } catch (Exception e) {
            log.error("Failed to sync restaurant {} to Elasticsearch: {}", restaurant.getId(), e.getMessage());
        }
    }

    public void deleteRestaurant(Long restaurantId) {
        try {
            searchRepository.deleteById(restaurantId.toString());
            log.info("Successfully deleted restaurant {} from Elasticsearch", restaurantId);
        } catch (Exception e) {
            log.error("Failed to delete restaurant {} from Elasticsearch: {}", restaurantId, e.getMessage());
        }
    }
}
