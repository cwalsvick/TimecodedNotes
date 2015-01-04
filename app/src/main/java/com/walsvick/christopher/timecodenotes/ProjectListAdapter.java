package com.walsvick.christopher.timecodenotes;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.walsvick.christopher.timecodenotes.com.walsvick.christopher.timecodenodes.model.Project;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christopher on 1/2/2015.
 */
public class ProjectListAdapter extends BaseAdapter {

    private Context context;
    private List<Project> projectList;

    public ProjectListAdapter(Context context, ArrayList<Project> list) {
        this.context = context;
        this.projectList = list;
    }


    @Override
    public int getCount() {
        return projectList.size();
    }

    @Override
    public Object getItem(int position) {
        return projectList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        Project project = (Project) getItem(position);

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.list_view_item_project, parent, false);
         }

        row.setTag(project);
        final TextView projectName = (TextView) row.findViewById(R.id.list_view_item_project_name);
        final TextView projectStartDate = (TextView) row.findViewById(R.id.list_view_item_project_date);
        final TextView projectCameras = (TextView) row.findViewById(R.id.list_view_item_project_camera_list);

        projectName.setText(project.getName());
        projectStartDate.setText(project.getStartDate().toString());
        projectCameras.setText(StringUtils.join(project.getCameraListNames(), ", "));

        return row;
    }
}
