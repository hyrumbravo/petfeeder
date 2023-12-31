package com.example.petfeeder.SplashScreen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.petfeeder.Components.Dashboard;
import com.example.petfeeder.R;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    ProgressBar pb;
    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prog();
    }
    public void prog() {
        pb = findViewById(R.id.pb);

        final Timer t = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                counter++;
                pb.setProgress(counter);

                if(counter == 100) {
                    t.cancel();
                    Intent intent = new Intent(MainActivity.this, Dashboard.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        t.schedule(tt, 0, 30);
    }
}