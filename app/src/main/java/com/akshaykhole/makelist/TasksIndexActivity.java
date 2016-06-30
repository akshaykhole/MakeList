package com.akshaykhole.makelist;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.akshaykhole.makelist.adapters.TasksIndexAdapter;
import com.akshaykhole.makelist.models.Task;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class TasksIndexActivity extends AppCompatActivity implements DialogInterface.OnDismissListener {
    private Realm realm;
    public TasksIndexAdapter tasksIndexAdapter;
    private ListView tasksLv;
    private ArrayList<Task> tasksArrayList;
    private RealmResults<Task> tasks;
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    final private static int REQUEST_CODE_RECEIVE_SMS = 123;
    final private static String TAG = "**ML**";

    @Override
    public void onDismiss(final DialogInterface dialog) {
        populateTasks();
    }

    // Define SMS receiver Broadcast receiver
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateTaskList(intent);
        }
    };

    private void updateTaskList(Intent intent) {
        Log.d(TAG, "handling sms");
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
            tasksArrayList.add(t);
            realm.commitTransaction();
        } catch (Exception e){
            Log.d(TAG + "EXCEPTION->", e.getMessage());
        }

        tasksIndexAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_index);
        configureDatabase();
        populateTasks();
        requestSmsReceivePermission();

        // Let user delete a task by long pressing on list view item
        tasksLv = (ListView) findViewById(R.id.tasksIndexListView);
        tasksLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position,
                                           long id) {

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        tasks.get(position).deleteFromRealm();
                    }
                });

                Toast.makeText(getApplicationContext(), "SUCCESS!",
                        Toast.LENGTH_SHORT).show();
                populateTasks();
                return true;
            }
        });

        tasksLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Task t = tasks.get(i);
                Bundle args = new Bundle();
                args.putString("taskId", t.getId());
                args.putString("taskToEdit", "true");

                FragmentManager fm = getSupportFragmentManager();
                TaskFormFragment tff = TaskFormFragment.newInstance(args);
                tff.show(fm, "task_form");
            }
        });

        registerReceiver(broadcastReceiver, new IntentFilter(SMS_RECEIVED));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(SMS_RECEIVED));
    }

    public void createNewTask(View view) {
        FragmentManager fm = getSupportFragmentManager();
        TaskFormFragment tff = TaskFormFragment.newInstance(new Bundle());
        tff.show(fm, "task_form");
    }

    private void populateTasks() {
        RealmQuery<Task> query = realm.where(Task.class);
        Log.d("POPULATING -->", "Task list");
        tasks = query.findAll();

        tasksArrayList =  new ArrayList<Task>();

        for(Task t : tasks) {
            tasksArrayList.add(t);
        }

        tasksIndexAdapter = new TasksIndexAdapter(this, tasksArrayList);
        ListView tasksIndexListView = (ListView) findViewById(R.id.tasksIndexListView);
        tasksIndexListView.setAdapter(tasksIndexAdapter);
    }

    private void configureDatabase() {
        Log.d("DATABASE -->", "CONFIGURING..");
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .name("makelist.realm")
                .build();

        Realm.setDefaultConfiguration(realmConfiguration);
        realm = Realm.getDefaultInstance();
    }

    public void requestSmsReceivePermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS);

        if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] { Manifest.permission.RECEIVE_SMS },
                    REQUEST_CODE_RECEIVE_SMS);
        }
    }
}
