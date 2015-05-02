package com.walsvick.christopher.timecodenotes.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created on 1/10/2015 by Christopher.
 */
public class NoteTable {

    public static String TABLE_NAME = "note_table";

    public static String NOTE_ID = "_id";
    public static String NOTE_PROJECT_ID = "projectId";
    public static String NOTE_TIME_CODE = "timeCode";
    public static String NOTE_CAMERA_NAME = "cameraName";
    public static String NOTE_TEXT = "noteText";

    public static final String[] ALL_COLUMNS = {
            NoteTable.NOTE_ID,
            NoteTable.NOTE_PROJECT_ID,
            NoteTable.NOTE_TIME_CODE,
            NoteTable.NOTE_CAMERA_NAME,
            NoteTable.NOTE_TEXT };

    /** SQLite database creation statement. Auto-increments IDs of inserted notes.
     * Note IDs are set after insertion into the database. */
    public static final String TABLE_CREATE = "create table " + TABLE_NAME + " (" +
            NOTE_ID 			        + " integer primary key autoincrement, " +
            NOTE_PROJECT_ID		        + " integer not null, " +
            NOTE_TIME_CODE              + " text not null, " +
            NOTE_CAMERA_NAME            + " text not null, " +
            NOTE_TEXT                   + " text, " +
            "FOREIGN KEY(" + NOTE_PROJECT_ID + ") REFERENCES " + ProjectTable.TABLE_NAME + "(" + ProjectTable.PROJECT_ID +"));";

    /** SQLite database table removal statement. Only used if upgrading database. */
    public static final String TABLE_DROP = "drop table if exists " + TABLE_NAME;

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database) {
        database.execSQL(TABLE_DROP);
        onCreate(database);
    }
}
