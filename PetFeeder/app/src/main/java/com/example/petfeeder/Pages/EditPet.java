package com.example.petfeeder.Pages;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.petfeeder.Application.PetFeeder;
import com.example.petfeeder.Database.DatabaseHelper;
import com.example.petfeeder.Models.PetModel;
import com.example.petfeeder.R;
import com.google.android.material.textfield.TextInputEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;

public class EditPet extends AppCompatActivity {

    ContentResolver contentResolver;
    TextInputEditText petName, petBreed, petWeight, bdate, ageEditText, allergies, treats, med, vetName,vetNum;

    RadioGroup psex;
    RadioButton radioButton;
    CircularImageView picture;
    private Uri imagePath;
    DatabaseHelper databaseHelper;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 101;
    private static final int IMAGE_PICK_CAMERA_CODE = 102;
    private static final int IMAGE_PICK_GALLERY_CODE = 103;
    private String[] cameraPermissions;
    private String[] storagePermissions;
    private Uri imageUri;
    private String pname, breed, sex, birthdate, addedTime, updatedTime, PFDD;
    private Integer id, age, weight;
    private boolean isEditMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pet);

        contentResolver = getContentResolver();
        petName = findViewById(R.id.editPetName);
        petBreed = findViewById(R.id.editBreed);
        psex = findViewById(R.id.sexRB);
        petWeight = findViewById(R.id.editWeight);
        bdate = findViewById(R.id.editBdate);
        ageEditText = findViewById(R.id.EditAge);
        picture = findViewById(R.id.editPetPic);
        allergies = findViewById(R.id.allergies);
        treats = findViewById(R.id.treats);
        med = findViewById(R.id.medication);
        vetName = findViewById(R.id.vetName);
        vetNum = findViewById(R.id.vetContact);
        databaseHelper = new DatabaseHelper(this);
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        PetModel petModel = PetFeeder.getInstance().getPetModel();
        id = petModel.getID();
        pname = petModel.getName();
        breed = petModel.getBreed();
        sex = petModel.getSex();
        birthdate = petModel.getBirthdate();
        age = petModel.getAge();
        weight = petModel.getWeight();
        imageUri = Uri.parse(petModel.getImage());
        petName.setText(pname);
        petBreed.setText(breed);
        // Set the selected radio button based on the sex
        if (sex.equals("Male")) {
            psex.check(R.id.maleRB);
        } else if (sex.equals("Female")) {
            psex.check(R.id.femaleRB);
        }
        bdate.setText(birthdate);
        ageEditText.setText(String.valueOf(age));
        petWeight.setText(String.valueOf(weight));
        if (imageUri.equals("null")) {
            picture.setImageResource(R.drawable.profile);
        } else {
            picture.setImageURI(imageUri);
        }

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
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

        bdate.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choseImage();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.update_menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.updatePet) {
            storeData();
            Intent intent = new Intent(EditPet.this, DisplayPetDetails.class);
            intent.putExtra("RECORD_ID", String.valueOf(this.id));
            startActivity(intent);
            finish();
            return true;
        }
        else if (id ==R.id.deletePet){
            databaseHelper.deleteData(String.valueOf(this.id));
            onResume();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Update the text of the EditText with the selected date
                        bdate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);

                        // Calculate age
                        Calendar currentDate = Calendar.getInstance();
                        int currentYear = currentDate.get(Calendar.YEAR);
                        int currentMonth = currentDate.get(Calendar.MONTH) + 1; // Months are zero-based
                        int currentDayOfMonth = currentDate.get(Calendar.DAY_OF_MONTH);
                        int selectedYear = year;
                        int selectedMonth = month + 1; // Months are zero-based
                        int selectedDayOfMonth = dayOfMonth;
                        int age = currentYear - selectedYear;
                        if (currentMonth < selectedMonth || (currentMonth == selectedMonth && currentDayOfMonth < selectedDayOfMonth)) {
                            age--; // Not yet reached the birthdate in the current year
                        }
                        // Display age
                        ageEditText.setText(String.valueOf(age));
                    }
                }, year, month, dayOfMonth);

        // Show the dialog
        datePickerDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
        datePickerDialog.show();
    }

    private  boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private void choseImage(){
        String[] options = {"Camera", "Gallery"};  // Separate the options with individual strings
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image From:");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int item) {
                if (options[item].equals("Camera")){
                    if(!checkCameraPermission()){
                        requestCameraPermission();
                    }
                    else {
                        pickFromCamera();
                    }
                }
                else if (options[item].equals("Gallery")) {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    }
                    else {
                        pickFromGallery();
                    }
                }
            }
        });
        builder.create().show();
    }

    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Image title");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image description");
        imagePath = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imagePath);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length>0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted && storageAccepted) {
                        pickFromCamera();
                    }
                    else {
                        Toast.makeText(this, "Camera & Storage permissions are required", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted){
                        pickFromGallery();
                    }
                    else {
                        Toast.makeText(this, "Storage permissions are required", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(this);
            }
            else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                CropImage.activity(imagePath)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(this);
            }
            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    imagePath = resultUri;
                    picture.setImageURI(resultUri);
                }
                else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    Toast.makeText(this, ""+error, Toast.LENGTH_SHORT).show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void storeData(){
        int selectID = psex.getCheckedRadioButtonId();
        radioButton = findViewById(selectID);
        if (selectID ==1){
            Toast.makeText(this, "Select Sex", Toast.LENGTH_SHORT).show();
        } else {
            radioButton.getText();
        }
        String petNameText = petName.getText().toString().trim();
        breed = ""+petBreed.getText().toString().trim();
        sex = ""+radioButton.getText().toString().trim();  // Update this line
        birthdate = bdate.getText().toString().trim();
        age = Integer.parseInt(ageEditText.getText().toString().trim().replaceAll("[^\\d]", ""));
        weight = Integer.valueOf(petWeight.getText().toString().trim());

        String timestamp = ""+System.currentTimeMillis();
        databaseHelper.updateData(
                id,
                ""+petNameText,
                ""+breed,
                ""+sex,
                ""+birthdate,
                ""+age,
                ""+weight,
                ""+imageUri,
                ""+addedTime,
                ""+timestamp,
                ""+PFDD);
    }
}