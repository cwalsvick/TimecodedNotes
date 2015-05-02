package com.walsvick.christopher.timecodenotes.view;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.ResourceCursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.walsvick.christopher.timecodenotes.R;
import com.walsvick.christopher.timecodenotes.db.ProjectTable;

import org.apache.commons.lang3.StringUtils;

/**
 * Created on 1/10/2015 by Christopher.
 */
public class ProjectListCursorAdapter extends ResourceCursorAdapter {

    public ProjectListCursorAdapter(Context context, int layout, Cursor c, int flags) {
        super(context, layout, c, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView projectName = (TextView) view.findViewById(R.id.list_view_item_project_name);
        TextView projectStartDate = (TextView) view.findViewById(R.id.list_view_item_project_date);
        TextView projectCameras = (TextView) view.findViewById(R.id.list_view_item_project_camera_list);

        projectName.setText(cursor.getString(ProjectTable.PROJECT_NAME_COL));
        projectStartDate.setText(cursor.getString(ProjectTable.PROJECT_START_DATE_COL));
        //projectCameras.setText(StringUtils.join(project.getCameraListNames(), ", "));
    }
}
