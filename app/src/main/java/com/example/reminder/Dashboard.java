package com.example.reminder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.reminder.Helper.Login;
import com.example.reminder.Helper.SessionManager;
import com.example.reminder.List.ReminderList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Dashboard extends AppCompatActivity {
    private Button add;
    private ArrayList<ReminderList>reminderLists = new ArrayList<>();
    private RecyclerView my_list;
    private SessionManager sessionManager;
    private String token;
    private Button logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        add = findViewById(R.id.add);
        my_list = findViewById(R.id.my_list);
        my_list.setHasFixedSize(true);
        my_list.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String,String>user = sessionManager.getUserDetails();
        token = user.get(SessionManager.KEY_TOKEN);
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logoutUser();
            }
        });
        getReminders(token);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),AddReminder.class);
                startActivity(i);
            }
        });


    }
    private void getReminders(String token) {
        StringRequest request = new StringRequest(Request.Method.GET, "https://reminder-app-api.techlious.com/reminder/v1/my-reminders", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("HELLO", response);
               // Toast.makeText(getApplicationContext(),"Res"+response,Toast.LENGTH_LONG).show();
                try {
                    JSONArray array = new JSONArray(response);

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        ReminderList rl = new ReminderList(obj.getString("id"), obj.getString("title"), obj.getString("description"), obj.getString("date_time"));
                        reminderLists.add(rl);
                    }
                    ReminderAdapter rl = new ReminderAdapter(reminderLists,getApplicationContext());
                    my_list.setAdapter(rl);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token);

                return headers;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }
    final class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.MyViewHolder>{
        ArrayList<ReminderList>reminderLists;
        Context context;

        public ReminderAdapter(ArrayList<ReminderList> reminderLists, Context context) {
            this.reminderLists = reminderLists;
            this.context = context;
        }

        @NonNull

        @Override
        public ReminderAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.inner_reminder_list,parent,false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ReminderAdapter.MyViewHolder holder, int position) {
            ReminderList rl = reminderLists.get(position);
            holder.description.setText(rl.getDesc());
            holder.title.setText(rl.getTitle());
            holder.time.setText(rl.getTime());
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteReminder(rl.getId());
                }
            });
            holder.update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(),UpdateReminder.class);
                    i.putExtra("id",rl.getId());
                    i.putExtra("title",rl.getTitle());
                    i.putExtra("description",rl.getDesc());
                    i.putExtra("time",rl.getTime());
                    i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(i.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return reminderLists.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView title,description,time;
            Button delete,update;
            public MyViewHolder(View itemView) {
                super(itemView);
                time = itemView.findViewById(R.id.time);
                title = itemView.findViewById(R.id.title);
                description = itemView.findViewById(R.id.description);
                delete = itemView.findViewById(R.id.delete);
                update = itemView.findViewById(R.id.update);
            }
        }
    }
    private void deleteReminder(String id){
        StringRequest request = new StringRequest(Request.Method.GET, "https://reminder-app-api.techlious.com/reminder/v1/delete/"+id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(),"Reminder Deteted",Toast.LENGTH_LONG).show();
                getReminders(token);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getReminders(token);
    }
}