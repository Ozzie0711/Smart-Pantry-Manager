package com.smartpantry.manager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        TextView txtSettingsTitle = findViewById(R.id.txtSettingsTitle);
        Button btnBack = findViewById(R.id.btnBack);

        txtSettingsTitle.setText("Smart Pantry Manager Settings");

        btnBack.setOnClickListener(v -> finish());
    }
}