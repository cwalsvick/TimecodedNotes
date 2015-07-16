package com.walsvick.christopher.timecodenotes.view;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;


/**
 * Created by Christopher on 7-10-2015
 */
public class NoteItemView extends CardView {

    private Context mContext;
    private EditNoteListener mListener;

    public NoteItemView(Context context) {
        super(context);
        this.mContext = context;
    }

    public NoteItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public void setBackPressedListener(EditNoteListener listener) {
        this.mListener = listener;
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm.isActive() && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            mListener.onBackPressedWhileEditing();
        }

        return super.dispatchKeyEventPreIme(event);
    }
}
