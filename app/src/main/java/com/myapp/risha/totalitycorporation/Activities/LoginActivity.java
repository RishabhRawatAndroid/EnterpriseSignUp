package com.myapp.risha.totalitycorporation.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.myapp.risha.totalitycorporation.R;
import com.myapp.risha.totalitycorporation.storage.UserSharedPreference;

public class LoginActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    TextView forgetpassword;
    TextView signup;
    Button login;
    TextView validation;
    ConstraintLayout layout;

    private FirebaseAuth firebaseAuth;

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
                                Snackbar.make(layout, "Your password is Wrong", Snackbar.LENGTH_LONG).show();
                            } else {
                                Snackbar.make(layout, "Your email is Wrong", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
