package com.walsvick.christopher.timecodenotes.view;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.walsvick.christopher.timecodenotes.R;
import com.walsvick.christopher.timecodenotes.db.ProjectDAO;
import com.walsvick.christopher.timecodenotes.model.Project;

import org.apache.commons.lang3.StringUtils;

/**
 * Created on 5/2/2015 by Christopher.
 */
public class RecyclerViewCursorAdapter extends RecyclerView.Adapter<RecyclerViewCursorAdapter.ViewHolder> {

    private Cursor dataCursor;
    private Context context;
    private OnRecyclerItemClickListener callback;

    public interface OnRecyclerItemClickListener {
        public void onItemClickListener(int position);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ProjectListItemView listItemView;
        public OnRecyclerItemClickListener callback;

        public ViewHolder(View v, OnRecyclerItemClickListener callback) {
            super(v);
            listItemView = new ProjectListItemView(v);
        }
    }

    public RecyclerViewCursorAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.dataCursor = cursor;
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_view_item_project, parent, false);

       // ProjectListItemView item = new ProjectListItemView(context, itemView);

        ViewHolder vh = new ViewHolder(itemView, callback);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        if (!dataCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        Project project = ProjectDAO.cursorToProject(dataCursor);

        viewHolder.listItemView.setProject(project);
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
        return ProjectDAO.cursorToProject(dataCursor);
    }
}
