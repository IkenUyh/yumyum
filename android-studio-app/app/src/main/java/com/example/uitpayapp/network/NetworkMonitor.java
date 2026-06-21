package com.example.uitpayapp.network;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;

import com.example.uitpayapp.R;

import java.lang.ref.WeakReference;

public class NetworkMonitor implements Application.ActivityLifecycleCallbacks {

    private static NetworkMonitor instance;
    private final Application application;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private WeakReference<Activity> currentActivityRef = new WeakReference<>(null);
    private boolean isNetworkAvailable = true;
    private ConnectivityManager.NetworkCallback networkCallback;

    private NetworkMonitor(Application application) {
        this.application = application;
    }

    public static void initialize(Application application) {
        if (instance == null) {
            instance = new NetworkMonitor(application);
            application.registerActivityLifecycleCallbacks(instance);
            instance.startMonitoring();
        }
    }

    public static NetworkMonitor getInstance() {
        return instance;
    }

    private void startMonitoring() {
        ConnectivityManager connectivityManager = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return;

        // Check initial state
        isNetworkAvailable = checkNetworkAvailability(connectivityManager);

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                mainHandler.post(() -> {
                    if (!isNetworkAvailable) {
                        isNetworkAvailable = true;
                        hideNoInternetOverlay(currentActivityRef.get());
                    }
                });
            }

            @Override
            public void onLost(Network network) {
                mainHandler.post(() -> {
                    boolean currentStatus = checkNetworkAvailability(connectivityManager);
                    if (!currentStatus && isNetworkAvailable) {
                        isNetworkAvailable = false;
                        showNoInternetOverlay(currentActivityRef.get());
                    }
                });
            }
        };

        NetworkRequest.Builder builder = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        connectivityManager.registerNetworkCallback(builder.build(), networkCallback);
    }

    private boolean checkNetworkAvailability(ConnectivityManager connectivityManager) {
        Network activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork == null) return false;
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
        return capabilities != null && (
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        );
    }

    public boolean isNetworkAvailable() {
        return isNetworkAvailable;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        currentActivityRef = new WeakReference<>(activity);
        // Sync layout on resume
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        isNetworkAvailable = checkNetworkAvailability(connectivityManager);

        if (!isNetworkAvailable) {
            showNoInternetOverlay(activity);
        } else {
            hideNoInternetOverlay(activity);
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (currentActivityRef.get() == activity) {
            currentActivityRef.clear();
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    private void showNoInternetOverlay(Activity activity) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) return;

        ViewGroup root = activity.findViewById(android.R.id.content);
        if (root == null) return;

        // Check if already showing
        View existingOverlay = root.findViewById(R.id.layout_no_internet_root);
        if (existingOverlay != null) return;

        // Inflate the overlay layout
        LayoutInflater inflater = LayoutInflater.from(activity);
        View overlay = inflater.inflate(R.layout.layout_no_internet, root, false);

        // Configure margins if bottom navigation bar exists
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) overlay.getLayoutParams();
        if (params == null) {
            params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        View bottomBar = activity.findViewById(R.id.bottom_container);
        if (bottomBar == null) bottomBar = activity.findViewById(R.id.bottomNavContainer);
        if (bottomBar == null) bottomBar = activity.findViewById(R.id.layoutBottomNav);

        if (bottomBar != null && bottomBar.getVisibility() == View.VISIBLE) {
            final View finalBottomBar = bottomBar;
            final View finalOverlay = overlay;
            final Runnable updateMargin = () -> {
                int height = finalBottomBar.getHeight();
                if (height > 0) {
                    FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) finalOverlay.getLayoutParams();
                    if (lp != null && lp.bottomMargin != height) {
                        lp.bottomMargin = height;
                        finalOverlay.setLayoutParams(lp);
                    }
                }
            };

            if (bottomBar.getHeight() > 0) {
                params.bottomMargin = bottomBar.getHeight();
            } else {
                params.bottomMargin = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 90, activity.getResources().getDisplayMetrics());
            }

            final View.OnLayoutChangeListener layoutChangeListener = new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                           int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    updateMargin.run();
                }
            };

            bottomBar.addOnLayoutChangeListener(layoutChangeListener);
            overlay.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {}

                @Override
                public void onViewDetachedFromWindow(View v) {
                    finalBottomBar.removeOnLayoutChangeListener(layoutChangeListener);
                }
            });

            bottomBar.post(updateMargin);
        }

        overlay.setLayoutParams(params);

        // Set up retry button
        AppCompatButton btnRetry = overlay.findViewById(R.id.btn_no_internet_retry);
        btnRetry.setOnClickListener(v -> {
            ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (checkNetworkAvailability(cm)) {
                isNetworkAvailable = true;
                hideNoInternetOverlay(activity);
                activity.recreate();
            } else {
                Toast.makeText(activity, "Vui lòng kiểm tra kết nối mạng và thử lại!", Toast.LENGTH_SHORT).show();
            }
        });

        root.addView(overlay);
    }

    private void hideNoInternetOverlay(Activity activity) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) return;

        ViewGroup root = activity.findViewById(android.R.id.content);
        if (root == null) return;

        View existingOverlay = root.findViewById(R.id.layout_no_internet_root);
        if (existingOverlay != null) {
            root.removeView(existingOverlay);
        }
    }
}
