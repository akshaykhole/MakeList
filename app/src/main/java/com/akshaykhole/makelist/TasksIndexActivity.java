package com.akshaykhole.makelist;

import android.content.DialogInterface;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.akshaykhole.makelist.adapters.TasksIndexAdapter;
import com.akshaykhole.makelist.models.Task;

import java.util.ArrayList;
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    public void createNewTask(View view) {
        FragmentManager fm = getSupportFragmentManager();
        TaskFormFragment tff = TaskFormFragment.newInstance();
        tff.show(fm, "task_form");
    }

    private void createDummyTask() {
        realm.beginTransaction();

        Task t = realm.createObject(Task.class);
        t.setId(UUID.randomUUID().toString());
        t.setText("Fetch bread " + UUID.randomUUID().toString());
        t.setPriority("low");
        t.setAssignedBy("self");
        t.setDueDate(new Date());
        t.setComplete(Boolean.FALSE);
        realm.commitTransaction();

        RealmQuery<Task> query = realm.where(Task.class);
        RealmResults<Task> tasks = query.findAll();
        for(Task task : tasks) {
            Log.d("REALM=====>", task.getText());
        }
    }

    private void populateTasks() {
        RealmQuery<Task> query = realm.where(Task.class);
        RealmResults<Task> tasks = query.findAll();

        ArrayList<Task> tasksArrayList =  new ArrayList<Task>();

        for(Task t : tasks) {
            tasksArrayList.add(t);
        }

        tasksIndexAdapter = new TasksIndexAdapter(this, tasksArrayList);
        ListView tasksIndexListView = (ListView) findViewById(R.id.tasksIndexListView);
        tasksIndexListView.setAdapter(tasksIndexAdapter);
    }

    private void configureDatabase() {
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .name("makelist.realm")
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
        realm = Realm.getDefaultInstance();
    }
}
