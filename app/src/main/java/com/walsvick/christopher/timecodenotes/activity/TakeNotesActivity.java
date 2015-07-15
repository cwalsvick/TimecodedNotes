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
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.walsvick.christopher.timecodenotes.R;
import com.walsvick.christopher.timecodenotes.db.DBContentProvider;
import com.walsvick.christopher.timecodenotes.db.NoteDAO;
import com.walsvick.christopher.timecodenotes.db.NoteTable;
import com.walsvick.christopher.timecodenotes.model.Note;
import com.walsvick.christopher.timecodenotes.model.Project;
import com.walsvick.christopher.timecodenotes.view.EditNoteListener;
import com.walsvick.christopher.timecodenotes.view.FloatingActionButton;
import com.walsvick.christopher.timecodenotes.view.NewNoteItemView;
import com.walsvick.christopher.timecodenotes.view.NoteRecyclerViewCursorAdapter;
import com.walsvick.christopher.timecodenotes.view.TimeCodePickerDialog;

import org.joda.time.LocalDateTime;


public class TakeNotesActivity extends ActionBarActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,NewNoteItemView.NewNoteOnBackPressedListener,
        EditNoteListener {

    private Project project;

    private RecyclerView noteListView;
    private NoteRecyclerViewCursorAdapter noteListAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private Chronometer timeCodeTextView;
    private FloatingActionButton addNoteButton;
    private ImageButton editTimeStampButton;

    // view items for new note cardview
    private NewNoteItemView newNoteItemView;
    private TextView newNoteTimeStamp;
    private EditText newNoteEditText;
    private Spinner newNoteCameraSpinner;
    private String timeOfNewNote;
    private long manualBaseTime;

    private boolean mNewNoteOnBackPressed;
    private String mLastCameraUsed;

    private NoteDAO dao;
    private boolean mNewNoteActive;
    private boolean mEditNoteActive;
    private Note mNoteBeingEdited;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_notes);

        dao = new NoteDAO(this);

        getSelectedProject();
        setTitle(project.getName());

        noteListView = (RecyclerView) findViewById(R.id.note_recycler_view);
        noteListView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        noteListView.setLayoutManager(layoutManager);
        noteListAdapter = new NoteRecyclerViewCursorAdapter(this, null, project, this);
        noteListView.setAdapter(noteListAdapter);

        initViewItems();

        fillData();
    }

    private void initViewItems() {
        setupTimeCodeTextView();

        addNoteButton = (FloatingActionButton) findViewById(R.id.take_notes_add_note_button);
        createAddNoteButtonListener();
        editTimeStampButton = (ImageButton) findViewById(R.id.edit_time_stamp_button);
        createEditTimeStampButtonListener();

        newNoteItemView = (NewNoteItemView) findViewById(R.id.new_note_item_view);
        newNoteItemView.setBackPressedListener(this);
        newNoteEditText = (EditText) findViewById(R.id.new_note_edit_text);
        newNoteTimeStamp = (TextView) findViewById(R.id.new_note_time_code);
        newNoteCameraSpinner = (Spinner) findViewById(R.id.new_note_camera_spinner);
    }

    private void createEditTimeStampButtonListener() {
        editTimeStampButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TakeNotesActivity.this);

                final TimeCodePickerDialog dialog = new TimeCodePickerDialog(TakeNotesActivity.this);
                builder.setView(dialog.getView());
                builder.setPositiveButton("Start Time", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startTimerFromManualBase(dialog.getHours(), dialog.getMinutes(), dialog.getSeconds());
                        dialogInterface.cancel();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.create().show();
            }
        });
    }

    private void startTimerFromManualBase(int hours, int min, int sec) {
        manualBaseTime = hours * 60 * 60 * 1000 + min * 60 * 1000  + sec * 1000;
        timeCodeTextView.stop();
        timeCodeTextView.setBase(SystemClock.elapsedRealtime());
        timeCodeTextView.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer cArg) {
                long time = ((SystemClock.elapsedRealtime() - cArg.getBase()) + manualBaseTime) % 90000000;
                int h = (int) (time / 3600000);
                int m = (int) (time - h * 3600000) / 60000;
                int s = (int) (time - h * 3600000 - m * 60000) / 1000;
                String hh = h < 10 ? "0" + h : h + "";
                String mm = m < 10 ? "0" + m : m + "";
                String ss = s < 10 ? "0" + s : s + "";
                cArg.setText(hh + ":" + mm + ":" + ss);
            }
        });

        timeCodeTextView.start();
    }

    private void setupTimeCodeTextView() {
        timeCodeTextView = (Chronometer) findViewById(R.id.take_notes_time_code_chronometer);
        timeCodeTextView.setBase(SystemClock.elapsedRealtime());
        timeCodeTextView.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer cArg) {
                cArg.setText(LocalDateTime.now().toString("HH:mm:ss"));
            }
        });

        timeCodeTextView.start();
    }

    private void fillData() {
        getLoaderManager().restartLoader(0, null, this);
        this.noteListView.setAdapter(this.noteListAdapter);
    }

    private void createAddNoteButtonListener() {
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeOfNewNote = timeCodeTextView.getText().toString();
                newNoteTimeStamp.setText(timeOfNewNote);

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(v.getContext(),
                        android.R.layout.simple_spinner_item, project.getCameras());

                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                newNoteCameraSpinner.setAdapter(dataAdapter);
                newNoteCameraSpinner.setSelection(mLastCameraUsed != null ? project.getCameras().indexOf(mLastCameraUsed) : 0);
                newNoteEditText.setText(null);
                newNoteItemView.setVisibility(View.VISIBLE);

                newNoteEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (hasFocus) {
                            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                        } else {
                            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            resetNewNoteView();
                        }
                    }
                });
                newNoteEditText.setOnKeyListener(new EditText.OnKeyListener() {
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if ((event.getAction() == KeyEvent.ACTION_DOWN)
                                && (keyCode == KeyEvent.KEYCODE_ENTER
                                || keyCode == KeyEvent.KEYCODE_DPAD_CENTER)) {
                            createNewNote();
                            resetNewNoteView();
                            fillData();
                            invalidateOptionsMenu();
                            return true;
                        }
                        return false;
                    }
                });
                newNoteEditText.requestFocus();

                mNewNoteActive = true;
                invalidateOptionsMenu();
            }
        });
    }

    private void resetNewNoteView() {
        newNoteItemView.setVisibility(View.GONE);
        newNoteEditText.setText(null);
        newNoteCameraSpinner.setSelection(mLastCameraUsed != null ? project.getCameras().indexOf(mLastCameraUsed) : 0);
        newNoteEditText.clearFocus();
    }

    private void createNewNote() {
        Note n = new Note();
        n.setProjectId(project.getId());
        n.setTimeCode(timeOfNewNote);
        n.setNote(newNoteEditText.getText().toString());
        n.setCamera(project.getCameras().get(newNoteCameraSpinner.getSelectedItemPosition()));
        n.setProjectId(project.getId());

        int noteId = dao.saveNote(project, n);
        n.setId(noteId);

        mLastCameraUsed = n.getCamera();
    }

    private void editNote(Note note) {
        dao.updateNote(note);
    }

    private void getSelectedProject() {
        Intent i = getIntent();
        project = i.getParcelableExtra(MainActivity.SELECTED_PROJECT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_take_notes, menu);

        if (mNewNoteActive) {
            MenuItem cancelNote = menu.findItem(R.id.cancel_note);
            cancelNote.setVisible(true);
            mNewNoteActive = false;
        }

        if (mEditNoteActive) {
            MenuItem deleteNote = menu.findItem(R.id.delete_note);
            deleteNote.setVisible(true);
            mEditNoteActive = false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.cancel_note:
                mNewNoteActive = false;
                resetNewNoteView();
                invalidateOptionsMenu();
                break;
            case R.id.delete_note:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Delete Note");
                builder.setMessage("Are you sure you want to delete this note?");
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mEditNoteActive = false;
                        dao.deleteNote(mNoteBeingEdited);
                        noteListAdapter.notifyDataSetChanged();
                        fillData();
                        invalidateOptionsMenu();
                        dialogInterface.cancel();
                    }
                });
                builder.create().show();
                break;
            default:

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

    @Override
    public void newNoteOnBackPressed() {
        this.mNewNoteOnBackPressed = true;
        invalidateOptionsMenu();
    }

    @Override
    public void onBackPressed() {
        if (mNewNoteOnBackPressed) {
            mNewNoteOnBackPressed = false;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onEdit(Note n) {
        mNoteBeingEdited = n;
        mEditNoteActive = true;
        invalidateOptionsMenu();
    }

    @Override
    public void onDone(Note note) {
        editNote(note);
        fillData();
        invalidateOptionsMenu();
    }

    @Override
    public void onBackPressedWhileEdititing() {
        this.mNewNoteOnBackPressed = true;
        invalidateOptionsMenu();
    }
}
