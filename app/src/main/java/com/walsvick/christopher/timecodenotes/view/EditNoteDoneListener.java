package com.walsvick.christopher.timecodenotes.view;

import com.walsvick.christopher.timecodenotes.model.Note;

/**
 * Created by Christopher on 7/12/2015.
 */
public interface EditNoteDoneListener {

    public void onDone(Note note);
    public void onBackPressedWhileEdititing();
}
