package com.walsvick.christopher.timecodenotes.activity;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.walsvick.christopher.timecodenotes.R;
import com.walsvick.christopher.timecodenotes.db.DBContentProvider;
import com.walsvick.christopher.timecodenotes.db.NoteDAO;
import com.walsvick.christopher.timecodenotes.db.NoteTable;
import com.walsvick.christopher.timecodenotes.model.Note;
import com.walsvick.christopher.timecodenotes.model.Project;
import com.walsvick.christopher.timecodenotes.view.NoteListCursorAdapter;

import org.joda.time.LocalDateTime;


public class TakeNotesActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Project project;
    private ListView noteListView;
    private NoteListCursorAdapter noteListAdapter;

    private TextView timeCodeTextView;
    private Button addNoteButton;
    private Spinner cameraSpinner;
    private LinearLayout bottomContainer;

    private NoteDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_notes);

        dao = new NoteDAO(this);

        getSelectedProject();
        setTitle(project.getName());

        noteListView = (ListView) findViewById(R.id.take_notes_list_view);
        noteListAdapter = new NoteListCursorAdapter(this, null, 0);
        noteListView.setAdapter(noteListAdapter);
        setListItemClickListener();
        initViewItems();

        fillData();
    }

    private void initViewItems() {
        timeCodeTextView = (TextView) findViewById(R.id.take_notes_time_code_text_view);
        timeCodeTextView.setText(LocalDateTime.now().toString("HH:mm:ss"));
        setUpTimeCodeRunnable();

        addNoteButton = (Button) findViewById(R.id.take_notes_add_note_button);
        createAddNoteButtonListener();

        cameraSpinner = (Spinner) findViewById(R.id.take_notes_camera_spinner);
        initCameraSpinner();

        bottomContainer = (LinearLayout) findViewById(R.id.activity_take_notes_bottom_container);
    }

    private void setListItemClickListener() {
        noteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                launchNewNoteDialog(dao.cursorToNote((Cursor) noteListView.getItemAtPosition(position)), position);
            }
        });
    }

    private void fillData() {
        getLoaderManager().restartLoader(0, null, this);
        this.noteListView.setAdapter(this.noteListAdapter);
    }

    private void initCameraSpinner() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, project.getCameras());

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cameraSpinner.setAdapter(dataAdapter);
    }

    private void createAddNoteButtonListener() {
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Note note = new Note(LocalDateTime.now());
                note.setCamera(project.getCameras().get(cameraSpinner.getSelectedItemPosition()));
                launchNewNoteDialog(note, -1);
            }
        });
    }

    private void launchNewNoteDialog(final Note note, final int position) {
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

        builder.setTitle("Camera " + note.getCamera()
                + " - " + note.getTimeCode().toString("HH:mm:ss"));
        builder.setPositiveButton(getResources().getString(R.string.dialog_new_note_create),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        note.setNote(editText.getText().toString());
                        if (position < 1) {
                            note.setId(dao.saveNote(project, note));
                        }
                        else {
                            dao.updateNote(project, note);
                        }

                        fillData();

                        dialog.cancel();
                        bottomContainer.setVisibility(View.VISIBLE);

                    }
                });
        builder.setNegativeButton(getResources().getString(R.string.dialog_new_note_cancel),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        bottomContainer.setVisibility(View.GONE);

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
                                timeCodeTextView.setText(LocalDateTime.now().toString("HH:mm:ss"));
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = NoteTable.ALL_COLUMNS;
        Uri uri = Uri.parse(DBContentProvider.NOTE_CONTENT_URI + "/project/" + project.getId());
        return new CursorLoader(this, uri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        noteListAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        noteListAdapter.swapCursor(null);
    }
}
