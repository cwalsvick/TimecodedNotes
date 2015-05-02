package com.walsvick.christopher.timecodenotes.view;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.walsvick.christopher.timecodenotes.R;
import com.walsvick.christopher.timecodenotes.model.Note;

/**
 * Created on 1/18/2015 by Christopher.
 */
public class NoteListItemView extends LinearLayout {

    private Note note;

    private TextView timeCode;
    private TextView noteText;
    private TextView cameraText;

    public NoteListItemView(Context context, Note note) {
        super(context);

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.list_view_item_note, this, true);

        timeCode = (TextView) view.findViewById(R.id.list_view_item_note_time_code);
        noteText = (TextView) view.findViewById(R.id.list_view_item_note);
        cameraText = (TextView) view.findViewById(R.id.list_view_item_note_camera);

        setNote(note);
    }

    public void setNote(Note note) {
        this.note = note;
        timeCode.setText(note.getTimeCode().toString("HH:mm:ss"));
        noteText.setText(note.getNote());
        cameraText.setText("- " + note.getCamera() + " -");
    }
}
