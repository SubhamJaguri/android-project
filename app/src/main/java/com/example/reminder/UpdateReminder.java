package com.example.reminder;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.reminder.Helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UpdateReminder extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
    private EditText title,desc,date;
    private String my_date,token;
    private Button add;
    private SessionManager sessionManager;
    int day, month, year, hour, minute;
    int myday, myMonth, myYear, myHour, myMinute;
    private String id,mytitle,mydesc,mytime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_reminder);
        title = findViewById(R.id.title);
        desc = findViewById(R.id.desc);
        date = findViewById(R.id.date);
        add = findViewById(R.id.add);
        id = getIntent().getExtras().getString("id");
        mytitle = getIntent().getExtras().getString("title");
        mydesc = getIntent().getExtras().getString("description");
        mytime = getIntent().getExtras().getString("time");
        my_date = mytime;
        title.setText(mytitle);
        desc.setText(mydesc);
        date.setText(mytime);
        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String,String> user = sessionManager.getUserDetails();
        token = user.get(SessionManager.KEY_TOKEN);
        Log.d("TOKEN===>",token);
       // Toast.makeText(getApplicationContext(),token,Toast.LENGTH_LONG).show();
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateReminder.this, UpdateReminder.this,year, month,day);
                datePickerDialog.show();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(title.getText().toString().length()>0){
                    if(desc.getText().toString().length()>0){
                        if(my_date.length()>0){
                            submitReminder(title.getText().toString(),desc.getText().toString(),my_date);
                        }else{
                            Toast.makeText(getApplicationContext(),"Please select date",Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"Please enter description",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Please enter title",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void submitReminder(String title,String desc, String date){
        JSONObject jsonobject = new JSONObject();
        try {
            jsonobject.put("title", title);
            jsonobject.put("description", desc);
            jsonobject.put("date_time", date);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, "https://reminder-app-api.techlious.com/reminder/v1/update/"+id, jsonobject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d("HELLO", response.toString());
                        //Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_LONG).show();
                        try {
                            if(response.get("status").equals("success")){
                                Toast.makeText(getApplicationContext(),"Reminder updated successfully",Toast.LENGTH_LONG).show();
                                Intent i = new Intent(getApplicationContext(), Dashboard.class);
                                startActivity(i);
                            }else{
                                Toast.makeText(getApplicationContext(),response.get("message").toString(),Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");


                return headers;
            }



        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(jsonObjReq);
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        myYear = year;
        myday = day;
        myMonth = month;
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR);
        minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(UpdateReminder.this, UpdateReminder.this, hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        myHour = hourOfDay;
        myMinute = minute;
        date.setText(myYear+"-"+myMonth+"-"+myday+" "+myHour+":"+myMinute+":"+"00");
        my_date = myYear+"-"+myMonth+"-"+myday+" "+myHour+":"+myMinute+":"+"00";
    }
}