package com.walsvick.christopher.timecodenotes.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.walsvick.christopher.timecodenotes.R;
import com.walsvick.christopher.timecodenotes.model.Project;

import org.apache.commons.lang3.StringUtils;

/**
 * Created on 1/18/2015 by Christopher.
 */
public class ProjectListItemView extends LinearLayout {

    private Project project;

    private TextView projectName;
    private TextView projectStartDate;
    private TextView projectCameras;

    public ProjectListItemView(Context context, Project project) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.list_view_item_project, this, true);

        this.projectName = (TextView) v.findViewById(R.id.list_view_item_project_name);
        this.projectStartDate = (TextView) v.findViewById(R.id.list_view_item_project_date);
        this.projectCameras = (TextView) v.findViewById(R.id.list_view_item_project_camera_list);

        setProject(project);
    }

    public void setProject(Project project) {
        this.project = project;

        projectName.setText(project.getName());
        projectStartDate.setText(project.getStartDate().toString());
        projectCameras.setText(StringUtils.join(project.getCameras(), ", "));
    }


}
