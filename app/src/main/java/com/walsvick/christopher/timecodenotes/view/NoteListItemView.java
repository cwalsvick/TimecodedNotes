package com.walsvick.christopher.timecodenotes.view;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.walsvick.christopher.timecodenotes.R;
import com.walsvick.christopher.timecodenotes.model.Note;
import com.walsvick.christopher.timecodenotes.model.Project;

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
        timeCode.setText(note.getTimeCode().toString("HH:mm:ss"));
        noteText.setText(note.getNote());
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(view.getContext(),
                android.R.layout.simple_spinner_item, project.getCameras());

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cameraSpinner.setAdapter(dataAdapter);
    }
}
