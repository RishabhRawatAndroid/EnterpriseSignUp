package com.myapp.risha.totalitycorporation.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.myapp.risha.totalitycorporation.R;
import com.myapp.risha.totalitycorporation.storage.UserSharedPreference;

public class MainActivity extends AppCompatActivity {

    private UserSharedPreference preference;
    private ImageView profileimage;
    private TextView profilename;
    private TextView profilemail;
    private TextView profileno;
    private Button logoutbtn;
    private ProgressDialog progressDialog;
    private ConstraintLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preference=new UserSharedPreference(this);
        profileimage=findViewById(R.id.profile);
        profilename=findViewById(R.id.user_name);
        profilemail=findViewById(R.id.user_email);
        profileno=findViewById(R.id.user_phoneno);
        logoutbtn=findViewById(R.id.logout_btn);
        layout=findViewById(R.id.main_layout);

        if (preference.isSavetocloud() && !preference.isCustom())
        {

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("Fetching from the server");
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Please Wait!  ^_^");
            progressDialog.show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("USER");
                    Query query=reference.orderByChild("email").equalTo(preference.getLoginemail());
                    query.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            String name=String.valueOf(dataSnapshot.child("name").getValue());
                            String email=String .valueOf(dataSnapshot.child("email").getValue());
                            String phone=String.valueOf(dataSnapshot.child("phone").getValue());

                            //setting all the value from the database
                            profilename.setText(name);
                            profilemail.setText(email);
                            profileno.setText(phone);

                            //puting into shared preference
                            preference.setName(name);
                            preference.setEmail(email);
                            preference.setPhoneno(phone);
                            preference.setCustom(true);

                            progressDialog.dismiss();
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }).start();
        }
        else if(!preference.isSavetocloud() && preference.isCustom())
        {
            profilename.setText(preference.getName());
            profilemail.setText(preference.getEmail());
            profileno.setText(preference.getPhoneno());
        }
        else if(preference.isSavetocloud() && preference.isCustom())
        {
            profilename.setText(preference.getName());
            profilemail.setText(preference.getEmail());
            profileno.setText(preference.getPhoneno());
        }

        else if(preference.isGooglesign())
        {
            Glide.with(MainActivity.this).load(preference.getPhotourl()).into(profileimage);
            profilename.setText(preference.getName());
            profilemail.setText(preference.getEmail());

        }



    }

    public void LogOutUser(View view) {

        FirebaseAuth.getInstance().signOut();

        startActivity(new Intent(MainActivity.this,LoginActivity.class));
        overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
        finish();

//        FirebaseAuth.AuthStateListener authStateListener=new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user=firebaseAuth.getCurrentUser();
//                if(user==null)
//                {
//                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
//                    overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
//                    finish();
//                }
//                else
//                {
//                    Snackbar.make(layout,"Something error please try again later",Snackbar.LENGTH_LONG).show();
//                }
//            }
//        };
    }
}