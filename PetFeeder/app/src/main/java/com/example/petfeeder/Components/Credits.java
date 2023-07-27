package com.example.petfeeder.Components;

import android.os.Bundle;

import com.example.petfeeder.Adapters.DrawerNav;
import com.example.petfeeder.R;
import com.example.petfeeder.databinding.ActivityCreditsBinding;

public class Credits extends DrawerNav {

    ActivityCreditsBinding activityCreditsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        activityCreditsBinding = ActivityCreditsBinding.inflate(getLayoutInflater());
        setContentView(activityCreditsBinding.getRoot());

        allocateActivityTitle("Credits");
    }
}