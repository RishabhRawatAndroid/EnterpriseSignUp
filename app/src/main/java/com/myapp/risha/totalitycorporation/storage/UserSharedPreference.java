package com.myapp.risha.totalitycorporation.storage;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSharedPreference {

    private SharedPreferences preferences;
    private  SharedPreferences.Editor editor;
    private boolean first_time_open;

    private String name;
    private String email;
    private String phoneno;
    private boolean custom;
    private boolean savetocloud;
    private String loginemail;
    private boolean googlesign;
    private String photourl;
    private boolean facebooksign;

    public boolean isGooglesign() {
        return preferences.getBoolean("googlesign",false);
    }

    public void setGooglesign(boolean googlesign) {
        editor.putBoolean("googlesign",googlesign);
        editor.commit();
    }

    public String getPhotourl() {
        return preferences.getString("photourl",null);
    }

    public void setPhotourl(String photourl) {
        editor.putString("photourl",photourl);
        editor.commit();
    }

    public boolean isFacebooksign() {
        return preferences.getBoolean("facebooksign",false);
    }

    public void setFacebooksign(boolean facebooksign) {
        editor.putBoolean("facebooksign",facebooksign);
        editor.commit();
    }

    public UserSharedPreference(Context context)
    {
        preferences=context.getSharedPreferences("user",0);
        editor=preferences.edit();
    }

    public String getLoginemail() {
      return  preferences.getString("loginemail",null);
    }

    public void setLoginemail(String loginemail) {
        editor.putString("loginemail",loginemail);
        editor.commit();
    }

    public boolean isFirst_time_open() {
        return preferences.getBoolean("first_time_open",false);
    }

    public void setFirst_time_open(boolean first_time_open) {
        editor.putBoolean("first_time_open",first_time_open);
        editor.commit();
    }

    public boolean isCustom() {
        return preferences.getBoolean("custom",false);
    }

    public void setCustom(boolean custom) {
        editor.putBoolean("custom",custom);
        editor.commit();
    }

    public String getName() {
        return preferences.getString("name",null);
    }

    public void setName(String name) {
        editor.putString("name",name);
        editor.commit();
    }

    public String getEmail() {
        return preferences.getString("email",null);
    }

    public void setEmail(String email) {
        editor.putString("email",email);
        editor.commit();
    }

    public String getPhoneno() {
        return preferences.getString("phoneno",null);
    }

    public void setPhoneno(String phoneno) {
        editor.putString("phoneno",phoneno);
        editor.commit();
    }

    public boolean isSavetocloud() {
        return preferences.getBoolean("save_to_cloud",false);
    }

    public void setSavetocloud(boolean savetocloud) {
        editor.putBoolean("save_to_cloud",savetocloud);
        editor.commit();
    }

}
