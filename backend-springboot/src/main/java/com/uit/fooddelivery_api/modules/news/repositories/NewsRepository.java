package com.uit.fooddelivery_api.modules.news.repositories;

import com.uit.fooddelivery_api.modules.news.entities.News;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {
    List<News> findByIsActiveTrueOrderByCreatedAtDesc();
}