package com.example.reminder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.reminder.Helper.Login;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    private Button login, register;
    private EditText name, email, password;
    final String[] token = new String[1];
    String status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Login.class);
                startActivity(i);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(getApplicationContext(), Dashboard.class);
//                startActivity(i);
                if (name.getText().toString().length() > 0) {
                    if (email.getText().toString().length() > 0) {
                        if (password.getText().toString().length() > 0) {
                            doSignup(email.getText().toString(), name.getText().toString(), password.getText().toString());
                        } else {
                            Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Please email address", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter name", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void doSignup(String email, String name, String pass) {
        JSONObject jsonobject = new JSONObject();
        try {
            jsonobject.put("name", name);
            jsonobject.put("email", email);
            jsonobject.put("password", pass);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, "https://reminder-app-api.techlious.com/user/v1/register", jsonobject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d("HELLO", response.toString());
                        try {
                            if(response.get("status").equals("success")){
                                Toast.makeText(getApplicationContext(),"Account created Please Login",Toast.LENGTH_LONG).show();
                                Intent i = new Intent(getApplicationContext(),Login.class);
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

}