package com.walsvick.christopher.timecodenotes.activity;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.walsvick.christopher.timecodenotes.R;
import com.walsvick.christopher.timecodenotes.db.DBContentProvider;
import com.walsvick.christopher.timecodenotes.db.ProjectDAO;
import com.walsvick.christopher.timecodenotes.db.ProjectTable;
import com.walsvick.christopher.timecodenotes.io.StorageUtil;
import com.walsvick.christopher.timecodenotes.model.Project;
import com.walsvick.christopher.timecodenotes.view.FloatingActionButton;
import com.walsvick.christopher.timecodenotes.view.NewProjectDialog;
import com.walsvick.christopher.timecodenotes.view.RecyclerItemClickListener;
import com.walsvick.christopher.timecodenotes.view.ProjectRecyclerViewCursorAdapter;

import org.apache.commons.lang3.StringUtils;

public class MainActivity extends ActionBarActivity implements
        NewProjectDialog.NewProjectDialogListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private ProjectDAO projectDAO;

    private RecyclerView projectListView;
    private ProjectRecyclerViewCursorAdapter projectListAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private FloatingActionButton newProjectButton;

    public final static String SELECTED_PROJECT = "COM.WALSVICK.CHRISTOPHER.TIMECODENOTES.SELECTED_PROJECT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getResources().getString(R.string.project_list));
        setContentView(R.layout.activity_main);
        projectDAO = new ProjectDAO(this);

        newProjectButton = (FloatingActionButton) findViewById(R.id.new_project_fab);
        newProjectButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                NewProjectDialog dialog = new NewProjectDialog(MainActivity.this, MainActivity.this);
                dialog.begin();
            }
        });

        projectListView = (RecyclerView) findViewById(R.id.project_recycler_view);
        projectListView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        projectListView.setLayoutManager(layoutManager);

        projectListAdapter = new ProjectRecyclerViewCursorAdapter(this, null);
        projectListView.setAdapter(projectListAdapter);
        projectListView.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new OnItemClickListener()));

        registerForContextMenu(projectListView);
        fillData();
    }

    private class OnItemClickListener extends RecyclerItemClickListener.SimpleOnItemClickListener {

        @Override
        public void onItemClick(View childView, int position) {
            Intent i = new Intent(MainActivity.this, TakeNotesActivity.class);
            i.putExtra(SELECTED_PROJECT, (Project) projectListAdapter.getItem(position));
            startActivity(i);
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Project project = (Project) projectListAdapter.getItem(projectListAdapter.getPosition());

        switch (item.getItemId()) {
            case R.id.menu_item_view_project_info:
                showAdditionalInfoDialog(project);
                return true;
            case R.id.menu_item_export_project:
                exportProject(project);
                return true;
            case R.id.menu_item_email_project:
                emailProject(project);
                return true;
            case R.id.menu_item_edit_project:
                editProject(project);
                return true;
            case R.id.menu_item_clone_project:
                cloneProject(project);
                return true;
            case R.id.menu_item_delete_project:
                deleteProject(project);
                return true;
        }
        return false;
    }

    private void emailProject(Project project) {
        Resources res = getResources();
        String[] emailBody = new String[] {res.getString(R.string.email_body_prefix),
            res.getString(R.string.email_body_name_label) + project.getName(),
            res.getString(R.string.email_body_date_label) + project.getStartDate(),
            res.getString(R.string.email_body_cameras_label) + StringUtils.join(project.getCameras(), ", "),
            res.getString(R.string.email_body_additional_info_label) + project.getAddInfo()};

        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("*/*");
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                res.getString(R.string.email_subject_prefix, res.getString(R.string.app_name)) + project.getName());
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, StringUtils.join(emailBody, "\n"));

        String fileName = StorageUtil.exportToTSV(this, project);
        Log.d(getClass().getSimpleName(), "fileUri=" + Uri.parse("file://" + fileName));
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + fileName));
        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }

    private void showAdditionalInfoDialog(Project p) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final TextView textView = new TextView(this);
        textView.setPadding(16, 16, 16, 16);
        textView.setTextSize(16);
        textView.setText(p.getAddInfo());
        builder.setView(textView);

        builder.setTitle(p.getName() + " Information");
        builder.setNeutralButton("Close",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void exportProject(Project p) {
        StorageUtil.exportToTSV(this, p);
    }

    private void deleteProject(final Project p) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Project");
        builder.setMessage("Are you sure you want to delete this project?");
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                projectDAO.deleteProject(p);
                fillData();
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    private void cloneProject(Project projectToClone) {
        Project newProject = new Project(projectToClone);
        newProject.setName(newProject.getName() + " - Copy");

        NewProjectDialog dialog = new NewProjectDialog(this, this, newProject, NewProjectDialog.CLONE);
        dialog.begin();
    }

    private void editProject(Project p) {
        NewProjectDialog dialog = new NewProjectDialog(this, this, p, NewProjectDialog.EDIT);
        dialog.begin();
    }

    private void fillData() {
        getLoaderManager().restartLoader(0, null, this);
        this.projectListView.setAdapter(this.projectListAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = ProjectTable.ALL_COLUMNS;
        return new CursorLoader(this, DBContentProvider.PROJECT_CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        projectListAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        projectListAdapter.swapCursor(null);
    }

    @Override
    public void onProjectCreate(Project p) {
        p.setId(projectDAO.saveProject(p));
        fillData();
    }

    @Override
    public void onProjectChange(Project p) {
        projectDAO.updateProject(p);
        fillData();
    }
}

