package com.example.reminder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.reminder.Helper.Login;
import com.example.reminder.Helper.SessionManager;

public class MainActivity extends AppCompatActivity {
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sessionManager = new SessionManager(getApplicationContext());
        if(sessionManager.isUserLoggedIn()){
            Intent i = new Intent(getApplicationContext(),Dashboard.class);
            startActivity(i);
        }else{
            Intent i = new Intent(getApplicationContext(), Login.class);
            startActivity(i);

        }
    }
}