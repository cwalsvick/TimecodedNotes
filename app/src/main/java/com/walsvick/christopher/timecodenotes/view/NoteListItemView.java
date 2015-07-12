package com.walsvick.christopher.timecodenotes.view;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.walsvick.christopher.timecodenotes.R;
import com.walsvick.christopher.timecodenotes.model.Note;
import com.walsvick.christopher.timecodenotes.model.Project;

import java.util.ArrayList;

/**
 * Created on 1/18/2015 by Christopher.
 */
public class NoteListItemView {

    private Note note;
    private View view;

    private TextView timeCode;
    private TextView noteText;
    private Spinner cameraSpinner;


    public NoteListItemView(View v) {
        this.view = v;
        timeCode = (TextView) view.findViewById(R.id.list_view_item_note_time_code);
        noteText = (TextView) view.findViewById(R.id.list_view_item_note);
        cameraSpinner = (Spinner) view.findViewById(R.id.list_view_item_camera_spinner);
    }

    public void setNote(Note note, Project project) {
        this.note = note;
        timeCode.setText(note.getTimeCode());
        noteText.setText(note.getNote());

        ArrayList<String> cameras = project.getCameras();
        cameras.remove(note.getCamera());
        cameras.add(0, note.getCamera());

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(view.getContext(),
                android.R.layout.simple_spinner_item, cameras);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cameraSpinner.setAdapter(dataAdapter);
    }
}
