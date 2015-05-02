package com.walsvick.christopher.timecodenotes.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created on 1/10/2015 by Christopher.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    /** The name of the database. */
    public static final String DATABASE_NAME = "timeCodeNotes.db";

    /** The starting database version. */
    public static final int DATABASE_VERSION = 1;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ProjectTable.onCreate(db);
        NoteTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ProjectTable.onUpgrade(db);
        NoteTable.onUpgrade(db);
    }
}
