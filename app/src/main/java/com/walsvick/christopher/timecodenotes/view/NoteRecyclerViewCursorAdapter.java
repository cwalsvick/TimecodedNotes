package com.walsvick.christopher.timecodenotes.view;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.walsvick.christopher.timecodenotes.R;
import com.walsvick.christopher.timecodenotes.db.NoteDAO;
import com.walsvick.christopher.timecodenotes.model.Note;

/**
 * Created on 5/2/2015 by Christopher.
 */
public class NoteRecyclerViewCursorAdapter extends RecyclerView.Adapter<NoteRecyclerViewCursorAdapter.ViewHolder> {

    private Cursor dataCursor;
    private Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public NoteListItemView listItemView;

        public ViewHolder(View v) {
            super(v);
            listItemView = new NoteListItemView(v);
        }
    }

    public NoteRecyclerViewCursorAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.dataCursor = cursor;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_view_item_note, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        if (!dataCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        Note note = NoteDAO.cursorToNote(dataCursor);

        viewHolder.listItemView.setNote(note);
    }

    @Override
    public int getItemCount() {
        return (dataCursor == null) ? 0 : dataCursor.getCount();
    }

    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    public Cursor swapCursor(Cursor cursor) {
        if (dataCursor == cursor) {
            return null;
        }
        Cursor oldCursor = dataCursor;
        this.dataCursor = cursor;
        if (cursor != null) {
            this.notifyDataSetChanged();
        }
        return oldCursor;
    }

    public Object getItem(int position) {
        if (!dataCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        return NoteDAO.cursorToNote(dataCursor);
    }
}
