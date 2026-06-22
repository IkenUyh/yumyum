package com.uit.fooddelivery_api.modules.search.repositories;

import com.uit.fooddelivery_api.modules.search.documents.RestaurantDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantSearchRepository extends ElasticsearchRepository<RestaurantDocument, String> {
    @org.springframework.data.elasticsearch.annotations.Query("{\"match\": {\"name\": {\"query\": \"?0\"}}}")
    List<RestaurantDocument> searchFuzzyByName(String keyword);
}