package com.walsvick.christopher.timecodenotes.view;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.ResourceCursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.walsvick.christopher.timecodenotes.R;
import com.walsvick.christopher.timecodenotes.db.ProjectDAO;
import com.walsvick.christopher.timecodenotes.db.ProjectTable;
import com.walsvick.christopher.timecodenotes.model.Project;

import org.apache.commons.lang3.StringUtils;

/**
 * Created on 1/10/2015 by Christopher.
 */
public class ProjectListCursorAdapter extends CursorAdapter {
    public ProjectListCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ProjectDAO dao = new ProjectDAO(context);
        Project project = dao.cursorToProject(cursor);

        ProjectListItemView view = new ProjectListItemView(context, project);
        return view;
    }

    @Override
    public void bindView(View oldView, Context context, Cursor cursor) {
        ProjectDAO dao = new ProjectDAO(context);
        Project project = dao.cursorToProject(cursor);

        ProjectListItemView view = (ProjectListItemView) oldView;
        view.setProject(project);
    }
}
