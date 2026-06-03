package com.example.uitpayapp.ScanQRCode;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.graphics.shapes.Utils;

import com.example.uitpayapp.R;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class QRScanActivity extends AppCompatActivity {
    private static final int CAMERA_REQ = 101;
    private CompoundBarcodeView barcodeView;
    private Boolean isFlashOn=false;
    ImageView btnFlash;
    View useImgFromStore;
     /* @Override
     protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);
        initView();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQ);
        } else {
            barcodeView.resume();
        }
        barcodeView.decodeContinuous(result -> {
            barcodeView.pause();
            String qrData = result.getText();
            FinishWithResult(qrData);
        });
        findViewById(R.id.btn_close_scan).setOnClickListener(v -> finish());
        btnFlash.setOnClickListener(v -> HandleFlashClick());
        useImgFromStore.setOnClickListener(v->
        {
            selectedImgFromStore.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build());
        });
    } */
    /* private void initView()
    {
        barcodeView = findViewById(R.id.barcode_scanner);
        View topBar = findViewById(R.id.top_bar_qrScan);
        btnFlash = findViewById(R.id.qrscan_btn_flash);
        useImgFromStore = findViewById(R.id.qrscan_useimg_instore);
        ViewCompat.setOnApplyWindowInsetsListener(topBar, (v, insets) -> {
            Insets systemBar = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBar.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });
        String scan_type = getIntent().getStringExtra("SCAN_TYPE");
        if ("CCCD".equals(scan_type)) {
            View isScanTransfer = findViewById(R.id.isScanTransfer);
            if (isScanTransfer != null) isScanTransfer.setVisibility(View.GONE);
            TextView isScanCCCD = findViewById(R.id.isScanCCCD);
            if (isScanCCCD != null) isScanCCCD.setVisibility(View.VISIBLE);
        }
    } */
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
    private void HandleFlashClick()
    {
        if (isFlashOn)
        {
            barcodeView.setTorchOff();
            isFlashOn=false;
            btnFlash.setImageTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#757575")));
        } else
        {
            barcodeView.setTorchOn();
            isFlashOn=true;
            btnFlash.setImageTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
        }
    }
    private final ActivityResultLauncher<PickVisualMediaRequest> selectedImgFromStore=registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri->{
        if (uri!=null)
        {
            HandleQRCodeFromUri(uri);
        }
    });
    private void HandleQRCodeFromUri(Uri uri)
    {
        try {
            // cho nay hoi chat
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap frame = BitmapFactory.decodeStream(inputStream);
            int[] pixels = new int[frame.getWidth() * frame.getHeight()];
            frame.getPixels(pixels, 0, frame.getWidth(), 0, 0, frame.getWidth(), frame.getHeight());
            LuminanceSource source = new RGBLuminanceSource(frame.getWidth(), frame.getHeight(), pixels);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Result result = new MultiFormatReader().decode(bitmap);
            FinishWithResult(result.getText());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void FinishWithResult(String qrData)
    {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("QR_DATA", qrData);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
