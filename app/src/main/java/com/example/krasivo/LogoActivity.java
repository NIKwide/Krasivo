package com.example.krasivo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogoActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "my_prefs";
    private static final String PREF_EMAIL = "email";
    private static final String PREF_PASSWORD = "password";

    private EditText edLogin, edPassword;
    private Button bStart, bSignUp, bSignIn, signOut;
    private TextView tvUserName;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        initViews();
        checkUserLoggedIn();
    }

    private void initViews() {
        signOut = findViewById(R.id.bSignOut);
        tvUserName = findViewById(R.id.tvUserEmail);
        bStart = findViewById(R.id.bStart);
        bSignIn = findViewById(R.id.bSignIn);
        bSignUp = findViewById(R.id.bSignUp);
        edLogin = findViewById(R.id.edLogin);
        edPassword = findViewById(R.id.edPassword);
        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    }

    private void checkUserLoggedIn() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userName = "dsadasd" + currentUser.getEmail();
            tvUserName.setText(userName);
            showToast("User not null");
            startStoreActivity();
        } else {
            showToast("User null");
        }
    }

    public void onClickSignUp(View view) {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    public void onClickSignIn(View view) {
        String email = edLogin.getText().toString().trim();
        String password = edPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            showToast("Пожалуйста, заполните все поля");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null && user.isEmailVerified()) {
                            showToast("Вход пользователя выполнен успешно");

                            saveUserCredentials(email, password);
                            startStoreActivity();
                        } else {
                            showToast("Пожалуйста, подтвердите адрес электронной почты");
                        }
                    } else {
                        showToast("Ошибка входа пользователя");
                    }
                });
    }

    private void saveUserCredentials(String email, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_EMAIL, email);
        editor.putString(PREF_PASSWORD, password);
        editor.apply();
    }

    public void onClickStart(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void startStoreActivity() {
        Intent intent = new Intent(this, Store.class);
        startActivity(intent);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}