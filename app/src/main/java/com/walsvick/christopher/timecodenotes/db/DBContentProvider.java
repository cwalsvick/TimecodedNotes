package com.walsvick.christopher.timecodenotes.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created on 1/10/2015 by Christopher.
 */
public class DBContentProvider extends ContentProvider {
    /**
     * The database.
     */
    private DatabaseHelper database;

    /**
     * sURIMatcher values
     */
    private static final int PROJECT = 10;
    private static final int PROJECT_BY_ID = 11;
    private static final int NOTE = 20;
    private static final int NOTES_BY_PROJECT_ID = 21;
    private static final int NOTE_BY_ID = 22;


    /**
     * The authority for this content provider.
     */
    private static final String AUTHORITY = "com.walsvick.christopher.timecodenotes.db";

    private static final String PROJECT_TABLE = ProjectTable.TABLE_NAME;
    private static final String NOTE_TABLE = NoteTable.TABLE_NAME;

    /**
     * This provider's content location. Used by accessing applications to
     * interact with this provider.
     */
    public static final Uri PROJECT_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PROJECT_TABLE);
    public static final Uri NOTE_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + NOTE_TABLE);

    /**
     * Matches content URIs requested by accessing applications with possible
     * expected content URI formats to take specific actions in this provider.
     */
    private static final UriMatcher sURIMatcher;

    static {
        sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sURIMatcher.addURI(AUTHORITY, PROJECT_TABLE, PROJECT);
        sURIMatcher.addURI(AUTHORITY, PROJECT_TABLE + "/#", PROJECT_BY_ID);
        sURIMatcher.addURI(AUTHORITY, NOTE_TABLE, NOTE);
        sURIMatcher.addURI(AUTHORITY, NOTE_TABLE + "/project/#", NOTES_BY_PROJECT_ID);
        sURIMatcher.addURI(AUTHORITY, NOTE_TABLE + "/#", NOTE_BY_ID);
      /*  sURIMatcher.addURI(AUTHORITY, DOCUMENT_TABLE + "/document/vaultId/#", DOCUMENTS);
        sURIMatcher.addURI(AUTHORITY, DOCUMENT_TABLE + "/document/#", DOCUMENT);
        sURIMatcher.addURI(AUTHORITY, PROPERTY_TABLE + "/property", PROPERTIES);
        sURIMatcher.addURI(AUTHORITY, PROPERTY_TABLE + "/property/#", PROPERTY);
        sURIMatcher.addURI(AUTHORITY, DOCUMENT_X_PROPERTY_TABLE + "/propertyValue", DOCUMENT_X_PROPERTY);
        sURIMatcher.addURI(AUTHORITY, DOCUMENT_X_PROPERTY_TABLE + "/document/#", DXP_DOCUMENT_ID);
        sURIMatcher.addURI(AUTHORITY, DOCUMENT_X_PROPERTY_TABLE + "/document/#/property/#", DXP_VALUE_UPDATE);
        sURIMatcher.addURI(AUTHORITY, VAULT_ENDPOINT_TABLE + "/vaultEndpoint/#", VAULT_ENDPOINT);
        sURIMatcher.addURI(AUTHORITY, USER_TABLE + "/user/#", USER);
        sURIMatcher.addURI(AUTHORITY, USER_TABLE + "/user", USER_BY_NAME);
        sURIMatcher.addURI(AUTHORITY, USER_X_ENDPOINT_TABLE + "/user/#/endpoint/#", USER_X_ENDPOINT);
        sURIMatcher.addURI(AUTHORITY, USER_X_ENDPOINT_TABLE + "/user/#", ENDPOINTS);*/
    }

    @Override
    public boolean onCreate() {
        database = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /** Open the database for writing. */
        SQLiteDatabase sqlDB = this.database.getWritableDatabase();

        long id;

        /** Match the passed-in URI to an expected URI format. */
        int uriType = sURIMatcher.match(uri);
        switch(uriType)	{
            case PROJECT:
                id = sqlDB.insert(PROJECT_TABLE, null, values);
                break;
            case NOTE:
                id = sqlDB.insert(NOTE_TABLE, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        /** Alert any watchers of an underlying data change for content/view refreshing. */
        getContext().getContentResolver().notifyChange(uri, null);

        return Uri.parse("insertResultId" + "/" + id);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Uisng SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // check if the caller has requested a column which does not exists
      //  checkColumns(projection);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case PROJECT:
                queryBuilder.setTables(ProjectTable.TABLE_NAME);
                break;
            case NOTES_BY_PROJECT_ID:
                queryBuilder.setTables(NoteTable.TABLE_NAME);
                queryBuilder.appendWhere(NoteTable.NOTE_PROJECT_ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase sqlDB = this.database.getWritableDatabase();

        String where;

        /** Match the passed-in URI to an expected URI format. */
        int uriType = sURIMatcher.match(uri);
        switch(uriType)	{
            case PROJECT_BY_ID:
                where = ProjectTable.PROJECT_ID + "=" + Integer.parseInt(uri.getLastPathSegment());
                sqlDB.delete(PROJECT_TABLE, where, null);
                break;
            case NOTES_BY_PROJECT_ID:
                where = NoteTable.NOTE_PROJECT_ID + "=" + Integer.parseInt(uri.getLastPathSegment());
                sqlDB.delete(NOTE_TABLE, where, null);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        /** Alert any watchers of an underlying data change for content/view refreshing. */
        getContext().getContentResolver().notifyChange(uri, null);

        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase sqlDB = this.database.getWritableDatabase();

        String where;

        /** Match the passed-in URI to an expected URI format. */
        int uriType = sURIMatcher.match(uri);
        switch(uriType)	{
            case NOTE_BY_ID:
                where = NoteTable.NOTE_ID + "=" + Integer.parseInt(uri.getLastPathSegment());
                sqlDB.update(NOTE_TABLE, values, where, null);
                break;
            case PROJECT_BY_ID:
                where = ProjectTable.PROJECT_ID + "=" + Integer.parseInt(uri.getLastPathSegment());
                sqlDB.update(PROJECT_TABLE, values, where, null);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        /** Alert any watchers of an underlying data change for content/view refreshing. */
        getContext().getContentResolver().notifyChange(uri, null);

        return 0;
    }
}

