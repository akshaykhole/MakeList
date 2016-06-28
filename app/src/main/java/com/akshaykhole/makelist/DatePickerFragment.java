package com.akshaykhole.makelist;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.icu.util.Calendar;
import android.os.Bundle;

/**
 * Created by akshay on 6/27/16.
 */
public class DatePickerFragment extends DialogFragment {
    public interface SelectDateFragmentListener {
        void onFinishSelectDate(Calendar c);
    }

    public void sendBackResult() {
        SelectDateFragmentListener listener = (SelectDateFragmentListener) getTargetFragment();
        listener.onFinishSelectDate(Calendar.getInstance());
    }
}
