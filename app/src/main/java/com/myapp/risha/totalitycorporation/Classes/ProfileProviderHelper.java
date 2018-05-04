package com.myapp.risha.totalitycorporation.Classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.myapp.risha.totalitycorporation.storage.ProfileProvider;

public class ProfileProviderHelper {

    private Context context;
    public ProfileProviderHelper(Context context)
    {
        this.context=context;
    }

    public boolean insert(ContentValues values) {
        ContentValues values1 = values;
        Uri uri = context.getContentResolver().insert(ProfileProvider.CONTENT_URI, values);
        getContacts();
        if (uri != null) {
            Log.d("RISHABH", "DATA inserted");
            return true;
        } else {
            Log.d("RISHABH","DATA not inserted");
            return false;
        }
    }

    public boolean delete(String id)
    {
       // long idDeleted = context.getContentResolver().delete(ProfileProvider.CONTENT_URI,"id = ? ", new String[]{id});

        long idDeleted = context.getContentResolver().delete(ProfileProvider.CONTENT_URI,null,null);
        getContacts();
        if(idDeleted>0)
        {
            Log.d("RISHABH","DATA deleted");
            return true;
        }
        else
        {
            Log.d("RISHABH","DATA not deleted");
            return false;
        }
    }

    public void getContacts(){

        // Projection contains the columns we want
        String[] projection = new String[]{"Name" , "Email" ,"PhoneNo" , "Image" , "Cloud" , "Google" , "Facebook"};

        // Pass the URL, projection and I'll cover the other options below
        Cursor cursor = context.getContentResolver().query(ProfileProvider.CONTENT_URI, projection, null, null, null);

        String contactList = "";

        // Cycle through and display every row of data
        if(cursor.moveToFirst()){

            do{

               // String id = cursor.getString(cursor.getColumnIndex("Id"));
                String name = cursor.getString(cursor.getColumnIndex("Name"));
                String email = cursor.getString(cursor.getColumnIndex("Email"));
                String phone = cursor.getString(cursor.getColumnIndex("PhoneNo"));
                String img=cursor.getString(cursor.getColumnIndex("Image"));
                String cloud=cursor.getString(cursor.getColumnIndex("Cloud"));
                String google=cursor.getString(cursor.getColumnIndex("Google"));
                String facebook=cursor.getString(cursor.getColumnIndex("Facebook"));

                contactList = contactList + " : " + name + "\n" + email+ "\n" + phone +"\n"+ img + "\n" + cloud+ "\n" + google +"\n"+facebook;

            }while (cursor.moveToNext());

        }

        Log.d("RISHABH","DATA IS "+contactList);

    }

}
