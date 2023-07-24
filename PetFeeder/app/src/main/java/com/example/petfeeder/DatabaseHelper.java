package com.example.petfeeder;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String AUTHORITY = "com.example.petfeeder";
    public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/"+Constants.TABLE_NAME);

    private static final int PETS = 1;

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // Add URIs for pets table
        URI_MATCHER.addURI(AUTHORITY, Constants.TABLE_NAME, PETS);
    }

    public Context context;
    public DatabaseHelper(@Nullable Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    private Context getContext() {
        return context;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Implement the query operation for the Content Provider
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor;

        int match = URI_MATCHER.match(uri);
        switch (match) {
            case PETS:
                // Query the pets table
                cursor = db.query(Constants.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        // Set the notification URI on the cursor
        Context context = getContext();
        if (context != null) {
            cursor.setNotificationUri(context.getContentResolver(), uri);
        }

        return cursor;
    }

    public Uri insert(Uri uri, ContentValues values) {
        // Implement the insert operation for the Content Provider
        SQLiteDatabase db = getWritableDatabase();

        long id;
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case PETS:
                // Insert into pets table
                id = db.insert(Constants.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        // Notify the Content Resolver that the data has changed
        if (id != -1) {
            Context context = getContext();
            if (context != null) {
                context.getContentResolver().notifyChange(uri, null);
            }
        }

        return ContentUris.withAppendedId(uri, id);
    }

    //END

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

    public int updateData(Integer id, String petName, String breed, String sex, String bdate, String age,
                          String weight, String petPic, String addedtime, String updatedtime, String petFinderID) {
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
        values.put(Constants.COLUMN_PET_FINDER_ID, petFinderID);

        int returnValue = db.update(Constants.TABLE_NAME, values, Constants.COLUMN_ID +" = ?", new String[] {String.valueOf(id)});
        db.close();
        return returnValue;
    }

    @SuppressLint("Range")
    public PetModel getRecordDetails(String recordID) {
        String selectQuery = "SELECT * FROM " + Constants.TABLE_NAME + " WHERE " + Constants.COLUMN_ID + "=\"" + recordID + "\"";
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
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
        db.close();

        return petModel;
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

    @SuppressLint("Range")
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

    @SuppressLint("Range")
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

    public long storePedometerData(int ID, int numSteps, String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_ID, ID);
        values.put(Constants.COLUMN_NUMSTEPS, numSteps);
        values.put(Constants.COLUMN_DATE, date);

        long id = db.insert(Constants.TABLE_NAME2, null, values);  // Corrected table name usage
        db.close();
        return id;
    }

    public int updatePedometerData(String id, int numstep, String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.COLUMN_ID, id);
        values.put(Constants.COLUMN_NUMSTEPS, numstep);
        values.put(Constants.COLUMN_DATE, date);

        int returnValue = db.update(Constants.TABLE_NAME2, values, Constants.COLUMN_DATE +" = ? AND "+ Constants.COLUMN_ID +"= ?", new String[] {date, id});
        if (returnValue==0){ //insert if rows affected are 0 (pet's record is still not in database).
            returnValue = (int) db.insert(Constants.TABLE_NAME2, null, values);
        }
        db.close();
        return returnValue;
    }

    @SuppressLint("Range")
    public String getLatestDateOfPedometerData(){
        String query = "SELECT "+ Constants.COLUMN_DATE +" FROM " + Constants.TABLE_NAME2 +
                        " ORDER BY " + Constants.COLUMN_DATE + " DESC LIMIT 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        String returnValue = null;
        if (cursor != null && cursor.getCount()>0) {
            cursor.moveToFirst();
            returnValue = cursor.getString(cursor.getColumnIndex(Constants.COLUMN_DATE));
        }
        cursor.close();

        return returnValue;
    }
    @SuppressLint("Range")
    public Integer getIdFromPetFinderID(String petFinderId) {
        String query = "SELECT " + Constants.COLUMN_ID + " FROM " + Constants.TABLE_NAME + " WHERE " + Constants.COLUMN_PET_FINDER_ID + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{petFinderId});

        Integer returnValue = null;
        if (cursor.moveToFirst() && cursor.getColumnIndex(Constants.COLUMN_ID) != -1) {
            returnValue = cursor.getInt(cursor.getColumnIndex(Constants.COLUMN_ID));
        }
        cursor.close();
        return returnValue;
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

    public int deletePedometer(String selection, String[] selectionArgs) {
        //USED FOR CONTENT PROVIDER.
        SQLiteDatabase db = getWritableDatabase();
        int feedback = db.delete(Constants.TABLE_NAME2, selection, selectionArgs);
        db.close();
        return feedback;
    }

    public int getRecordsCount() {
        String countQuery = "SELECT * FROM " + Constants.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }
}
