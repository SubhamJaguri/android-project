package com.example.reminder.Helper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.reminder.Dashboard;
import com.example.reminder.R;
import com.example.reminder.Register;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    private Button login,register;
    private EditText email,password;
    private String myToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(getApplicationContext(), Dashboard.class);
//                startActivity(i);
                if(email.getText().toString().length()>0){
                    if(password.getText().toString().length()>0){
                           doLogin(email.getText().toString(), password.getText().toString());
                    }else{
                        Toast.makeText(getApplicationContext(),"Please enter password",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Please enter email address",Toast.LENGTH_LONG).show();
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Register.class);
                startActivity(i);
            }
        });

    }
    private void doLogin(String email, String password){
        JSONObject jsonobject = new JSONObject();
        try {

            jsonobject.put("email", email);
            jsonobject.put("password", password);
            jsonobject.put("device_token", FirebaseInstanceId.getInstance().getToken());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, "https://reminder-app-api.techlious.com/user/v1/login", jsonobject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d("HELLO", response.toString());
                        try {
                            if(response.get("status").equals("success")){
                                SessionManager sessionManager = new SessionManager(getApplicationContext());
                                sessionManager.createUserSession(response.getString("accessToken"));
                                Intent i = new Intent(getApplicationContext(),Dashboard.class);
                                startActivity(i);
                                finish();
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
}