package com.example.petfeeder.DataSharing;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.petfeeder.Database.Constants;
import com.example.petfeeder.Database.DatabaseHelper;

public class PetFeederContentProvider extends ContentProvider {
    public static final String AUTHORITY = "com.example.petfeeder";
    public static final Uri CONTENT_URI_PETS = Uri.parse("content://"+AUTHORITY+"/"+ Constants.TABLE_NAME);
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int PETS = 1;

    static {
        // Add URIs for pets table
        URI_MATCHER.addURI(AUTHORITY, Constants.TABLE_NAME, PETS);
    }

    public static final String CONTENT_TYPE_PETS = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + Constants.TABLE_NAME;

    DatabaseHelper databaseHelper;

    @Override
    public boolean onCreate() {
        databaseHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor = null;
        switch (URI_MATCHER.match(uri)){
            case PETS:
                if (selection==null) {
                    cursor = databaseHelper.getAllPets();
                } else {
                    cursor = databaseHelper.query(uri, projection, selection, selectionArgs, sortOrder, getContext());
                }
                break;
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (URI_MATCHER.match(uri)){
            case PETS: return CONTENT_TYPE_PETS;
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) throws UnsupportedOperationException {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        switch (URI_MATCHER.match(uri)){
            case PETS: return deletePets(uri, s, strings);
        }
        return -1;
    }
    private int deletePets(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs){
        int returnValue = databaseHelper.deleteData(selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return returnValue;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        switch (URI_MATCHER.match(uri)){
            case PETS: return updatePets(uri, contentValues);
        }
        return -1;
    }
    private int updatePets(@NonNull Uri uri, @Nullable ContentValues values){
        assert values != null;
        int returnValue = databaseHelper.updateData(
                values.getAsInteger(Constants.COLUMN_ID),
                values.getAsString(Constants.COLUMN_PETNAME),
                values.getAsString(Constants.COLUMN_BREED),
                values.getAsString(Constants.COLUMN_SEX),
                values.getAsString(Constants.COLUMN_BIRTHDATE),
                values.getAsString(Constants.COLUMN_AGE),
                values.getAsString(Constants.COLUMN_WEIGHT),
                values.getAsString(Constants.COLUMN_IMAGE),
                values.getAsString(Constants.COLUMN_UPDATED_TIMESTAMP),
                values.getAsString(Constants.COLUMN_PET_FINDER_ID),
                values.getAsString(Constants.COLUMN_ALLERGIES),
                values.getAsString(Constants.COLUMN_MEDICATIONS),
                values.getAsString(Constants.COLUMN_VETNAME),
                values.getAsString(Constants.COLUMN_VETCONTACT));
        getContext().getContentResolver().notifyChange(uri, null);
        return returnValue;
    }
}
