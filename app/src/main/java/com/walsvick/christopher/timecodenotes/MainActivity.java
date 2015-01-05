package com.walsvick.christopher.timecodenotes;

import android.content.Intent;
import android.os.Parcel;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.walsvick.christopher.timecodenotes.IO.StorageUtil;
import com.walsvick.christopher.timecodenotes.model.Project;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements NewProjectDialog.NewProjectCreatedListener {

    private ListView projectListView;
    private ArrayList<Project> projectList;
    private ProjectListAdapter projectListAdapter;

    public final static String SELECTED_PROJECT = "COM.WALSVICK.CHRISTOPHER.TIMECODENOTES.SELECTED_PROJECT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        projectListView = (ListView) findViewById(R.id.project_listView);
        projectList = new ArrayList<>();
        projectListAdapter = new ProjectListAdapter(this, projectList);
        projectListView.setAdapter(projectListAdapter);
        setListViewClickListener();

        getSavedProjects();
    }

    private void getSavedProjects() {
        StorageUtil storage = new StorageUtil();

        File[] savedProjects = storage.getSavedProjects();
        if (savedProjects != null) {
            for (File f : savedProjects) {
                projectList.add(storage.readProject(f));
            }
            projectListAdapter.notifyDataSetChanged();
        }
    }

    private void setListViewClickListener() {
        projectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, TakeNotesActivity.class);
                Parcel parcel = Parcel.obtain();
                i.putExtra(SELECTED_PROJECT, projectList.get(position));
                startActivity(i);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
    public void onProjectCreate(Project p) {
        projectList.add(p);
        projectListAdapter.notifyDataSetChanged();
    }
}
