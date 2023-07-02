package com.example.krasivo;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {
    private EditText edName, edSecName, edEmail;
    private DatabaseReference mDataBase;
    private String USER_KEY = "User";
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        Button switchThemeButton = findViewById(R.id.btn_switch_theme);
        switchThemeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                boolean isDarkMode = nightMode == Configuration.UI_MODE_NIGHT_YES;
                setThemeMode(!isDarkMode);
            }
        });
    }

    private void init()
    {
        edName = findViewById(R.id.edName);
        edSecName = findViewById(R.id.edSecName);
        edEmail = findViewById(R.id.edEmail);
        mDataBase = FirebaseDatabase.getInstance().getReference(USER_KEY);
        sharedPreferences = getSharedPreferences("my_prefs", MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
    }

    public void onClickSave(View view)
    {
        String id = mDataBase.getKey();
        String name = edName.getText().toString();
        String sec_name = edSecName.getText().toString();
        String email = edEmail.getText().toString();
        User newUser = new User(id,name,sec_name,email);
        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(sec_name) && !TextUtils.isEmpty(email) )
        {
            mDataBase.push().setValue(newUser);
            Toast.makeText(this, "Сохранено", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Пустое поле", Toast.LENGTH_SHORT).show();
        }
    }
    public void onClickRead(View view)
    {
        Intent i = new Intent(MainActivity.this, ReadActivity.class);
        startActivity(i);
    }
    public void onClickSignOut(View view) {
        // Удаление сохраненных данных о пользователе из SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("email");
        editor.remove("password");
        editor.apply();

        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, LogoActivity.class);
        startActivity(intent);
        finish();
    }
    private void setThemeMode(boolean isDarkMode) {
        AppCompatDelegate.setDefaultNightMode(isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        recreate();
    }
}
