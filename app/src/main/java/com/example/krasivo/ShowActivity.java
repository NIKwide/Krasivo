package com.example.krasivo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ShowActivity extends AppCompatActivity {
    private TextView tvName, tvSecName, tvEmail;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_layout);
        init();
        getIntentMain();
    }
    private void init()
    {
        tvName = findViewById(R.id.tvName);
        tvSecName = findViewById(R.id.tvSecName);
        tvEmail = findViewById(R.id.tvEmail);
    }
    private void getIntentMain()
    {
        Intent i = getIntent();
        if(i != null)
        {
            tvName.setText(i.getStringExtra(Constant.USER_NAME));
            tvSecName.setText(i.getStringExtra(Constant.USER_SEC_NAME));
            tvEmail.setText(i.getStringExtra(Constant.USER_EMAIL));
        }
    }
}