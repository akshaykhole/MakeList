package com.akshaykhole.makelist;

import android.content.DialogInterface;
import android.support.v4.app.FragmentManager;
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    public void createNewTask(View view) {
        FragmentManager fm = getSupportFragmentManager();
        TaskFormFragment tff = TaskFormFragment.newInstance(new Bundle());
        tff.show(fm, "task_form");
    }

    private void populateTasks() {
        RealmQuery<Task> query = realm.where(Task.class);
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
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .name("makelist.realm")
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
        realm = Realm.getDefaultInstance();
    }
}
