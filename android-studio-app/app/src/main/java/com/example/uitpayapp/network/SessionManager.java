package com.example.uitpayapp.network;

import android.content.Context;
import android.content.SharedPreferences;

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
    
    // Address storage
    private static final String KEY_DELIVERY_ADDRESS_ID = "delivery_address_id";
    private static final String KEY_DELIVERY_ADDRESS_TEXT = "delivery_address_text";
    
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
    public void createLoginSession(Long id, String token, String fullName, String phone, String avatarUrl, String email) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putLong(KEY_USER_ID, id != null ? id : -1L);
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_USER_NAME, fullName);
        editor.putString(KEY_USER_PHONE, phone);
        editor.putString(KEY_USER_AVATAR, avatarUrl);
        editor.putString(KEY_USER_EMAIL, email);
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

    /**
     * KIỂM TRA TRẠNG THÁI ĐĂNG NHẬP
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
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
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
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
}