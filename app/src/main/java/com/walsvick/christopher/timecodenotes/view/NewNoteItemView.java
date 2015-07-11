package com.walsvick.christopher.timecodenotes.view;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


/**
 * Created by Christopher on 7-10-2015
 */
public class NewNoteItemView extends CardView {

    private Context mContext;
    private NewNoteOnBackPressedListener mListener;

    public NewNoteItemView(Context context) {
        super(context);
        this.mContext = context;
    }

    public NewNoteItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public void setBackPressedListener(NewNoteOnBackPressedListener listener) {
        this.mListener = listener;
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm.isActive() && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            this.setVisibility(View.GONE);
            mListener.newNoteOnBackPressed();
        }

        return super.dispatchKeyEventPreIme(event);
    }

    public interface NewNoteOnBackPressedListener {
        void newNoteOnBackPressed();
    }
}
