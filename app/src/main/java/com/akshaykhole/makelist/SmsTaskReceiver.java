// References: http://codetheory.in/android-sms
// https://developer.android.com/training/permissions/requesting.html

package com.akshaykhole.makelist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import com.akshaykhole.makelist.models.Task;

import org.json.*;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import io.realm.Realm;

/**
 * Created by akshay on 6/28/16.
 */
public class SmsTaskReceiver extends BroadcastReceiver {
    private String TAG = "SMS RECEIVER->";
    private Realm realm;
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    public SmsTaskReceiver() { }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(SMS_RECEIVED)) {
            SmsMessage[] smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            SmsMessage smsMessage = smsMessages[0];
            String smsFrom = smsMessage.getOriginatingAddress();
            String smsBody = smsMessage.getMessageBody();

            try {
                JSONObject mainObject = new JSONObject(smsBody);
                JSONObject makelistObject = mainObject.getJSONObject("makelist");

                String makelistText = makelistObject.getString("text");
                String makelistPriority = makelistObject.getString("priority");

                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                Date tomorrow = calendar.getTime();

                realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                Task t = realm.createObject(Task.class);
                t.setId(UUID.randomUUID().toString());
                t.setText(makelistText);
                t.setPriority(makelistPriority);
                t.setAssignedBy(smsFrom);
                t.setDueDate(tomorrow);
                t.setComplete(Boolean.FALSE);
                realm.commitTransaction();
            } catch (Exception e){
                Log.d(TAG + "EXCEPTION->", e.getMessage());
            }
        }
    }
}


