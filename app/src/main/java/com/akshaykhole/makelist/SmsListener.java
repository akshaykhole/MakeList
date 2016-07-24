package com.akshaykhole.makelist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by akshay on 7/24/16.
 */
public class SmsListener extends BroadcastReceiver {
    private SharedPreferences preferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Log.d("**************LOG --->", "GOT SMS");
        }
    }
}
