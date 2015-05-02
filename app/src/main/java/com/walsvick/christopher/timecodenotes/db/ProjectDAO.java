package com.walsvick.christopher.timecodenotes.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.walsvick.christopher.timecodenotes.model.Project;

import org.joda.time.LocalDate;

import java.util.ArrayList;

/**
 * Created on 1/10/2015 by Christopher.
 */
public class ProjectDAO {

    private Context context;

    public ProjectDAO(Context context) {
        this.context = context;
    }

    public ArrayList<Project> getAllProjects() {
        ArrayList<Project> projects = new ArrayList<Project>();

        String[] projection = ProjectTable.ALL_COLUMNS;
        Uri uri = Uri.parse(DBContentProvider.PROJECT_CONTENT_URI + "/project");
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            projects.add(cursorToProject(cursor));
            cursor.moveToNext();
        }


        return projects;
    }

    private Project cursorToProject(Cursor cursor) {
        Project p = new Project();

        p.setId(cursor.getInt(ProjectTable.PROJECT_ID_COL));
        p.setName(cursor.getString(ProjectTable.PROJECT_NAME_COL));
        p.setStartDate(new LocalDate(cursor.getString(ProjectTable.PROJECT_START_DATE_COL)));
        p.setAddInfo(cursor.getString(ProjectTable.PROJECT_ADDITIONAL_INFO_COL));

        return p;
    }

    public void saveAllProjects(ArrayList<Project> projectList) {
    }

    public void saveProject(Project p) {
        ContentValues values = new ContentValues();
        values.put(ProjectTable.PROJECT_NAME, p.getName());
        values.put(ProjectTable.PROJECT_START_DATE, p.getStartDate().toString("yyyy-MM-dd"));
        values.put(ProjectTable.PROJECT_ADDITIONAL_INFO, p.getAddInfo());

        Uri projectUri = context.getContentResolver().insert(DBContentProvider.PROJECT_CONTENT_URI, values);
        p.setId(Integer.valueOf(projectUri.getLastPathSegment()));
    }
}
