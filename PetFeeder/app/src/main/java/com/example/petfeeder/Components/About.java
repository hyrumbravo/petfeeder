package com.example.petfeeder.Components;

import android.os.Bundle;

import com.example.petfeeder.Adapters.DrawerNav;
import com.example.petfeeder.R;
import com.example.petfeeder.databinding.ActivityAboutBinding;

public class About extends DrawerNav {

    ActivityAboutBinding activityAboutBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        activityAboutBinding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(activityAboutBinding.getRoot());

        allocateActivityTitle("About");
    }
}