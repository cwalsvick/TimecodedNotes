package com.walsvick.christopher.timecodenotes.view;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.walsvick.christopher.timecodenotes.R;

/**
 * Created by Christopher on 7/10/2015.
 */
public class TimeCodePickerDialog {

    NumberPicker mvHourPicker;
    NumberPicker mvMinutePicker;
    NumberPicker mvSecondPicker;
    View mvView;

    public TimeCodePickerDialog(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mvView = inflater.inflate(R.layout.time_code_number_picker, null);

        mvHourPicker = (NumberPicker) mvView.findViewById(R.id.hh_picker);
        mvHourPicker.setMaxValue(24);
        mvHourPicker.setMinValue(0);
        mvMinutePicker = (NumberPicker) mvView.findViewById(R.id.mm_picker);
        mvMinutePicker.setMaxValue(59);
        mvMinutePicker.setMinValue(0);
        mvSecondPicker = (NumberPicker) mvView.findViewById(R.id.ss_picker);
        mvSecondPicker.setMaxValue(59);
        mvSecondPicker.setMinValue(0);
    }

    public View getView() {
        return mvView;
    }

    public int getHours() {
        return mvHourPicker.getValue();
    }

    public int getMinutes() {
        return mvMinutePicker.getValue();
    }

    public int getSeconds() {
        return mvSecondPicker.getValue();
    }
}
