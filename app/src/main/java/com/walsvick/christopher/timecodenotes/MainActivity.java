package com.walsvick.christopher.timecodenotes;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.walsvick.christopher.timecodenotes.com.walsvick.christopher.timecodenodes.model.Project;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements NewProjectDialog.NewProjectCreatedListener {

    private ListView projectListView;
    private ArrayList<Project> projectList;
    private ProjectListAdapter projectListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        projectListView = (ListView) findViewById(R.id.project_listView);
        projectList = new ArrayList<>();
        projectListAdapter = new ProjectListAdapter(this, projectList);
        projectListView.setAdapter(projectListAdapter);
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
