package com.example.risha.totalitycorporation.storage;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSharedPreference {

    private SharedPreferences preferences;
    private  SharedPreferences.Editor editor;
    private boolean first_time_open;

    public UserSharedPreference(Context context)
    {
        preferences=context.getSharedPreferences("user",0);
        editor=preferences.edit();
    }

    public boolean isFirst_time_open() {
        return preferences.getBoolean("first_time_open",false);
    }

    public void setFirst_time_open(boolean first_time_open) {
        editor.putBoolean("first_time_open",first_time_open);
        editor.commit();
    }
}
