package com.example.weather;

import android.os.Bundle;
import android.widget.Button;

import com.example.weather.base.BaseActivity;

public class DetailActivity extends BaseActivity {
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        backButton = findViewById(R.id.bt_detail_back);
        backButton.setOnClickListener(v -> finish());
    }
}