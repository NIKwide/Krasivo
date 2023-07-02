package com.example.krasivo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

public class NoInternetActivity extends AppCompatActivity {

    private static final long RETRY_DELAY_MILLIS = 5000; // интервал между попытками подключения в миллисекундах
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);
        handler = new Handler();
        checkInternetConnection();
    }

    private void checkInternetConnection() {
        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork == null || !activeNetwork.isConnectedOrConnecting()) {
            showToast("Нет подключения к интернету");
            if (handler != null) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkInternetConnection();
                    }
                }, RETRY_DELAY_MILLIS);
            }
        } else {
            startMainActivity();
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
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