package com.example.uitpayapp.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.nio.charset.StandardCharsets;

public class SessionManager {
    // Tên của file SharedPreferences lưu trên thiết bị
    private static final String PREF_NAME = "UitPaySessionPrefs";

    // Các Key định danh dữ liệu
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_NAME = "user_full_name";
    private static final String KEY_USER_PHONE = "user_phone_number";
    private static final String KEY_USER_AVATAR = "user_avatar_url";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_ROLE = "user_role";
    
    // Address storage
    private static final String KEY_DELIVERY_ADDRESS_ID = "delivery_address_id";
    private static final String KEY_DELIVERY_ADDRESS_TEXT = "delivery_address_text";
    private static final String KEY_DELIVERY_LAT = "delivery_lat";
    private static final String KEY_DELIVERY_LNG = "delivery_lng";

    // FCM Storage
    private static final String KEY_FCM_TOKEN = "fcm_token";
    private static final String KEY_FCM_TOKEN_SYNCED = "fcm_token_synced";
    
    private static SessionManager instance;
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    // Construtor đóng (private) để ngăn chặn việc khởi tạo tự do từ bên ngoài
    private SessionManager(Context context) {
        // Sử dụng getApplicationContext() để tránh rò rỉ bộ nhớ khi Activity bị hủy
        this.sharedPreferences = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
    }

    // Hàm kiểm tra và cấp phát thực thể duy nhất (Thread-safe Singleton)
    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    /**
     * LƯU TRỮ THÔNG TIN KHI ĐĂNG NHẬP THÀNH CÔNG
     */
    public void createLoginSession(Long id, String token, String fullName, String phone, String avatarUrl, String email, String role) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putLong(KEY_USER_ID, id != null ? id : -1L);
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_USER_NAME, fullName);
        editor.putString(KEY_USER_PHONE, phone);
        editor.putString(KEY_USER_AVATAR, avatarUrl);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_ROLE, role);
        editor.apply();
    }
    /**
     * LẤY AUTHENTICATION TOKEN (JWT)
     * @return chuỗi token hoặc null nếu chưa đăng nhập
     */
    public String getAuthToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    /**
     * LẤY TÊN NGƯỜI DÙNG ĐỂ HIỂN THỊ LÊN UI
     */
    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, "Khách hàng");
    }

    public boolean isLoggedIn() {
        String token = getAuthToken();
        if (token == null || token.isEmpty() || isTokenExpired(token)) {
            if (token != null) {
                clearSession();
            }
            return false;
        }
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public boolean isTokenExpired(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) return true;
            String payload = parts[1];
            byte[] decodedBytes = Base64.decode(payload, Base64.DEFAULT);
            String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);
            JsonObject jsonObject = new Gson().fromJson(decodedString, JsonObject.class);
            if (jsonObject.has("exp")) {
                long exp = jsonObject.get("exp").getAsLong();
                long currentTimeSec = System.currentTimeMillis() / 1000;
                return currentTimeSec >= exp;
            }
        } catch (Exception e) {
            return true;
        }
        return false;
    }

    /**
     * ĐĂNG XUẤT - XÓA SẠCH DỮ LIỆU PHIÊN LÀM VIỆC
     */


    public void clearSession() {
        editor.remove(KEY_TOKEN);
        editor.remove(KEY_USER_NAME);
        editor.remove(KEY_USER_PHONE);
        editor.remove(KEY_USER_AVATAR);
        editor.remove(KEY_USER_EMAIL);
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USER_ROLE);
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.putBoolean(KEY_FCM_TOKEN_SYNCED, false);
        editor.apply();
    }


    public String getUserPhone() {
        return sharedPreferences.getString(KEY_USER_PHONE, "");
    }

    public String getUserAvatar() {
        return sharedPreferences.getString(KEY_USER_AVATAR, "");
    }

    public Long getUserId() {
        return sharedPreferences.getLong(KEY_USER_ID, -1L);
    }

    public void updateProfileSession(String fullName, String avatarUrl, String email) {
        editor.putString(KEY_USER_NAME, fullName);
        editor.putString(KEY_USER_AVATAR, avatarUrl);
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();
    }

    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, "");
    }

    public String getUserRole() {
        return sharedPreferences.getString(KEY_USER_ROLE, "");
    }

    /**
     * LƯU ĐỊA CHỈ GIAO HÀNG ĐANG CHỌN
     */
    public void saveDeliveryAddress(Long addressId, String addressText) {
        if (addressId == null) {
            editor.remove(KEY_DELIVERY_ADDRESS_ID);
        } else {
            editor.putLong(KEY_DELIVERY_ADDRESS_ID, addressId);
        }
        
        if (addressText == null) {
            editor.remove(KEY_DELIVERY_ADDRESS_TEXT);
        } else {
            editor.putString(KEY_DELIVERY_ADDRESS_TEXT, addressText);
        }
        editor.apply();
    }

    /**
     * LẤY ĐỊA CHỈ GIAO HÀNG ĐANG CHỌN
     * @return chuỗi địa chỉ hoặc null
     */
    public String getDeliveryAddressText() {
        return sharedPreferences.getString(KEY_DELIVERY_ADDRESS_TEXT, null);
    }
    
    public Long getDeliveryAddressId() {
        long id = sharedPreferences.getLong(KEY_DELIVERY_ADDRESS_ID, -1L);
        return id == -1L ? null : id;
    }

    /**
     * LƯU TỌA ĐỘ GIAO HÀNG ĐANG CHỌN
     */
    public void saveDeliveryCoordinates(Double lat, Double lng) {
        if (lat == null || lng == null) {
            editor.remove(KEY_DELIVERY_LAT);
            editor.remove(KEY_DELIVERY_LNG);
        } else {
            editor.putString(KEY_DELIVERY_LAT, String.valueOf(lat));
            editor.putString(KEY_DELIVERY_LNG, String.valueOf(lng));
        }
        editor.apply();
    }

    public Double getDeliveryLatitude() {
        String val = sharedPreferences.getString(KEY_DELIVERY_LAT, null);
        if (val != null) return Double.parseDouble(val);
        return null;
    }

    public Double getDeliveryLongitude() {
        String val = sharedPreferences.getString(KEY_DELIVERY_LNG, null);
        if (val != null) return Double.parseDouble(val);
        return null;
    }

    public void saveFcmToken(String fcmToken) {
        editor.putString(KEY_FCM_TOKEN, fcmToken);
        editor.apply();
    }

    public String getFcmToken() {
        return sharedPreferences.getString(KEY_FCM_TOKEN, null);
    }

    public void setFcmTokenSynced(boolean synced) {
        editor.putBoolean(KEY_FCM_TOKEN_SYNCED, synced);
        editor.apply();
    }

    public boolean isFcmTokenSynced() {
        return sharedPreferences.getBoolean(KEY_FCM_TOKEN_SYNCED, false);
    }
}