package com.example.petfeeder.Adapters;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.petfeeder.Application.PetFeeder;
import com.example.petfeeder.Components.About;
import com.example.petfeeder.Components.Credits;
import com.example.petfeeder.Components.Dashboard;
import com.example.petfeeder.R;
import com.google.android.material.navigation.NavigationView;

public class DrawerNav extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawerLayout;

    public void setContentView(View view){
        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_drawer_nav, null);
        FrameLayout container = drawerLayout.findViewById(R.id.activityContainer);
        container.addView(view);
        super.setContentView(drawerLayout);

        Toolbar toolbar = drawerLayout.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = drawerLayout.findViewById(R.id.navigationView);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        drawerLayout.closeDrawer(GravityCompat.START);
        switch (menuItem.getItemId()){

            case R.id.nav_dashboard:
                if (PetFeeder.getInstance().getDrawerNavID() != 0) {
                    startActivity(new Intent(DrawerNav.this, Dashboard.class));
                    PetFeeder.getInstance().setDrawerNavID(0);
                }
                break;

            case R.id.nav_about:
                if (PetFeeder.getInstance().getDrawerNavID() != 1) {
                    startActivity(new Intent(DrawerNav.this, About.class));
                    PetFeeder.getInstance().setDrawerNavID(1);
                }
                break;

            case R.id.nav_credits:
                if (PetFeeder.getInstance().getDrawerNavID() != 2) {
                    startActivity(new Intent(DrawerNav.this, Credits.class));
                    PetFeeder.getInstance().setDrawerNavID(2);
                }
                break;
        }
        return true;
    }

    protected void allocateActivityTitle (String titlesString) {
        if (getSupportActionBar() !=null) {
            getSupportActionBar().setTitle(titlesString);
        }
    }
}