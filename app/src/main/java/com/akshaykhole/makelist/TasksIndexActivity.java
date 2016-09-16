package com.akshaykhole.makelist;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.provider.Telephony;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.akshaykhole.makelist.adapters.TasksIndexAdapter;
import com.akshaykhole.makelist.models.Task;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class TasksIndexActivity
        extends AppCompatActivity
        implements DialogInterface.OnDismissListener {

    final private static int REQUEST_CODE_RECEIVE_SMS = 123;
    final private static int REQUEST_CODE_SEND_SMS = 321;
    final private static String TAG = "**ML**";

    private Realm realm;
    private ListView tasksLv;
    private ArrayList<Task> tasksArrayList;
    private RealmResults<Task> tasks;
    private TasksIndexAdapter tasksIndexAdapter;
    public BroadcastReceiver smsReceiver;

    @Override
    public void onDismiss(final DialogInterface dialog) {
        populateTasks();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_index);
        configureDatabase();
        populateTasks();
        requestSmsPermission();

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
                tasksArrayList.remove(position);
                tasksIndexAdapter.notifyDataSetChanged();

                Toast.makeText(getApplicationContext(), "Deleted Task Successfully!",
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        // Show the Task form to create or edit on click
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

        smsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String s = intent.getStringExtra(SmsListener.SMS_LISTENER_TASK_RECEIVED);
                if(s == "MAKELIST_SMS_RECEIVED") {
                    populateTasks();
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        LocalBroadcastManager.getInstance(this).registerReceiver((smsReceiver),
                new IntentFilter(SmsListener.SMS_LISTENER_BROADCASTS)
        );
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(smsReceiver);
        realm.close();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        realm.close();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(smsReceiver);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void createNewTask(View view) {
        FragmentManager fm = getSupportFragmentManager();
        TaskFormFragment tff = TaskFormFragment.newInstance(new Bundle());
        tff.show(fm, "task_form");
    }

    public void sendTask(View view) {
        Intent intent = new Intent(this, SendTodoActivity.class);
        startActivity(intent);
    }

    private void populateTasks() {
        RealmQuery<Task> query = realm.where(Task.class);
        tasks = query.findAll();
        tasksArrayList =  new ArrayList<Task>();

        if((tasks.size() == 0) || tasks.size() != tasksArrayList.size()) {
            for(Task t : tasks) {
                tasksArrayList.add(t);
            }
            tasksIndexAdapter = new TasksIndexAdapter(this, tasksArrayList);
            ListView tasksIndexListView = (ListView) findViewById(R.id.tasksIndexListView);
            tasksIndexListView.setAdapter(tasksIndexAdapter);
        }
    }

    // Sets up the database
    private void configureDatabase() {
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .name("makelist.realm")
                .build();

        Realm.setDefaultConfiguration(realmConfiguration);
        realm = Realm.getDefaultInstance();
    }

    // Request permissions from user for reading/receiving SMS
    public void requestSmsPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS);

        int permissionCheck2 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);

        if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] { Manifest.permission.RECEIVE_SMS },
                    REQUEST_CODE_RECEIVE_SMS);
        }

        if(permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] { Manifest.permission.SEND_SMS },
                    REQUEST_CODE_SEND_SMS);
        }
    }
}
