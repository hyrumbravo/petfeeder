package com.example.petfeeder.Application;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.petfeeder.Constants;
import com.example.petfeeder.DataSharing.PetProviderConstants;
import com.example.petfeeder.DatabaseHelper;
import com.example.petfeeder.PetModel;
import com.example.petfeeder.RecordModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PetFeeder extends Application implements Application.ActivityLifecycleCallbacks{

    static PetFeeder instance;

    ContentResolver contentResolver;
    ContentObserver contentObserver;
    DatabaseHelper databaseHelper;

    PetModel petModel;

    Boolean contentProviderExists = false;
    Boolean previousContentProviderExists = false;

    ArrayList<RecordModel> unlistedPets;

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        instance = this;

        databaseHelper = new DatabaseHelper(this);
        unlistedPets = new ArrayList<>();

        //CHECK IF CONTENT PROVIDER EXISTS BEFORE USING IT.
        //SPECIFICALLY, THIS IS DETECTING IF PET FINDER IS INSTALLED USING ITS CONTENT URI.
        isContentUriExists();
        contentInitialize();

        //REGISTER THE LIFECYCLE CALLBACK. USED FOR DETECTING NEW DATA WHEN COMING BACK TO THE APP.
        registerActivityLifecycleCallbacks(this);
    }



    private void isContentUriExists() {
        Uri uri_pets = PetProviderConstants.CONTENT_URI_PETS;
        Uri uri_step = PetProviderConstants.CONTENT_URI_STEP;
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor_pets = null;
        Cursor cursor_step = null;

        try {
            cursor_pets = contentResolver.query(uri_pets, null, null, null, null);
            cursor_step = contentResolver.query(uri_step, null, null, null, null);
            contentProviderExists = cursor_pets != null && cursor_step != null;
        } catch (Exception e) {
            e.printStackTrace();
            contentProviderExists = false;
        } finally {
            if (cursor_pets != null) {
                cursor_pets.close();
            }
            if (cursor_step != null) {
                cursor_step.close();
            }
        }
        //IF CONTENT PROVIDER DOES NOT EXIST PREVIOUSLY, AND NOW EXISTS, INITIALIZE CONTENT
        if (contentProviderExists != previousContentProviderExists && contentProviderExists)
            contentInitialize();
    }

    private void contentInitialize(){
        if (contentProviderExists) {
            previousContentProviderExists = contentProviderExists;
            //IF PET FINDER IS INSTALLED
            contentResolver = getContentResolver(); //GET AN INSTANCE OF CONTENT RESOLVER.
            contentObserver = new ContentObserver(new Handler()) {
                //THIS OBSERVER IS FOR DETECTING CHANGES TO PET FINDER'S DATABASE.
                @Override
                public void onChange(boolean selfChange) {
                    super.onChange(selfChange);

                    getPedometerData();
                    setUnlistedPets();
                }
            };
            //REGISTER THE OBSERVER
            contentResolver.registerContentObserver(PetProviderConstants.CONTENT_URI_PETS, true, contentObserver);
            contentResolver.registerContentObserver(PetProviderConstants.CONTENT_URI_STEP, true, contentObserver);

            getPedometerData();
            setUnlistedPets();
        }
    }

    private String convertToSortableFormat(String date) {
        if (date == null) return null;
        SimpleDateFormat originalFormat = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date parsedDate = originalFormat.parse(date);
            return targetFormat.format(parsedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    //DATA GETTERS
    public static PetFeeder getInstance() {
        return instance;
    }
    public PetModel getPetModel() {
        return petModel;
    }

    //DATA SETTERS
    @SuppressLint("Range")
    private void setUnlistedPets(){
        if (!contentProviderExists) return;
        //GET ALL PETS WITHOUT PET FEEDER ID.
        Cursor cursor = contentResolver.query(PetProviderConstants.CONTENT_URI_PETS,
                null,
                Constants.COLUMN_PET_FEEDER_ID + " IS NULL",
                null,
                null,
                null);
        unlistedPets.clear();
        if (cursor!=null && cursor.getCount()>0){
            while (cursor.moveToNext()){
                //Reminder: Pets without a pet feeder id will not be stored in the database.
                // Although, it will still be displayed.
                RecordModel recordModel = new RecordModel(
                        ""+cursor.getString(cursor.getColumnIndex(Constants.COLUMN_PET_FEEDER_ID)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.COLUMN_PETNAME)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.COLUMN_BREED)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.COLUMN_SEX)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.COLUMN_AGE)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.COLUMN_WEIGHT)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.COLUMN_IMAGE)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.COLUMN_ADDED_TIMESTAMP)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.COLUMN_UPDATED_TIMESTAMP)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.COLUMN_ID)));
                unlistedPets.add(recordModel);
            }
            cursor.close();
        }
    }
    public ArrayList<RecordModel> getUnlistedPets() {
        return unlistedPets;
    }
    @SuppressLint("Range")
    private void getPedometerData(){
        if (!contentProviderExists) return;
        //GET PEDOMETER DATA THAT HAS A DATE THAT ISN'T IN THE DATABASE YET.
        String whereClause = "DATE(substr("+Constants.COLUMN_DATE+
                             ",7)||\"-\"||substr("+Constants.COLUMN_DATE+
                             ",1,2)||\"-\"||substr("+Constants.COLUMN_DATE+
                             ",4,2)) > ?";
        String date = convertToSortableFormat(databaseHelper.getLatestDateOfPedometerData());
        Cursor cursor = null;
        if (date == null){
            cursor = contentResolver.query(PetProviderConstants.CONTENT_URI_STEP,
                    null,
                    null,
                    null,
                    null,
                    null);
        } else {
            cursor = contentResolver.query(PetProviderConstants.CONTENT_URI_STEP,
                    null,
                    whereClause,
                    new String[]{date},
                    null,
                    null);
        }
        if (cursor!=null && cursor.getCount()>0){
            while (cursor.moveToNext()){
                Integer id = databaseHelper.getIdFromPetFinderID(cursor.getString(cursor.getColumnIndex(Constants.COLUMN_ID)));
                if (id != null) {
                    databaseHelper.storePedometerData(
                            id,
                            cursor.getInt(cursor.getColumnIndex(Constants.COLUMN_NUMSTEPS)),
                            cursor.getString(cursor.getColumnIndex(Constants.COLUMN_DATE))
                    );
                }
            }
            cursor.close();
        }
    }
    public void setPetModel(PetModel petModel) {
        this.petModel = petModel;
    }

    //APPLICATION LIFECYCLE IMPLEMENTATIONS
    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {}

    @Override
    public void onActivityStarted(@NonNull Activity activity) {}

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        isContentUriExists();
        getPedometerData();
        setUnlistedPets();
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {}

    @Override
    public void onActivityStopped(@NonNull Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {}
}