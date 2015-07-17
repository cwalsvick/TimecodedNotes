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
public class ProjectListItemView {

    private View view;

    private TextView projectName;
    private TextView projectStartDate;
    private TextView projectCameras;
    private TextView projectInfo;

    public ProjectListItemView(View v) {
        this.view = v;
        this.projectName = (TextView) v.findViewById(R.id.list_view_item_project_name);
        this.projectStartDate = (TextView) v.findViewById(R.id.list_view_item_project_date);
        this.projectCameras = (TextView) v.findViewById(R.id.list_view_item_project_camera_list);
        this.projectInfo = (TextView) v.findViewById(R.id.list_view_item_project_add_info);
    }

    public View getView() {
        return this.view;
    }

    public void setProject(Project project) {
        this.projectName.setText(project.getName());
        this.projectStartDate.setText(project.getStartDate().toString());
        this.projectCameras.setText(StringUtils.join(project.getCameras(), ", "));
        this.projectInfo.setText(project.getAddInfo());
    }


}
