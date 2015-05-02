package com.walsvick.christopher.timecodenotes.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created on 1/10/2015 by Christopher.
 */
public class ProjectTable {

    public static String TABLE_NAME = "project_table";

    public static String PROJECT_ID = "_id";
    public static String PROJECT_NAME = "name";
    public static String PROJECT_START_DATE = "startDate";
    public static String PROJECT_CAMERA_NAMES = "cameraNames";
    public static String PROJECT_ADDITIONAL_INFO = "additionalInfo";

    public static final String[] ALL_COLUMNS = {
            ProjectTable.PROJECT_ID,
            ProjectTable.PROJECT_NAME,
            ProjectTable.PROJECT_START_DATE,
            ProjectTable.PROJECT_CAMERA_NAMES,
            ProjectTable.PROJECT_ADDITIONAL_INFO };

    /** SQLite database creation statement. Auto-increments IDs of inserted projects.
     * Project IDs are set after insertion into the database. */
    public static final String TABLE_CREATE = "create table " + TABLE_NAME + " (" +
            PROJECT_ID 			        + " integer primary key autoincrement, " +
            PROJECT_NAME		        + " text not null," +
            PROJECT_START_DATE		    + " text not null," +
            PROJECT_CAMERA_NAMES		+ " text not null," +
            PROJECT_ADDITIONAL_INFO		+ " text default null);";

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
