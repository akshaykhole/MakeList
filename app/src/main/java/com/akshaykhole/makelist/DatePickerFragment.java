package com.akshaykhole.makelist;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Button;
import android.widget.DatePicker;

/**
 * Created by akshay on 9/16/16.
 */
public class DatePickerFragment extends DialogFragment
                                implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int y, int m, int d) {
        Button datePickerButton = (Button) getActivity().findViewById(
                R.id.btnSendTodoFormSelectDate
        );

        datePickerButton.setText(y + "/" + m + "/" + d);
    }
}
