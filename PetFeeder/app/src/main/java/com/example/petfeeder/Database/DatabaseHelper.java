package com.example.petfeeder.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.example.petfeeder.Models.PetModel;
import com.example.petfeeder.Models.RecordModel;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

@SuppressLint("Range")
public class DatabaseHelper extends SQLiteOpenHelper {

    public Context context;

    public DatabaseHelper(@Nullable Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Constants.query);
        db.execSQL(Constants.query2);
        db.execSQL(Constants.query3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME2);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TABLE_NAME3);

        onCreate(db);
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder, Context context) {
        // Implement the query operation for the Content Provider
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor;

        cursor = db.query(Constants.TABLE_NAME + " LEFT JOIN " + Constants.TABLE_NAME3,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);


        // Set the notification URI on the cursor
        if (context != null) {
            cursor.setNotificationUri(context.getContentResolver(), uri);
        }

        return cursor;
    }

    public long storeData(String petName, String breed, String sex, String bdate, String age,
                          String weight, String petPic, String addedtime, String updatedtime) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_PETNAME, petName);
        values.put(Constants.COLUMN_BREED, breed);
        values.put(Constants.COLUMN_SEX, sex);
        values.put(Constants.COLUMN_BIRTHDATE, bdate);
        values.put(Constants.COLUMN_AGE, age);
        values.put(Constants.COLUMN_WEIGHT, weight);
        values.put(Constants.COLUMN_IMAGE, petPic);
        values.put(Constants.COLUMN_ADDED_TIMESTAMP, addedtime);
        values.put(Constants.COLUMN_UPDATED_TIMESTAMP, updatedtime);

        long id = db.insert(Constants.TABLE_NAME, null, values);  // Corrected table name usage
        db.close();
        return id;
    }
    public long storeData(String petName, String breed, String sex, String bdate, String age,
                          String weight, String petPic, String addedtime, String updatedtime, String petFinderId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_PETNAME, petName);
        values.put(Constants.COLUMN_BREED, breed);
        values.put(Constants.COLUMN_SEX, sex);
        values.put(Constants.COLUMN_BIRTHDATE, bdate);
        values.put(Constants.COLUMN_AGE, age);
        values.put(Constants.COLUMN_WEIGHT, weight);
        values.put(Constants.COLUMN_IMAGE, petPic);
        values.put(Constants.COLUMN_ADDED_TIMESTAMP, addedtime);
        values.put(Constants.COLUMN_UPDATED_TIMESTAMP, updatedtime);
        values.put(Constants.COLUMN_PET_FINDER_ID, petFinderId);

        long id = db.insert(Constants.TABLE_NAME, null, values);  // Corrected table name usage
        db.close();
        return id;
    }
    public int updateData(Integer id, String petName, String breed, String sex, String bdate, String age,
                          String weight, String petPic, String updatedtime, String petFinderID, String allergies,
                          String medications, String vetName, String vetContact) {
        if (petName == null || breed == null || sex == null || bdate == null || age == null || weight == null || petPic == null || updatedtime == null || petFinderID == null ) return -1;
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_PETNAME, petName);
        values.put(Constants.COLUMN_BREED, breed);
        values.put(Constants.COLUMN_SEX, sex);
        values.put(Constants.COLUMN_BIRTHDATE, bdate);
        values.put(Constants.COLUMN_AGE, age);
        values.put(Constants.COLUMN_WEIGHT, weight);
        values.put(Constants.COLUMN_IMAGE, petPic);
        values.put(Constants.COLUMN_UPDATED_TIMESTAMP, updatedtime);
        values.put(Constants.COLUMN_PET_FINDER_ID, petFinderID);

        int returnValue = db.update(Constants.TABLE_NAME, values, Constants.COLUMN_ID +" = ?", new String[] {String.valueOf(id)});
        updateHealthInfo(id, allergies, medications, vetName, vetContact);
        db.close();
        return returnValue;
    }
    public PetModel getRecordDetails(String recordID) {
        if (recordID == null) return new PetModel();
        String selectQuery = "SELECT * FROM " + Constants.TABLE_NAME + " WHERE " + Constants.COLUMN_ID + "= ?";
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{recordID});
        PetModel petModel = new PetModel();

        if (cursor.moveToFirst()) {
            do {
                petModel.setID(cursor.getInt(cursor.getColumnIndex(Constants.COLUMN_ID)));
                petModel.setName(
                        ""+cursor.getString(cursor.getColumnIndex(Constants.COLUMN_PETNAME)));
                petModel.setBreed(
                        "" +cursor.getString(cursor.getColumnIndex(Constants.COLUMN_BREED)));
                petModel.setSex(
                        ""+cursor.getString(cursor.getColumnIndex(Constants.COLUMN_SEX)));
                petModel.setAge(
                        cursor.getInt(cursor.getColumnIndex(Constants.COLUMN_AGE)));
                petModel.setWeight(
                        cursor.getInt(cursor.getColumnIndex(Constants.COLUMN_WEIGHT)));
                petModel.setBirthdate(
                        cursor.getString(cursor.getColumnIndex(Constants.COLUMN_BIRTHDATE)));
                petModel.setImage(
                        ""+cursor.getString(cursor.getColumnIndex(Constants.COLUMN_IMAGE)));
                petModel.setPetFinderID(
                        ""+cursor.getString(cursor.getColumnIndex(Constants.COLUMN_PET_FINDER_ID)));
            } while (cursor.moveToNext());
        }
        selectQuery = "SELECT * FROM " + Constants.TABLE_NAME3 + " WHERE " + Constants.COLUMN_ID + "=\"" + recordID + "\"";
        cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount()>0) {
            if (cursor.moveToFirst()) {
                petModel.setAllergies(
                        "" + cursor.getString(cursor.getColumnIndex(Constants.COLUMN_ALLERGIES)));
                petModel.setMedications(
                        "" + cursor.getString(cursor.getColumnIndex(Constants.COLUMN_MEDICATIONS)));
                petModel.setVetName(
                        "" + cursor.getString(cursor.getColumnIndex(Constants.COLUMN_VETNAME)));
                petModel.setVetContact(
                        cursor.getString(cursor.getColumnIndex(Constants.COLUMN_VETCONTACT)));
            }
        }
        db.close();
        db.close();
        return petModel;
    }

    private long updateHealthInfo(Integer Id, String Allergies, String Medications, String VetName, String VetContact) {
        if (Allergies == null || Medications == null || VetName == null || VetContact == null) return 0;
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_ID, Id); // Use Bluetooth address as ID
        values.put(Constants.COLUMN_ALLERGIES, Allergies);
        values.put(Constants.COLUMN_MEDICATIONS, Medications);
        values.put(Constants.COLUMN_VETNAME, VetName);
        values.put(Constants.COLUMN_VETCONTACT, VetContact);

        long returnValue =
                db.update(Constants.TABLE_NAME3, values, Constants.COLUMN_ID +" = ?", new String[] {String.valueOf(Id)});

        if (returnValue==0){
            //if update affected no lines, it means pet has no health information stored yet.
            returnValue = db.insertWithOnConflict(Constants.TABLE_NAME3, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        }

        db.close();
        return returnValue;
    }

    public Cursor getAllPets () {
        //USED FOR CONTENT PROVIDER.
        String table1 = Constants.TABLE_NAME;
        String table2 = Constants.TABLE_NAME3;
        String ID = Constants.COLUMN_ID;

        String selectQuery = "SELECT * FROM " + table1 + " LEFT JOIN " + table2 +
                " ON " + table1 + "." + ID + " = " + table2 + "." + ID;

        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery(selectQuery, null);
    }
    public ArrayList<RecordModel> getAllRecords (String orderby) {
        ArrayList<RecordModel> recordsList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + Constants.TABLE_NAME + " ORDER BY " + orderby;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                 RecordModel recordModel = new RecordModel(
                        ""+cursor.getInt(cursor.getColumnIndex(Constants.COLUMN_ID)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.COLUMN_PETNAME)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.COLUMN_BREED)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.COLUMN_SEX)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.COLUMN_BIRTHDATE)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.COLUMN_AGE)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.COLUMN_WEIGHT)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.COLUMN_IMAGE)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.COLUMN_ADDED_TIMESTAMP)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.COLUMN_UPDATED_TIMESTAMP)));

                recordsList.add(recordModel);
            } while (cursor.moveToNext());
        }
        db.close();
        return recordsList;
    }
    public ArrayList<RecordModel> searchRecords (String query) {
        ArrayList<RecordModel> recordsList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + Constants.TABLE_NAME + " WHERE " + Constants.COLUMN_PETNAME + " LIKE '%" + query +"%'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                 RecordModel recordModel = new RecordModel(
                        ""+cursor.getInt(cursor.getColumnIndex(Constants.COLUMN_ID)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.COLUMN_PETNAME)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.COLUMN_BREED)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.COLUMN_SEX)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.COLUMN_BIRTHDATE)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.COLUMN_AGE)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.COLUMN_WEIGHT)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.COLUMN_IMAGE)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.COLUMN_ADDED_TIMESTAMP)),
                        ""+cursor.getString(cursor.getColumnIndex(Constants.COLUMN_UPDATED_TIMESTAMP)));
                recordsList.add(recordModel);
            } while (cursor.moveToNext());
        }
        db.close();
        return recordsList;
    }

    public void deleteData(String id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(Constants.TABLE_NAME, Constants.COLUMN_ID + " = ?", new String[]{id});
        db.close();
    }
    public void deleteAllData(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + Constants.TABLE_NAME);
        db.close();
    }
    public int deleteData(String whereClause, String[] whereValues) {
        //USED FOR CONTENT PROVIDER.
        SQLiteDatabase db = getWritableDatabase();
        int feedback = db.delete(Constants.TABLE_NAME, whereClause, whereValues);
        db.close();
        return feedback;
    }
}
