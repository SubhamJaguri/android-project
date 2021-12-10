package com.example.reminder.Helper;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.reminder.MainActivity;

import java.util.HashMap;



public class SessionManager {
    private static final String IS_USER_LOGGED_IN = "IsLoggedIn";
    public static final String KEY_TOKEN = "token";
    private static final String PREF_NAME = "reminder";
    int PRIVATE_MODE = 0;
    Context context;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;

    public SessionManager(Context context2) {
        this.context = context2;
        this.sharedPreferences = context2.getSharedPreferences(PREF_NAME, this.PRIVATE_MODE);
        this.editor = this.sharedPreferences.edit();
    }

    public void createUserSession(String token) {
        this.editor.putBoolean(IS_USER_LOGGED_IN, true);
        this.editor.putString("token", token);

        this.editor.commit();
    }
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();
        String str = "token";
        user.put(str, this.sharedPreferences.getString(str, null));

        return user;
    }
    public void logoutUser() {
        this.editor.clear();
        this.editor.commit();
        Intent i = new Intent(this.context, MainActivity.class);
        i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(i.FLAG_ACTIVITY_NEW_TASK);
        this.context.startActivity(i);
    }
    public boolean checkActivityLoggedIn() {
        if (!isUserLoggedIn()) {
            return false;
        }
        return true;
    }

    public void clearData() {
        this.editor.clear();
        this.editor.commit();
    }
    public boolean isUserLoggedIn() {
        return this.sharedPreferences.getBoolean(IS_USER_LOGGED_IN, false);
    }
}

