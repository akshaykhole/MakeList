package com.akshaykhole.makelist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.akshaykhole.makelist.models.Task;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class TasksIndexActivity extends AppCompatActivity {
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_index);
        configureDatabase();
        realm = Realm.getDefaultInstance();

        realm.beginTransaction();
        Task t = realm.createObject(Task.class);
        t.setId(UUID.randomUUID().toString());
        t.setText("Fetch bread " + UUID.randomUUID().toString());
        t.setPriority("high");
        t.setAssignedBy("self");
        t.setDueDate(new Date());
        t.setComplete(Boolean.FALSE);
        realm.commitTransaction();

        RealmQuery<Task> query = realm.where(Task.class);
        RealmResults<Task> tasks = query.findAll();

        for(Task task : tasks) {
            Log.d("REALM=====>", task.getText());
        }

        realm.close();
    }

    private void configureDatabase() {
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .name("makelist.realm")
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }
}
