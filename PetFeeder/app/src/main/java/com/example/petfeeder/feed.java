package com.example.petfeeder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.petfeeder.Bluetooth.Bluetooth;

public class feed extends AppCompatActivity {

    Button loose_weight, bluetooth, not_diet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        loose_weight = findViewById(R.id.loose_weight);
        loose_weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(feed.this, Diet.class);
                startActivity(intent);
            }
        });


        bluetooth = findViewById(R.id.bluetooth);
        bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(feed.this, Bluetooth.class);
                startActivity(intent);
            }
        });
    }
}