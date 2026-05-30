package com.example.uitpayapp.utils;

import android.app.Activity;
import android.view.View;
import com.example.uitpayapp.R;

public class KeypadManager {

    // Interface để giao tiếp ngược lại với Activity/Fragment
    public interface KeypadListener {
        void onPasscodeChange(String currentPasscode); // Gọi mỗi khi nhập hoặc xóa 1 số
        void onPasscodeComplete(String passcode);      // Gọi khi đã nhập đủ số lượng quy định
    }

    private String passcode = "";
    private final int maxLength;
    private boolean isLocked = false;
    private final KeypadListener listener;

    /**
     * Khởi tạo KeypadManager
     * @param rootView View gốc chứa bàn phím (Activity hoặc View)
     * @param maxLength Độ dài tối đa của mã PIN/OTP
     * @param listener Callback để xử lý sự kiện
     */
    public KeypadManager(Activity rootView, int maxLength, KeypadListener listener) {
        this.maxLength = maxLength;
        this.listener = listener;
        setupListeners(rootView);
    }

    // Ánh xạ và gắn sự kiện cho các nút
    private void setupListeners(Activity rootView) {
        int[] buttonIds = {R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4,
                R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9};

        for (int i = 0; i < buttonIds.length; i++) {
            final String number = String.valueOf(i);
            rootView.findViewById(buttonIds[i]).setOnClickListener(v -> onNumberClick(number));
        }

        rootView.findViewById(R.id.btn_delete).setOnClickListener(v -> onDeleteClick());
    }

    // Xử lý sự kiện nhập số
    private void onNumberClick(String number) {
        // Chặn nhập nếu đang bị khóa (VD: đang gọi API) hoặc đã nhập đủ tối đa
        if (isLocked || passcode.length() >= maxLength) return;

        passcode += number;

        if (listener != null) {
            listener.onPasscodeChange(passcode);
        }

        // Tự động kích hoạt sự kiện hoàn thành khi đủ số
        if (passcode.length() == maxLength) {
            if (listener != null) {
                listener.onPasscodeComplete(passcode);
            }
        }
    }

    // Xử lý sự kiện nút xóa
    private void onDeleteClick() {
        // Chặn xóa nếu đang bị khóa hoặc chưa có dữ liệu nào
        if (isLocked || passcode.isEmpty()) return;

        passcode = passcode.substring(0, passcode.length() - 1);

        if (listener != null) {
            listener.onPasscodeChange(passcode);
        }
    }

    // --- CÁC HÀM TIỆN ÍCH DÀNH CHO ACTIVITY/FRAGMENT ---

    /** Khóa bàn phím (Thường dùng khi đang loading API) */
    public void lock() {
        isLocked = true;
    }

    /** Mở khóa bàn phím */
    public void unlock() {
        isLocked = false;
    }

    /** Xóa trắng dữ liệu (Thường dùng khi nhập sai) */
    public void clear() {
        passcode = "";
        if (listener != null) {
            listener.onPasscodeChange(passcode);
        }
    }

    public String getPasscode() {
        return passcode;
    }
}