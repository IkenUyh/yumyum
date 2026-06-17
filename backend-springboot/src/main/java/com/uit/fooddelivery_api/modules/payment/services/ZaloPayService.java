package com.uit.fooddelivery_api.modules.payment.services;

import com.uit.fooddelivery_api.common.utils.HMACUtil;
import com.uit.fooddelivery_api.modules.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@Service
@RequiredArgsConstructor
public class ZaloPayService {

    @Value("${zalopay.app-id}")
    private String appId;

    @Value("${zalopay.key1}")
    private String key1;

    @Value("${zalopay.endpoint-create}")
    private String endpointCreate;

    @Value("${zalopay.endpoint-query}")
    private String endpointQuery;

    @Value("${zalopay.endpoint-refund}")
    private String endpointRefund;

    @Value("${zalopay.endpoint-query-refund}")
    private String endpointQueryRefund;

    // Hàm tạo yêu cầu nạp tiền
    public Map<String, Object> createTopUpOrder(User user, Long amount) {
        String appTransId = getCurrentTimeString("yyMMdd") + "_" + System.currentTimeMillis();
        String appTime = String.valueOf(System.currentTimeMillis());
        String item = "[]"; // Mảng rỗng vì đây là nạp tiền
        String embedData = "{\"user_id\": " + user.getId() + "}"; // Nhúng ID của user vào để lúc Webhook gọi về còn biết cộng tiền cho ai
        String description = "Nạp tiền vào ví FoodDelivery #" + appTransId;

        // Công thức tạo mã MAC bắt buộc của ZaloPay: app_id|app_trans_id|app_user|amount|app_time|embed_data|item
        String dataToMac = appId + "|" + appTransId + "|" + user.getPhoneNumber() + "|" + amount + "|" + appTime + "|" + embedData + "|" + item;
        String mac = HMACUtil.HMacHexString(dataToMac, key1);

        // Đóng gói request body (ZaloPay yêu cầu form-data)
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("app_id", appId);
        requestBody.add("app_trans_id", appTransId);
        requestBody.add("app_user", user.getPhoneNumber());
        requestBody.add("app_time", appTime);
        requestBody.add("item", item);
        requestBody.add("embed_data", embedData);
        requestBody.add("amount", String.valueOf(amount));
        requestBody.add("description", description);
        requestBody.add("bank_code", ""); // Để trống thì ZaloPay sẽ hiện ra list ngân hàng để chọn
        requestBody.add("mac", mac);

        // Bắn API sang máy chủ ZaloPay
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        // Nhận JSON phản hồi từ ZaloPay
        Map<String, Object> response = restTemplate.postForObject(endpointCreate, request, Map.class);

        // Trả về app_trans_id để client có thể query trạng thái sau này
        if (response != null) {
            response.put("app_trans_id", appTransId);
        }

        return response; // Trong này sẽ chứa 'order_url' để Frontend mở lên
    }

    // Hàm truy vấn trạng thái đơn hàng
    public Map<String, Object> queryOrder(String appTransId) {
        String dataToMac = appId + "|" + appTransId + "|" + key1;
        String mac = HMACUtil.HMacHexString(dataToMac, key1);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("app_id", appId);
        requestBody.add("app_trans_id", appTransId);
        requestBody.add("mac", mac);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(endpointQuery, request, Map.class);
    }

    // Hàm yêu cầu hoàn tiền
    public Map<String, Object> refund(String zpTransId, Long amount, String description) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uid = timestamp + "" + (111 + (int)(Math.random() * 888)); // random uid
        String mRefundId = getCurrentTimeString("yyMMdd") + "_" + appId + "_" + uid;

        String dataToMac = appId + "|" + zpTransId + "|" + amount + "|" + description + "|" + timestamp;
        String mac = HMACUtil.HMacHexString(dataToMac, key1);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("app_id", appId);
        requestBody.add("m_refund_id", mRefundId);
        requestBody.add("zp_trans_id", zpTransId);
        requestBody.add("amount", String.valueOf(amount));
        requestBody.add("timestamp", timestamp);
        requestBody.add("description", description);
        requestBody.add("mac", mac);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(endpointRefund, request, Map.class);
    }

    // Hàm truy vấn trạng thái hoàn tiền
    public Map<String, Object> queryRefundStatus(String mRefundId) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String dataToMac = appId + "|" + mRefundId + "|" + timestamp; // app_id|m_refund_id|timestamp
        String mac = HMACUtil.HMacHexString(dataToMac, key1);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("app_id", appId);
        requestBody.add("m_refund_id", mRefundId);
        requestBody.add("timestamp", timestamp);
        requestBody.add("mac", mac);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(endpointQueryRefund, request, Map.class);
    }

    private String getCurrentTimeString(String format) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(cal.getTimeZone());
        return sdf.format(cal.getTime());
    }
}