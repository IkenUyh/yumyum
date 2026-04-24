package com.example.uitpayapp.ScanQRCode;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uitpayapp.R;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

public class QRScanActivity extends AppCompatActivity {
    private static final int CAMERA_REQ = 101;
    private CompoundBarcodeView barcodeView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);
        View topBar = findViewById(R.id.top_bar_qrScan);
        ViewCompat.setOnApplyWindowInsetsListener(topBar, (v, insets) -> {
            Insets cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout());
            Insets systemBar = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int safeTopPadding = Math.max(cutout.top, systemBar.top) + 10;
            v.setPadding(v.getPaddingLeft(), safeTopPadding, v.getPaddingRight(), v.getPaddingBottom());
            //thanh duoi
            return insets;
        });
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQ);
        } else {
            barcodeView.resume();
        }
        barcodeView = findViewById(R.id.barcode_scanner);
        barcodeView.decodeContinuous(result -> {
            String qrData = result.getText();
            barcodeView.pause();
            finish();
        });
        findViewById(R.id.btn_close_scan).setOnClickListener(v->finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        barcodeView.resume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        barcodeView.pause();
    }
}
