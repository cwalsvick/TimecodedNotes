package com.walsvick.christopher.timecodenotes;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.walsvick.christopher.timecodenotes.com.walsvick.christopher.timecodenodes.model.Note;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christopher on 1/2/2015.
 */
public class NoteListAdapter extends BaseAdapter {

    private Context context;
    private List<Note> noteList;

    public NoteListAdapter(Context context, ArrayList<Note> list) {
        this.context = context;
        this.noteList = list;
    }


    @Override
    public int getCount() {
        return noteList.size();
    }

    @Override
    public Object getItem(int position) {
        return noteList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        Note note = (Note) getItem(position);

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.list_view_item_note, parent, false);
         }

        row.setTag(note);
        final TextView timeCode = (TextView) row.findViewById(R.id.list_view_item_note_time_code);
        final TextView noteText = (TextView) row.findViewById(R.id.list_view_item_note);
        final TextView cameraText = (TextView) row.findViewById(R.id.list_view_item_note_camera);

        timeCode.setText(note.getTimeCode().toString("HH:mm:ss"));
        noteText.setText(note.getNote());
        cameraText.setText("- " + note.getCamera().getName() + " -");

        return row;
    }
}
