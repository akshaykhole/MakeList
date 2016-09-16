package com.akshaykhole.makelist.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.akshaykhole.makelist.R;
import com.akshaykhole.makelist.models.Task;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by akshay on 6/26/16.
 */

public class TasksIndexAdapter extends ArrayAdapter<Task> {
    private Realm realm;

    public TasksIndexAdapter(Context context, ArrayList<Task> tasks) {
        super(context, 0, tasks);
    }

    @Override
    public View getView(int position, View convertedView, ViewGroup parent) {
        realm = Realm.getDefaultInstance();
        RealmQuery<Task> query = realm.where(Task.class);
        RealmResults<Task> tasks = query.findAll();

        Task task = tasks.get(position);

        if(convertedView == null) {
            convertedView = LayoutInflater.from(getContext()).inflate(R.layout.tasks_index_row,
                    parent, false);
        }

        TextView taskDescription = (TextView) convertedView.findViewById(
                R.id.tasks_index_task_description
        );

        TextView taskDueIn = (TextView) convertedView.findViewById(R.id.lvTaskDueIn);
        TextView taskAssignedBy = (TextView) convertedView.findViewById(R.id.lvTaskAssignedBy);
        TextView taskPriority = (TextView) convertedView.findViewById(R.id.lvTaskPriority);
        TextView taskId = (TextView) convertedView.findViewById(R.id.taskIndexTaskId);

        taskDescription.setText(task.getText());
        taskDueIn.setText("Due in " + (daysBetween(new Date(), task.getDueDate()) + 1) + " days");
        taskAssignedBy.setText("Assigned By: " + task.getAssignedBy());
        taskPriority.setText(task.getPriority());
        taskId.setText(task.getId());

        return convertedView;
    }

    public int daysBetween(Date d1, Date d2){
        return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }
}
