package com.myapp.risha.totalitycorporation;

import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.myapp.risha.totalitycorporation.Activities.LoginActivity;
import com.myapp.risha.totalitycorporation.Activities.MainActivity;
import com.myapp.risha.totalitycorporation.Classes.OpenSetting;
import com.myapp.risha.totalitycorporation.storage.UserSharedPreference;

public class SplashScreen extends AppCompatActivity {

    private View statusbar;
    private ActionBar actionBar;
    private ConstraintLayout layout;
    private UserSharedPreference userSharedPrefrence;
    private ProgressBar progressBar;
    private String TAG="RISHABH";

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
//        actionBar=this.getSupportActionBar();
//        actionBar.hide();

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
                    Log.d(TAG,"Start the 3 second delay");
                    Thread.sleep(3000);
                    //after the 3 second delay the login activity open
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG,"run on ui thread");
                            startActivity(new Intent(SplashScreen.this,LoginActivity.class));
                            overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //this method check that user is online or not while opening the app
    private boolean isOnline()
    {
        ConnectivityManager manager=(ConnectivityManager)this.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info=manager.getActiveNetworkInfo();
        if ( info != null && info.isConnectedOrConnecting()){
            return true;
        }else{
            return false;
        }
    }

    private void activityopen()
    {
            if(!userSharedPrefrence.isFirst_time_open())
            {
                Log.d(TAG,"Enter in sharedpreference");
                if(isOnline())
                {
                    Log.d(TAG,"APP is online");
                    delay();
                }
                else
                {
                    Log.d(TAG,"App is not online");
                    progressBar.setVisibility(View.INVISIBLE);
                    Snackbar.make(layout,"No Network Connection Available :(",Snackbar.LENGTH_INDEFINITE).setAction(R.string.setting,new OpenSetting()).setActionTextColor(Color.RED).show();
                }
            }
            else
            {
                Log.d(TAG,"Stored in shared preference");
                delay();
                startActivity(new Intent(SplashScreen.this,MainActivity.class));
            }
        //apply the animation when sign in activity open
        overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"on start method");
        activityopen();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"on resume method");
        activityopen();
    }
}
