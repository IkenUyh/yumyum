package com.example.uitpayapp.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewGroup;
import android.view.Window;

import com.example.uitpayapp.R;
import com.example.uitpayapp.auth.SignInActivity;

public class LoginPopupHelper {

    public static void showLoginRequiredPopup(Context context) {
        Dialog loginDialog = new Dialog(context);
        loginDialog.setContentView(R.layout.dialog_login_required);
        Window window = loginDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            // Set width to 85% of screen width instead of MATCH_PARENT to make it smaller
            int width = (int)(context.getResources().getDisplayMetrics().widthPixels * 0.85);
            window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        loginDialog.findViewById(R.id.btn_cancel).setOnClickListener(view -> loginDialog.dismiss());
        loginDialog.findViewById(R.id.btn_login).setOnClickListener(view -> {
            loginDialog.dismiss();
            Intent intent = new Intent(context, SignInActivity.class);
            context.startActivity(intent);
        });

        loginDialog.show();
    }
}
