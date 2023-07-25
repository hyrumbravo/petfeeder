package com.example.petfeeder.Database;

public class Constants {

    public static final String DATABASE_NAME = "PetDatabase.db";
    public static final int DATABASE_VERSION = 2;

    public static final String TABLE_NAME = "PetRecord";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PETNAME = "petName";
    public static final String COLUMN_BREED = "breed";
    public static final String COLUMN_SEX = "sex";
    public static final String COLUMN_BIRTHDATE = "birthdate";
    public static final String COLUMN_AGE = "age";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_IMAGE = "petPic";
    public static final String COLUMN_ADDED_TIMESTAMP = "added_timestamp";
    public static final String COLUMN_UPDATED_TIMESTAMP = "updated_timestamp";

    public static final String COLUMN_PET_FEEDER_ID = "petFeederId";
    public static final String COLUMN_PET_FINDER_ID = "petFinderId";

    public static String query = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_PETNAME + " TEXT, "
            + COLUMN_BREED + " TEXT, "
            + COLUMN_SEX + " TEXT, "
            + COLUMN_BIRTHDATE + " TEXT, "
            + COLUMN_AGE + " TEXT, "
            + COLUMN_WEIGHT + " TEXT, "
            + COLUMN_IMAGE + " TEXT, "
            + COLUMN_ADDED_TIMESTAMP + " TEXT, "
            + COLUMN_UPDATED_TIMESTAMP + " TEXT, "
            + COLUMN_PET_FINDER_ID + " TEXT);"; //ADDED COLUMN_PET_FINDER_ID

    //PEDOMETER TABLE

    public static final String TABLE_NAME2 = "Pedometer";
    public static final String COLUMN_ID2 = "_id2";
    public static final String COLUMN_NUMSTEPS = "numSteps";
    public static final String COLUMN_DATE = "date";

    public static String query2 = "CREATE TABLE " + TABLE_NAME2 + "("
            + COLUMN_ID2 + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NUMSTEPS + " INTEGER, "
            + COLUMN_DATE + " TEXT, "
            + COLUMN_ID + " TEXT, "
            + "FOREIGN KEY(" + COLUMN_ID + ") REFERENCES " + TABLE_NAME + "(" + COLUMN_ID + "));";

    //MORE PET INFO
    public static final String TABLE_NAME3 = "PetMoreInfo";
    public static final String COLUMN_ID3 = "_id3";
    public static final String COLUMN_ALLERGIES = "allergies";
    public static final String COLUMN_MEDICATIONS = "medications";
    public static final String COLUMN_VETNAME = "vetName";
    public static final String COLUMN_VETCONTACT = "vetContact";

    public static String query3 = "CREATE TABLE " + TABLE_NAME3 + "("
            + COLUMN_ID3 + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_ALLERGIES + " TEXT, "
            + COLUMN_MEDICATIONS + " TEXT, "
            + COLUMN_VETNAME + " TEXT, "
            + COLUMN_VETCONTACT + " TEXT, "
            + COLUMN_ID + " TEXT, "
            + "FOREIGN KEY(" + COLUMN_ID + ") REFERENCES " + TABLE_NAME + "(" + COLUMN_ID + "));";

}
