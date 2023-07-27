package com.example.petfeeder.Application;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.petfeeder.DataSharing.PetProviderConstants;
import com.example.petfeeder.Database.Constants;
import com.example.petfeeder.Database.DatabaseHelper;
import com.example.petfeeder.Models.PetModel;
import com.example.petfeeder.Models.RecordModel;

import java.util.ArrayList;

public class PetFeeder extends Application implements Application.ActivityLifecycleCallbacks{

    static PetFeeder instance;

    ContentResolver contentResolver;
    ContentObserver contentObserver;
    DatabaseHelper databaseHelper;

    private int drawerNavID = 0;

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

        try (Cursor cursor_pets = getContentResolver().query(PetProviderConstants.CONTENT_URI_PETS, null, null, null, null)) {
            contentProviderExists = cursor_pets != null;
        } catch (Exception e) {
            e.printStackTrace();
            contentProviderExists = false;
        }
        //IF CONTENT PROVIDER DOES NOT EXIST PREVIOUSLY, AND NOW EXISTS, INITIALIZE CONTENT
        if (contentProviderExists != previousContentProviderExists && contentProviderExists)
            contentInitialize();
    }

    private void contentInitialize(){
        if (contentProviderExists) {
            previousContentProviderExists = true;
            //IF PET FINDER IS INSTALLED
            contentResolver = getContentResolver(); //GET AN INSTANCE OF CONTENT RESOLVER.
            contentObserver = new ContentObserver(new Handler()) {
                //THIS OBSERVER IS FOR DETECTING CHANGES TO PET FINDER'S DATABASE.
                @Override
                public void onChange(boolean selfChange) {
                    super.onChange(selfChange);

                    setUnlistedPets();
                }
            };
            //REGISTER THE OBSERVER
            contentResolver.registerContentObserver(PetProviderConstants.CONTENT_URI_PETS, true, contentObserver);

            setUnlistedPets();
        }
    }

    //DATA GETTERS
    public static PetFeeder getInstance() {
        return instance;
    }
    public PetModel getPetModel() {
        return petModel;
    }
    public int getDrawerNavID() {
        return drawerNavID;
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
                        ""+cursor.getString(cursor.getColumnIndex(Constants.COLUMN_BIRTHDATE)),
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
        setUnlistedPets();
        return unlistedPets;
    }
    @SuppressLint("Range")
    public void setPetModel(PetModel petModel) {
        this.petModel = petModel;
    }
    public void setDrawerNavID(int drawerNavID) {
        this.drawerNavID = drawerNavID;
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