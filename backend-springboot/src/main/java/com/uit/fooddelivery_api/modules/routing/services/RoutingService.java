package com.uit.fooddelivery_api.modules.routing.services;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RoutingService {

    private static final String OSRM_API_URL = "http://router.project-osrm.org/route/v1/driving/{lon1},{lat1};{lon2},{lat2}?overview=full&geometries=polyline";

    public RoutingResult getRouteAndETA(double lat1, double lon1, double lat2, double lon2) {
        RestTemplate restTemplate = new RestTemplate();
        String url = OSRM_API_URL.replace("{lon1}", String.valueOf(lon1))
                .replace("{lat1}", String.valueOf(lat1))
                .replace("{lon2}", String.valueOf(lon2))
                .replace("{lat2}", String.valueOf(lat2));

        try {
            JsonNode response = restTemplate.getForObject(url, JsonNode.class);
            if (response != null && "Ok".equals(response.get("code").asText())) {
                JsonNode route = response.get("routes").get(0);
                double durationSeconds = route.get("duration").asDouble();
                String polyline = route.get("geometry").asText();
                return new RoutingResult((int) (durationSeconds / 60), polyline);
            }
        } catch (Exception e) {
            System.err.println("Lỗi gọi OSRM: " + e.getMessage());
        }
        return new RoutingResult(15, ""); // Mặc định 15 phút nếu rớt mạng
    }

    public record RoutingResult(int etaMinutes, String polyline) {}
}