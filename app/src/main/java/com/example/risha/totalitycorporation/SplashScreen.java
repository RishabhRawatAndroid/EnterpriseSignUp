package com.example.risha.totalitycorporation;

import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.example.risha.totalitycorporation.Activities.LoginActivity;
import com.example.risha.totalitycorporation.Activities.MainActivity;
import com.example.risha.totalitycorporation.Classes.OpenSetting;
import com.example.risha.totalitycorporation.storage.UserSharedPreference;

public class SplashScreen extends AppCompatActivity {

    private View statusbar;
    private ActionBar actionBar;
    private ConstraintLayout layout;
    private UserSharedPreference userSharedPrefrence;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        layout=findViewById(R.id.layout);
        progressBar=findViewById(R.id.progressBar);
        userSharedPrefrence=new UserSharedPreference(SplashScreen.this);

        //hiding the status bar
        statusbar=getWindow().getDecorView();
        int ui=View.SYSTEM_UI_FLAG_FULLSCREEN;
        statusbar.setSystemUiVisibility(ui);

        //hiding the action  bar
        actionBar=getSupportActionBar();
        actionBar.hide();

        //new activity open
        activityopen();

    }

    //provide some delay for opening new activity
    private void delay()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //this method check that user is online or not while opening the app
    private boolean isOnline()
    {
        ConnectivityManager manager=(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info=manager.getActiveNetworkInfo();
        return (info.isConnected() && info!=null);
    }

    private void activityopen()
    {
            if(!userSharedPrefrence.isFirst_time_open())
            {
                if(isOnline())
                {
                    delay();
                    startActivity(new Intent(SplashScreen.this,LoginActivity.class));
                }
                else
                {
                    progressBar.setVisibility(View.INVISIBLE);
                    Snackbar.make(layout,"No Network Connection Available :(",Snackbar.LENGTH_INDEFINITE).setAction(R.string.setting,new OpenSetting()).setActionTextColor(Color.RED).show();
                }
            }
            else
            {
                delay();
                startActivity(new Intent(SplashScreen.this,MainActivity.class));
            }
        //apply the animation when sign in activity open
        overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
    }

    @Override
    protected void onStart() {
        super.onStart();
        activityopen();
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityopen();
    }
}
