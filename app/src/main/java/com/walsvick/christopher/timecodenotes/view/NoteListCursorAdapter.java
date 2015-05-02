package com.walsvick.christopher.timecodenotes.view;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.walsvick.christopher.timecodenotes.db.NoteDAO;
import com.walsvick.christopher.timecodenotes.model.Note;

/**
 * Created on 1/10/2015 by Christopher.
 */
public class NoteListCursorAdapter extends CursorAdapter {

    public NoteListCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        NoteDAO dao = new NoteDAO(context);
        Note note = dao.cursorToNote(cursor);

        return new NoteListItemView(context, note);
    }

    @Override
    public void bindView(View oldView, Context context, Cursor cursor) {
        NoteDAO dao = new NoteDAO(context);
        Note note = dao.cursorToNote(cursor);

        NoteListItemView view = (NoteListItemView) oldView;
        view.setNote(note);
    }
}
