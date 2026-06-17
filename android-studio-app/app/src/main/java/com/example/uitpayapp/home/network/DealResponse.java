package com.example.uitpayapp.home.network;

import com.example.uitpayapp.recommendeddeal.RecommendedDealModel;
import java.util.List;

public class DealResponse {
    private List<RecommendedDealModel> deals;
    private int totalPages;
    private int currentPage;

    public List<RecommendedDealModel> getDeals() { return deals; }
    public int getTotalPages() { return totalPages; }
    public int getCurrentPage() { return currentPage; }
}
