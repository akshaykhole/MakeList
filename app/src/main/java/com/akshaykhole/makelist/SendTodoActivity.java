package com.akshaykhole.makelist;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

public class SendTodoActivity extends AppCompatActivity {
    static final int REQUEST_SELECT_PHONE_NUMBER = 1;
    String selectedContactNumber = new String();
    private Spinner prioritySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_send_todo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set priority dropdown
        String[] priorityListItems = new String[] { "High", "Medium", "Low" };
        prioritySpinner = (Spinner) findViewById(R.id.sendTaskFormPriorityDropdown);
        ArrayAdapter<String> priorityArrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, priorityListItems);
        prioritySpinner.setAdapter(priorityArrayAdapter);
        // end set priority dropdown

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Sending TODO to: " + selectedContactNumber , Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                // Fetch values
                EditText et = (EditText) findViewById(R.id.textViewSendTodoTaskDescription);
                Button contactNumber = (Button) findViewById(R.id.btn_select_contact);
                Button selectedDate = (Button) findViewById(R.id.btnSendTodoFormSelectDate);
                Button selectedTime = (Button) findViewById(R.id.btnSendTodoSelectTime);
                String selectedPriority = prioritySpinner.getSelectedItem().toString();

                try {
                    // Build JSON
                    JSONObject message = new JSONObject();
                    JSONObject innerObject = new JSONObject();

                    innerObject.put("text", et.getText().toString());
                    innerObject.put("priority", selectedPriority);
                    innerObject.put("dueDate", selectedDate.getText().toString());

                    message.put("makelist", innerObject);

                    // Send SMS
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(selectedContactNumber,
                                                null,
                                                message.toString(),
                                                null,
                                                null);
                    // Go to main page
                    Intent intent = new Intent(getApplicationContext(), TasksIndexActivity.class);
                    startActivity(intent);
                } catch (JSONException e) {
                    Log.e("SMS sending failed", e.getMessage().toString());
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void selectContact(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);

        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_SELECT_PHONE_NUMBER);
        }
    }

    public void selectDate(View view) {
        DialogFragment dateFragment = new DatePickerFragment();
        dateFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void selectTime(View view) {
        DialogFragment timeFragment = new TimePickerFragment();
        timeFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_PHONE_NUMBER && resultCode == RESULT_OK) {
            // Get the URI and query the content provider for the phone number
            Uri contactUri = data.getData();
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
            Cursor cursor = getContentResolver().query(contactUri, projection,
                    null, null, null);
            // If the cursor returned is valid, get the phone number
            if (cursor != null && cursor.moveToFirst()) {
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                this.selectedContactNumber = cursor.getString(numberIndex);
                Button sendTodoSelectContact = (Button) findViewById(R.id.btn_select_contact);
                sendTodoSelectContact.setText(selectedContactNumber);
            }
        }
    }

}
