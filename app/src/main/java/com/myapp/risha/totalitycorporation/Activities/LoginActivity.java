package com.myapp.risha.totalitycorporation.Activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.myapp.risha.totalitycorporation.Classes.OpenSetting;
import com.myapp.risha.totalitycorporation.Classes.ProfileProviderHelper;
import com.myapp.risha.totalitycorporation.R;
import com.myapp.risha.totalitycorporation.storage.ProfileProvider;
import com.myapp.risha.totalitycorporation.storage.UserSharedPreference;

import static com.myapp.risha.totalitycorporation.storage.ProfileProvider.CLOUD;
import static com.myapp.risha.totalitycorporation.storage.ProfileProvider.EMAIL;
import static com.myapp.risha.totalitycorporation.storage.ProfileProvider.FACEBOOK;
import static com.myapp.risha.totalitycorporation.storage.ProfileProvider.GOOGLE;
import static com.myapp.risha.totalitycorporation.storage.ProfileProvider.IMAGE;
import static com.myapp.risha.totalitycorporation.storage.ProfileProvider.NAME;
import static com.myapp.risha.totalitycorporation.storage.ProfileProvider.PHONENO;


public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private TextView forgetpassword;
    private TextView signup;
    private Button login;
    private Button googlesignin;
    private LoginButton facebooksignin;
    private TextView validation;
    private ConstraintLayout layout;
    private static final int GOOGLE_SIGN_IN=255;

    //custom sign in approach
    private FirebaseAuth firebaseAuth;

    //google sign in approach
    private GoogleSignInClient client;

    //facebook sign in approach
    private CallbackManager manager;
    private AccessTokenTracker tracker;
    private ProfileTracker profileTracker;
    private FacebookCallback<LoginResult> facebookCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //adding firebase for custom sign in option
        firebaseAuth = FirebaseAuth.getInstance();

        //checking if user is already sign in or not
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            finish();
        }


        //all the view which present in the activity login screen
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        forgetpassword = findViewById(R.id.forgetpass);
        signup = findViewById(R.id.signup);
        login = findViewById(R.id.loginbtn);
        validation = findViewById(R.id.validate);
        layout = findViewById(R.id.layout);
        googlesignin=findViewById(R.id.google);
        facebooksignin=findViewById(R.id.facebook);

        //this is the email edit text listener because when user enter simultaneously show the that email is valid or not
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String email = username.getText().toString().trim();
                if (email.length() > 4) {
                    if (isValidEmail(email)) {
                        validation.setText("Valid Email");
                        validation.setTextColor(Color.GREEN);
                    } else {
                        validation.setText("Invalid Email");
                        validation.setTextColor(Color.RED);
                    }
                }
            }
        });


        //if user have not an account then user obviously go to the sign up option
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
                overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
            }
        });


        //if user forget the password then user click the forget password textview
        forgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,ForgetPasswordActivity.class));
                overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
            }
        });


        facebooksignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                manager=CallbackManager.Factory.create();

                tracker=new AccessTokenTracker() {
                    @Override
                    protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

                    }
                };

                profileTracker=new ProfileTracker() {
                    @Override
                    protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                        showfacebookprofile(currentProfile);
                    }
                };

                tracker.startTracking();
                profileTracker.startTracking();

                facebookCallback=new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Profile profile=Profile.getCurrentProfile();
                        showfacebookprofile(profile);

                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    }

                    @Override
                    public void onCancel() {
                        Snackbar.make(layout,"You cancel the facebook login",Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Snackbar.make(layout,"No Network Connection Available or No Facebook Account :(",Snackbar.LENGTH_LONG).setAction(R.string.setting,new OpenSetting()).setActionTextColor(Color.RED).show();
                    }
                };

                facebooksignin.setReadPermissions("email");
                facebooksignin.registerCallback(manager,facebookCallback);

            }
        });


    }


    //Email validation
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public void SignInUser(View view) {
        String uname = username.getText().toString().trim();
        String upassword = password.getText().toString();
        if (TextUtils.isEmpty(uname) || TextUtils.isEmpty(upassword)) {
            showdialog("Error", "Email or Password may not be blank");
        } else {
            if (isValidEmail(uname)) {
                //sign in user
                signinuser(uname,upassword);
            } else {
                showdialog("Alert!", "Email Id is not valid please see properly");
            }
        }

    }

    private void showdialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title).setMessage(message).setIcon(R.drawable.ic_warning_black_24dp)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    //in this listener we check if the user enter the wrong email or may be password then show the snackbar
    private void signinuser(final String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    //setsave to cloud means user is already a registered member so in main activity we fetch the user information
                    UserSharedPreference preference=new UserSharedPreference(LoginActivity.this);
                    preference.setLoginemail(email);
                    preference.setSavetocloud(true);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();
                } else {
                    firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                            if (task.isSuccessful()) {
                                Snackbar.make(layout, "Your password or Email may be Wrong", Snackbar.LENGTH_LONG).show();
                            } else {
                                Snackbar.make(layout,"No Network Connection Available :(",Snackbar.LENGTH_LONG).setAction(R.string.setting,new OpenSetting()).setActionTextColor(Color.RED).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else {
            manager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acct = completedTask.getResult(ApiException.class);
            if (acct != null) {
                String personName = acct.getDisplayName();
                String personEmail = acct.getEmail();
                String personPhoto = acct.getPhotoUrl().toString();

                Log.d("RISHABH Google photo",personPhoto);
                UserSharedPreference preference=new UserSharedPreference(this);
                preference.setEmail(personEmail);
                preference.setName(personName);
                preference.setPhotourl(personPhoto);
                preference.setGooglesign(true);

                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                savetosqlite(personName,personEmail,"",personPhoto,"0","1","0");
                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                FirebaseUser user=firebaseAuth.getCurrentUser();
                startActivity(new Intent(LoginActivity.this,MainActivity.class));

            }

            Snackbar.make(layout,"Please try again later with sign in google",Snackbar.LENGTH_LONG).show();

        } catch (ApiException e) {
            Snackbar.make(layout,"Please try again later with sign in google",Snackbar.LENGTH_LONG).show();
        }
    }

    public void Googlesignin(View view) {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        client = GoogleSignIn.getClient(this, gso);

        Intent signInIntent = client.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }


    private void showfacebookprofile(Profile currentProfile) {
        if(currentProfile!=null)
        {
            String id=currentProfile.getId();
            UserSharedPreference preference=new UserSharedPreference(this);
            preference.setName(currentProfile.getName());
            preference.setEmail("ID :"+currentProfile.getId());
            preference.setPhoneno("");
            Log.d("RISHABH",currentProfile.getProfilePictureUri(300,300).toString());
            preference.setPhotourl(currentProfile.getProfilePictureUri(300,300).toString());
            Log.d("RISHABH",currentProfile.getLinkUri().toString());
            Log.d("RISHABH",currentProfile.getProfilePictureUri(300,300).toString());
            preference.setFacebooksign(true);

            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            savetosqlite(currentProfile.getName(),"",currentProfile.getId(),currentProfile.getProfilePictureUri(300,300).toString(),"0","0","1");
            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(tracker!=null && profileTracker!=null)
        {
            tracker.stopTracking();
            profileTracker.stopTracking();
        }
    }

    public void savetosqlite(String name,String email,String phone,String photo,String cloud,String google,String facebook)
    {
        ProfileProviderHelper helper=new ProfileProviderHelper(LoginActivity.this);
        ContentValues args = new ContentValues();
        args.put(NAME, name);
        args.put(EMAIL, email);
        args.put(PHONENO,phone);
        args.put(IMAGE,photo);
        args.put(CLOUD,cloud);
        args.put(GOOGLE,google);
        args.put(FACEBOOK,facebook);
        helper.insert(args);
        helper.getContacts();

    }
}
