package com.akshaykhole.makelist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by akshay on 8/28/16.
 */
public class SendTaskFormFragment extends DialogFragment {
    public SendTaskFormFragment() {}

    public static SendTaskFormFragment newInstance(Bundle args) {
        SendTaskFormFragment frag = new SendTaskFormFragment();
        frag.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.send_task_form, container);
    }
}
