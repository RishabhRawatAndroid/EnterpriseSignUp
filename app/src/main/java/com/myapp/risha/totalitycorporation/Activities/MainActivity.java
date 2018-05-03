package com.myapp.risha.totalitycorporation.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private UserSharedPreference preference;
    private ImageView profileimage;
    private TextView profilename;
    private TextView profilemail;
    private TextView profileno;
    private Button logoutbtn;
    private ProgressDialog progressDialog;
    private ConstraintLayout layout;
    private GoogleSignInClient mGoogleSignInClient;
    private String TAG="RISHABH MAIN";
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
            Picasso.get().load(preference.getPhotourl()).error(R.drawable.ggoogle).memoryPolicy(MemoryPolicy.NO_CACHE).into(profileimage);
            profilename.setText(preference.getName());
            profilemail.setText(preference.getEmail());

        }
        else if(preference.isFacebooksign())
        {
            Picasso.get().load(preference.getPhotourl()).error(R.drawable.com_facebook_favicon_blue).memoryPolicy(MemoryPolicy.NO_CACHE).into(profileimage);
            profilename.setText(preference.getName());
            profilemail.setText(preference.getEmail());
        }




    }

    public void LogOutUser(View view) {

        final UserSharedPreference preference=new UserSharedPreference(MainActivity.this);
        if(preference.isCustom()) {
            FirebaseAuth.getInstance().signOut();
            Log.d(TAG,"firebase log out");
        }
        else if(preference.isGooglesign())
        {

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            preference.setGooglesign(false);
                            Log.d(TAG,"google account log out");
                            Snackbar.make(layout,"Sign out account",Snackbar.LENGTH_LONG).show();
                        }
                    });
        }
        else
        {
            //from facebook logout
            LoginManager.getInstance().logOut();
        }
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
