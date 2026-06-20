package com.example.uitpayapp.deliveryaddressorder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uitpayapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Looper;

public class MapPickerActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    MapView mapView;
    EditText etSearchAddress;
    View mapPickerFinish, mapPickerCurrentLocation;
    FusedLocationProviderClient fusedLocationClient;
    Location userLocation;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateRunnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_map_picker);
        Configuration.getInstance().setUserAgentValue(getPackageName());
        fusedLocationClient= LocationServices.getFusedLocationProviderClient(this);
        initView();
        setListener();
        setUpMap();
        Intent intent=getIntent();
        String address=intent.getStringExtra("ADDRESS_PUT");
        if(address!=null&&!address.isEmpty())
            goToSearchAddress(address);
        else
            goToCurrentPosition();
    }
    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        View topBar = findViewById(R.id.top_bar_map_picker);
        mapView = findViewById(R.id.map_view);
        etSearchAddress = findViewById(R.id.et_search_address);
        mapPickerFinish = findViewById(R.id.map_picker_finish);
        mapPickerCurrentLocation = findViewById(R.id.map_picker_current_location);
        topBar.findViewById(R.id.top_bar_back_btn).setOnClickListener(v -> finish());
        TextView tvTitle = topBar.findViewById(R.id.top_bar_title);
        View mainContainer = findViewById(R.id.map_picker_container);
        tvTitle.setText("Quản lý thẻ/tài khoản");
        ViewCompat.setOnApplyWindowInsetsListener(topBar, (v, insets) -> {
            Insets cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout());
            Insets systemBar = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int safeTopPadding = Math.max(cutout.top, systemBar.top) + 10;
            v.setPadding(v.getPaddingLeft(), safeTopPadding, v.getPaddingRight(), v.getPaddingBottom());
            //thanh duoi
            Insets navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            int safeBottomPadding = navInsets.bottom;
            if (mainContainer != null) {
                mainContainer.setPadding(mainContainer.getPaddingLeft(), mainContainer.getPaddingTop(), mainContainer.getPaddingRight(), safeBottomPadding);
            }
            return insets;
        });
        etSearchAddress.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                goToSearchAddress(etSearchAddress.getText().toString());
                return true;
            }
            return false;
        });
        mapView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP){
                if (updateRunnable != null) {
                    handler.removeCallbacks(updateRunnable);
                }
                updateRunnable = () -> {
                    GeoPoint newPosition = new GeoPoint(mapView.getMapCenter().getLatitude(), mapView.getMapCenter().getLongitude());
                    updatePosition(newPosition);
                };
                handler.postDelayed(updateRunnable, 1000);

            }
            return false;//coi nhu chua xu ly de cac su kien khac lam viec
        });
        mapPickerFinish.setOnClickListener(v -> {
            Intent intent=new Intent();
            intent.putExtra("ADDRESS_SELECTED",etSearchAddress.getText().toString());
            intent.putExtra("LATITUDE_SELECTED", mapView.getMapCenter().getLatitude());
            intent.putExtra("LONGITUDE_SELECTED", mapView.getMapCenter().getLongitude());
            setResult(RESULT_OK,intent);
            finish();
        });
    }
    private void setListener()
    {
        mapPickerCurrentLocation.setOnClickListener(v -> goToCurrentPosition());
    }
    private void setUpMap()
    {
        //mac dinh
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(16.0);
        mapView.setMaxZoomLevel(20.0);
        //lan luot vi do (ngang) va kinh do
        GeoPoint startPoint = new GeoPoint(10.87044, 106.80217);
        mapView.getController().setCenter(startPoint);
        mapView.addMapListener(new MapListener()
        {
            @Override
            public boolean onScroll(ScrollEvent event) {
                return false;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                return false;
            }
        });
    }
    private void goToCurrentPosition()
    {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else
        {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location ->
                {
                    userLocation=location;
                    if (location != null) {
                        GeoPoint newPosition = new GeoPoint(location.getLatitude(), location.getLongitude());
                        updatePosition(newPosition);
                    }
                });
        }
    }
    private void goToSearchAddress(String address)
    {
        executorService.execute(() -> {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocationName(address, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address firstAddress = addresses.get(0);
                    GeoPoint newPosition = new GeoPoint(firstAddress.getLatitude(), firstAddress.getLongitude());
                    runOnUiThread(() -> {
                        mapView.getController().setZoom(16.0);
                        mapView.getController().animateTo(newPosition);
                        etSearchAddress.setText(address);
                    });
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    private void updatePosition(GeoPoint newPosition)
    {
        mapView.getController().setCenter(newPosition);
        //lay ngon ngu theo may, do hien tai ko thay co TV
        executorService.execute(() -> {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(newPosition.getLatitude(), newPosition.getLongitude(), 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address firstAddress = addresses.get(0);
                    String address = firstAddress.getAddressLine(0);
                    runOnUiThread(() ->{
                        mapView.getController().animateTo(newPosition);
                        etSearchAddress.setText(address);
                    });
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                goToCurrentPosition();
            }
        }
    }
    @Override
    protected void onDestroy() {
        executorService.shutdown();
        if (updateRunnable != null) {
            handler.removeCallbacks(updateRunnable);
        }
        super.onDestroy();
    }

}
