package com.example.krasivo;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Objects;

public class SplashActivity extends AppCompatActivity {
    private static final int DELAY_MILLIS = 2000; // задержка в миллисекундах
    private static final int RETRY_DELAY_MILLIS = 5000; // интервал между попытками подключения в миллисекундах
    private FirebaseAuth firebaseAuth;
    private Intent nextActivityIntent;
    private ImageView logoImageView;
    private TextView loadingTextView;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initViews();
        if (handler == null) {
            handler = new Handler();
        }
        checkInternetConnection();
        logoImageView.postDelayed(() -> startNextActivity(), DELAY_MILLIS);
    }

    private void initViews() {
        logoImageView = Objects.requireNonNull(findViewById(R.id.logoImageView));
        loadingTextView = Objects.requireNonNull(findViewById(R.id.loadingTextView));
        firebaseAuth = FirebaseAuth.getInstance();
        nextActivityIntent = new Intent(this, isUserLoggedIn() ? Store.class : LogoActivity.class);
    }

    private void checkInternetConnection() {
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork == null || !activeNetwork.isConnectedOrConnecting()) {
            showToast("Нет подключения к интернету");
            nextActivityIntent = new Intent(this, NoInternetActivity.class);
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkInternetConnection();
                    }
                }, RETRY_DELAY_MILLIS);
            }
        }
    }

    private void startNextActivity() {
        startActivity(nextActivityIntent);
        finish();
    }

    private boolean isUserLoggedIn() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        return currentUser != null;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}