package com.example.petfeeder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.petfeeder.Application.PetFeeder;
import com.example.petfeeder.Database.DatabaseHelper;
import com.example.petfeeder.Models.PetModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class DisplayPetDetails extends AppCompatActivity {

    BottomNavigationView navigationView;
    Toolbar pet_toolbar;

    private CircularImageView petProfile;
    private TextView petNameDisplay, petBreedDisplay, petSexDisplay, petBdateDisplay, petAgeDisplay, petWeightDisplay;
    private String recordID;
    private DatabaseHelper dbhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_pet_details);

        pet_toolbar = findViewById(R.id.pet_toolbar);

        pet_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DisplayPetDetails.this , EditPet.class));

            }
        });

        navigationView = findViewById(R.id.bottomNav);

        navigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.nav_petProfile:
                                break;
                            case R.id.nav_feed:
                                Intent intent = new Intent(DisplayPetDetails.this, feed.class);
                                startActivity(intent);
                                break;
                            case R.id.nav_statistics:
                                break;
                        }
                        return false;
                    }
                }
        );



        petProfile = findViewById(R.id.petImage);
        petNameDisplay = findViewById(R.id.petNameDisplay);
        petBreedDisplay = findViewById(R.id.petBreedDisplay);
        petSexDisplay = findViewById(R.id.petSexDisplay);
        petBdateDisplay = findViewById(R.id.petBdateDisplay);
        petAgeDisplay = findViewById(R.id.petAgeDisplay);
        petWeightDisplay = findViewById(R.id.petWeightDisplay);

        Intent intent = getIntent();
        recordID = intent.getStringExtra("RECORD_ID");

        dbhelper = new DatabaseHelper(this);

        Toolbar myToolbar = findViewById(R.id.pet_toolbar);
        setSupportActionBar(myToolbar);

        // Set the custom back arrow as the navigation icon
        myToolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        // Set a click listener on the navigation icon
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        showRecordDetails();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.editPet) {
            startActivity(new Intent(DisplayPetDetails.this, EditPet.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint({"Range", "SetTextI18n"})
    private void showRecordDetails() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        PetModel petModel = databaseHelper.getRecordDetails(recordID);
        petNameDisplay.setText(petModel.getName());
        petBreedDisplay.setText(petModel.getBreed());
        petSexDisplay.setText(petModel.getSex());
        petBdateDisplay.setText(petModel.getBirthdate());
        petAgeDisplay.setText(petModel.getAge()+" y/o");
        petWeightDisplay.setText(petModel.getWeight()+" Kg");
        PetFeeder.getInstance().setPetModel(petModel);
    }
}
