package com.walsvick.christopher.timecodenotes.view;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.walsvick.christopher.timecodenotes.R;
import com.walsvick.christopher.timecodenotes.model.Note;
import com.walsvick.christopher.timecodenotes.model.Project;

/**
 * Created on 1/18/2015 by Christopher.
 */
public class NoteListItemView implements EditNoteListener {

    private Note mNote;
    private Project mProject;

    private NoteItemView mView;
    private TextView timeCode;
    private TextView noteText;
    private Spinner cameraSpinner;
    private TextView cameraTextView;

    private EditText editNoteTextView;
    private EditNoteListener mListener;


    public NoteListItemView(View v, EditNoteListener listener) {
        this.mView = (NoteItemView) v;
        this.mListener = listener;

        mView.setBackPressedListener(this);

        timeCode = (TextView) mView.findViewById(R.id.list_view_item_note_time_code);
        noteText = (TextView) mView.findViewById(R.id.list_view_item_note);
        cameraSpinner = (Spinner) mView.findViewById(R.id.list_view_item_camera_spinner);
        editNoteTextView = (EditText) mView.findViewById(R.id.new_note_edit_text);
        cameraTextView = (TextView) mView.findViewById(R.id.list_view_item_camera_text_view);
    }

    public void setNote(Note note, Project project) {
        this.mNote = note;
        this.mProject = project;
        timeCode.setText(note.getTimeCode());
        noteText.setText(note.getNote());
        editNoteTextView.setText(note.getNote());
        cameraTextView.setText(note.getCamera());

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mView.getContext(),
                android.R.layout.simple_spinner_item, project.getCameras());

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cameraSpinner.setAdapter(dataAdapter);
        cameraSpinner.setSelection(project.getCameras().indexOf(mNote.getCamera()));
    }

    private void resetNoteView() {
        noteText.setText(mNote.getNote());
        cameraTextView.setText(mNote.getCamera());
        editNoteTextView.setText(mNote.getNote());
        cameraSpinner.setSelection(mProject.getCameras().indexOf(mNote.getCamera()));

        editNoteTextView.setVisibility(View.GONE);
        cameraSpinner.setVisibility(View.GONE);
        noteText.setVisibility(View.VISIBLE);
        cameraTextView.setVisibility(View.VISIBLE);
    }

    public void editNote() {
        noteText.setVisibility(View.GONE);
        cameraTextView.setVisibility(View.GONE);
        editNoteTextView.setVisibility(View.VISIBLE);
        cameraSpinner.setVisibility(View.VISIBLE);

        editNoteTextView.setSelection(editNoteTextView.getText().length());

        editNoteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                InputMethodManager inputMethodManager = (InputMethodManager) mView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (hasFocus) {
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                } else {
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    resetNoteView();
                }
            }
        });
        
        editNoteTextView.setOnKeyListener(new EditText.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER
                        || keyCode == KeyEvent.KEYCODE_DPAD_CENTER)) {
                    mNote.setNote(editNoteTextView.getText().toString());
                    mNote.setCamera((String) cameraSpinner.getSelectedItem());
                    resetNoteView();

                    mListener.onDone(mNote);

                    editNoteTextView.clearFocus();
                    return true;
                }
                return false;
            }
        });
        editNoteTextView.requestFocus();
    }

    @Override
    public void onEdit(Note n, int notePosition) {

    }

    @Override
    public void onDone(Note note) {

    }

    @Override
    public void onBackPressedWhileEditing() {
        resetNoteView();
        mListener.onBackPressedWhileEditing();
    }

    public Note getNote() {
        return this.mNote;
    }
}
