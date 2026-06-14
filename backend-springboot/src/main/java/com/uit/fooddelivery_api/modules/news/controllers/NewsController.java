package com.uit.fooddelivery_api.modules.news.controllers;

import com.uit.fooddelivery_api.common.responses.ApiResponse;
import com.uit.fooddelivery_api.modules.news.entities.News;
import com.uit.fooddelivery_api.modules.news.repositories.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsRepository newsRepository;

    // Lấy bảng tin khuyến mãi (Công khai không cần Token)
    @GetMapping
    public ApiResponse<List<News>> getActiveNews() {
        return ApiResponse.success(newsRepository.findByIsActiveTrueOrderByCreatedAtDesc());
    }
}