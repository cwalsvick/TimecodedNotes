package com.walsvick.christopher.timecodenotes.activity;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
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
import com.walsvick.christopher.timecodenotes.view.RecyclerViewCursorAdapter;

public class MainActivity extends ActionBarActivity implements
        NewProjectDialog.NewProjectDialogListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private ProjectDAO projectDAO;

    private RecyclerView projectListView;
    private RecyclerViewCursorAdapter projectListAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private FloatingActionButton newProjectButton;

    public final static String SELECTED_PROJECT = "COM.WALSVICK.CHRISTOPHER.TIMECODENOTES.SELECTED_PROJECT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        projectListAdapter = new RecyclerViewCursorAdapter(this, null);
        projectListView.setAdapter(projectListAdapter);
        projectListView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent i = new Intent(MainActivity.this, TakeNotesActivity.class);
                i.putExtra(SELECTED_PROJECT, (Project) projectListAdapter.getItem(position));
                startActivity(i);
            }
        }));


        setListViewClickListener();
        registerForContextMenu(projectListView);
        fillData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.new_project) {
            NewProjectDialog dialog = new NewProjectDialog(this, this);
            dialog.begin();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderTitle("Action Menu");

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_context_project_list, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

        Project project = (Project) projectListAdapter.getItem(info.position);

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
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("image/jpeg");
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                "Test Subject");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                "Test contents");

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

    private void deleteProject(Project p) {
        projectDAO.deleteProject(p);
        fillData();
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

    private void setListViewClickListener() {
        /*projectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, TakeNotesActivity.class);
                i.putExtra(SELECTED_PROJECT,
                        projectDAO.cursorToProject((Cursor) projectListView.getItemAtPosition(position)));
                startActivity(i);
            }
        });*/
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

