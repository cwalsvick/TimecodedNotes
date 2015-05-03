package com.walsvick.christopher.timecodenotes.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.walsvick.christopher.timecodenotes.model.Project;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created on 1/10/2015 by Christopher.
 */
public class ProjectDAO {

    private Context context;
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public ProjectDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
        this.context = context;
    }

    public static Project cursorToProject(Cursor cursor) {
        Project p = new Project();

        p.setId(cursor.getInt(cursor.getColumnIndex(ProjectTable.PROJECT_ID)));
        p.setName(cursor.getString(cursor.getColumnIndex(ProjectTable.PROJECT_NAME)));
        p.setStartDate(LocalDate.parse(cursor.getString(cursor.getColumnIndex(ProjectTable.PROJECT_START_DATE))));
        p.setCameras(Arrays.asList(StringUtils.split(cursor.getString(cursor.getColumnIndex(ProjectTable.PROJECT_CAMERA_NAMES)), ';')));
        p.setAddInfo(cursor.getString(cursor.getColumnIndex(ProjectTable.PROJECT_ADDITIONAL_INFO)));

        return p;
    }

    public int saveProject(Project p) {
        ContentValues values = new ContentValues();
        values.put(ProjectTable.PROJECT_NAME, p.getName());
        values.put(ProjectTable.PROJECT_START_DATE, p.getStartDate().toString("yyyy-MM-dd"));
        values.put(ProjectTable.PROJECT_CAMERA_NAMES, StringUtils.join(p.getCameras(), ';'));
        values.put(ProjectTable.PROJECT_ADDITIONAL_INFO, p.getAddInfo());

        Uri noteUri = context.getContentResolver().insert(DBContentProvider.PROJECT_CONTENT_URI, values);
        return Integer.valueOf(noteUri.getLastPathSegment());

    }

    public void updateProject(Project p) {
        ContentValues values = new ContentValues();
        values.put(ProjectTable.PROJECT_NAME, p.getName());
        values.put(ProjectTable.PROJECT_START_DATE, p.getStartDate().toString("yyyy-MM-dd"));
        values.put(ProjectTable.PROJECT_CAMERA_NAMES, StringUtils.join(p.getCameras(), ';'));
        values.put(ProjectTable.PROJECT_ADDITIONAL_INFO, p.getAddInfo());

        Uri uri = Uri.parse(DBContentProvider.PROJECT_CONTENT_URI + "/" + p.getId());
        context.getContentResolver().update(uri, values, null, null);
    }

    public void deleteProject(Project p) {
        Uri uri = Uri.parse(DBContentProvider.PROJECT_CONTENT_URI + "/" + p.getId());
        context.getContentResolver().delete(uri, null, null);

        uri = Uri.parse(DBContentProvider.NOTE_CONTENT_URI + "/project/" + p.getId());
        context.getContentResolver().delete(uri, null, null);
    }
}
