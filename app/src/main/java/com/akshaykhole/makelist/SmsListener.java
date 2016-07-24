package com.akshaykhole.makelist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import com.akshaykhole.makelist.models.Task;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by akshay on 7/24/16.
 */
public class SmsListener extends BroadcastReceiver {
    private Realm realm;
    final private static String TAG = "**ML-TaskSMSListener**";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            configureDatabase(context);
            realm = Realm.getDefaultInstance();

            SmsMessage[] smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            SmsMessage smsMessage = smsMessages[0];
            String smsFrom = smsMessage.getOriginatingAddress();
            String smsBody = smsMessage.getMessageBody();

            try {
                JSONObject mainObject = new JSONObject(smsBody);
                JSONObject makelistObject = mainObject.getJSONObject("makelist");

                String makelistText = makelistObject.getString("text");
                String makelistPriority = makelistObject.getString("priority");
                String dueDateStr = makelistObject.getString("dueDate");
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                Date dueDate = dateFormat.parse(dueDateStr);

                realm.beginTransaction();
                Task t = realm.createObject(Task.class);
                t.setId(UUID.randomUUID().toString());
                t.setText(makelistText);
                t.setPriority(makelistPriority);
                t.setAssignedBy(smsFrom);
                t.setDueDate(dueDate);
                t.setComplete(Boolean.FALSE);
                realm.commitTransaction();
            } catch (Exception e){
                Log.d(TAG, e.getMessage());
            }
            realm.close();
            TasksIndexActivity.updateTaskList();
        }
    }

    private void configureDatabase(Context context) {
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(context)
                .name("makelist.realm")
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
        realm = Realm.getDefaultInstance();
    }
}
