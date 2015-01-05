package com.walsvick.christopher.timecodenotes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.walsvick.christopher.timecodenotes.IO.StorageUtil;
import com.walsvick.christopher.timecodenotes.model.Note;
import com.walsvick.christopher.timecodenotes.model.Project;

import org.joda.time.LocalTime;


public class TakeNotesActivity extends ActionBarActivity {

    private Project project;
    private ListView noteListView;
    private NoteListAdapter noteListAdapter;

    private LocalTime timeCode;

    private TextView timeCodeTextView;
    private Button addNoteButton;
    private Spinner cameraSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_notes);

        getSelectedProject();
        setTitle(project.getName());

        noteListView = (ListView) findViewById(R.id.take_notes_list_view);
        noteListAdapter = new NoteListAdapter(this, project.getNoteList());
        noteListView.setAdapter(noteListAdapter);

        timeCode = new LocalTime();
        timeCodeTextView = (TextView) findViewById(R.id.take_notes_time_code_text_view);
        setUpTimeCodeRunnable();

        addNoteButton = (Button) findViewById(R.id.take_notes_add_note_button);
        createAddNoteButtonListener();

        cameraSpinner = (Spinner) findViewById(R.id.take_notes_camera_spinner);
        initCameraSpinner();
    }

    @Override
    protected void onPause() {
        Log.d("TAKE_NOTES_ACTIVITY", "onPause");
        StorageUtil store = new StorageUtil();
        String rtn = store.saveProject(project);
        if (rtn != null) {
            Toast.makeText(this, rtn, Toast.LENGTH_SHORT).show();
        }

        super.onPause();
    }

    private void initCameraSpinner() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, project.getCameraListNames());

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cameraSpinner.setAdapter(dataAdapter);
    }

    private void createAddNoteButtonListener() {
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Note note = new Note(timeCode.now());
                note.setCamera(project.getCameraList().get(cameraSpinner.getSelectedItemPosition()));
                launchNewNoteDialog(note);
            }
        });
    }

    private void launchNewNoteDialog(final Note note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText editText = new EditText(this);
        editText.setText(note.getNote());
        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editText.setSingleLine(false);
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                editText.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
            }
        });
        editText.requestFocus();
        builder.setView(editText);

        builder.setTitle("Camera " + note.getCamera().getName()
                + " - " + note.getTimeCode().toString("HH:mm:ss"));
        builder.setPositiveButton(getResources().getString(R.string.dialog_new_note_create),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        note.setNote(editText.getText().toString());
                        project.addNote(note);
                        noteListAdapter.notifyDataSetChanged();
                    }
                });
        builder.setNegativeButton(getResources().getString(R.string.dialog_new_note_cancel),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void setUpTimeCodeRunnable() {
        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                timeCodeTextView.setText(timeCode.now().toString("HH:mm:ss"));
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();
    }

    private void getSelectedProject() {
        Intent i = getIntent();
        project = i.getParcelableExtra(MainActivity.SELECTED_PROJECT);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_take_notes, menu);
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

        return super.onOptionsItemSelected(item);
    }
}
