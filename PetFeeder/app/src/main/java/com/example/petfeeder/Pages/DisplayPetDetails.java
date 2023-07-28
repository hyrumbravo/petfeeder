package com.example.petfeeder.Pages;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.petfeeder.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;


public class DisplayPetDetails extends AppCompatActivity {

    private CircularImageView petProfile;
    private TextView petName, petBreed, petSex, date, petWeight, age_textview,
            petAllergiesDisplay, petMedDisplay, petVetDisplay, petContactDisplay;
    private String recordID;
    private DatabaseHelper dbhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_pet_details);

        petProfile = findViewById(R.id.petImage);
        petName = findViewById(R.id.petNameDisplay);
        petBreed = findViewById(R.id.petBreedDisplay);
        petSex = findViewById(R.id.petSexDisplay);
        date = findViewById(R.id.petBdateDisplay);
        petWeight = findViewById(R.id.petWeightDisplay);
        age_textview = findViewById(R.id.petAgeDisplay);
        petAllergiesDisplay = findViewById(R.id.petAllergiesDisplay);
        petMedDisplay = findViewById(R.id.petMedDisplay);
        petVetDisplay = findViewById(R.id.petVetDisplay);
        petContactDisplay = findViewById(R.id.vetContactDisplay);

        Toolbar pet_toolbar = findViewById(R.id.pet_toolbar);

        pet_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DisplayPetDetails.this , EditPet.class));

            }
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        bottomNav.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.nav_petProfile:
                                break;
                            case R.id.nav_feed:
                                Intent intent = new Intent(DisplayPetDetails.this, ScanBluetooth.class);
                                startActivity(intent);
                                break;
                        }
                        return false;
                    }
                }
        );

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
            Intent intent = new Intent(DisplayPetDetails.this, EditPet.class);
            intent.putExtra("RECORD_ID", recordID);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showRecordDetails() {
        PetModel petModel = dbhelper.getRecordDetails(recordID);
        PetFeeder.getInstance().setPetModel(petModel);

        String image = petModel.getImage();
        if (image.equals("null")) petProfile.setImageResource(R.drawable.profile);
        else petProfile.setImageURI(Uri.parse(image));

        petName.setText(petModel.getName());
        petBreed.setText(petModel.getBreed());
        petSex.setText(petModel.getSex());
        age_textview.setText(String.format(petModel.getAge()>1?"%d Years Old":"%d Year Old", petModel.getAge()));
        date.setText(petModel.getBirthdate());
        petWeight.setText(String.format("%dkg", petModel.getWeight()));
        if (!petModel.getAllergies().isEmpty() || !Objects.equals(petModel.getAllergies(), "")) petAllergiesDisplay.setText(petModel.getAllergies());
        if (!petModel.getMedications().isEmpty() || !Objects.equals(petModel.getMedications(), "")) petMedDisplay.setText(petModel.getMedications());
        if (!petModel.getVetName().isEmpty() || !Objects.equals(petModel.getVetName(), "")) petVetDisplay.setText(petModel.getVetName());
        if (!petModel.getVetContact().isEmpty() || !Objects.equals(petModel.getVetContact(), "")) petContactDisplay.setText(petModel.getVetContact());
    }
}