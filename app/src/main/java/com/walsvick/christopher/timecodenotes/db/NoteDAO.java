package com.walsvick.christopher.timecodenotes.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.walsvick.christopher.timecodenotes.model.Note;
import com.walsvick.christopher.timecodenotes.model.Project;

import java.util.ArrayList;

/**
 * Created on 1/10/2015 by Christopher.
 */
public class NoteDAO {

    private Context context;

    public NoteDAO(Context context) {
        this.context = context;
    }

    public int saveNote(Project p, Note n) {
        ContentValues values = new ContentValues();
        values.put(NoteTable.NOTE_PROJECT_ID, p.getId());
        values.put(NoteTable.NOTE_TIME_CODE, n.getLocalDateTimeCode().toString());
        values.put(NoteTable.NOTE_CAMERA_NAME, n.getCamera());
        values.put(NoteTable.NOTE_TEXT, n.getNote());

        Uri noteUri = context.getContentResolver().insert(DBContentProvider.NOTE_CONTENT_URI, values);
        return Integer.valueOf(noteUri.getLastPathSegment());
    }

    public void updateNote(Project p, Note n) {
        ContentValues values = new ContentValues();
        values.put(NoteTable.NOTE_PROJECT_ID, p.getId());
        values.put(NoteTable.NOTE_TIME_CODE, n.getLocalDateTimeCode().toString());
        values.put(NoteTable.NOTE_CAMERA_NAME, n.getCamera());
        values.put(NoteTable.NOTE_TEXT, n.getNote());

        Uri uri = Uri.parse(DBContentProvider.NOTE_CONTENT_URI + "/" + n.getId());
        context.getContentResolver().update(uri, values, null, null);
    }

    public ArrayList<Note> getAllNotes(Project p) {
        ArrayList<Note> notes = new ArrayList<>();

        String[] projection = NoteTable.ALL_COLUMNS;
        Uri uri = Uri.parse(DBContentProvider.NOTE_CONTENT_URI + "/project/" + p.getId());
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            notes.add(cursorToNote(cursor));
            cursor.moveToNext();
        }


        return notes;
    }

    public Note cursorToNote(Cursor cursor) {
        Note n = new Note();

        n.setId(cursor.getInt(cursor.getColumnIndex(NoteTable.NOTE_ID)));
        n.setProjectId(cursor.getInt(cursor.getColumnIndex(NoteTable.NOTE_PROJECT_ID)));
        n.setTimeCode(cursor.getString(cursor.getColumnIndex(NoteTable.NOTE_TIME_CODE)));
        n.setCamera(cursor.getString(cursor.getColumnIndex(NoteTable.NOTE_CAMERA_NAME)));
        n.setNote(cursor.getString(cursor.getColumnIndex(NoteTable.NOTE_TEXT)));

        return n;
    }
}
