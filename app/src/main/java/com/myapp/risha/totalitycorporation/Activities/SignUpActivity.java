package com.myapp.risha.totalitycorporation.Activities;

import android.app.ProgressDialog;
import android.content.ContentValues;
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
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.util.Range;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.myapp.risha.totalitycorporation.Classes.ProfileProviderHelper;
import com.myapp.risha.totalitycorporation.R;
import com.myapp.risha.totalitycorporation.storage.UserSharedPreference;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

import static com.myapp.risha.totalitycorporation.storage.ProfileProvider.CLOUD;
import static com.myapp.risha.totalitycorporation.storage.ProfileProvider.EMAIL;
import static com.myapp.risha.totalitycorporation.storage.ProfileProvider.FACEBOOK;
import static com.myapp.risha.totalitycorporation.storage.ProfileProvider.GOOGLE;
import static com.myapp.risha.totalitycorporation.storage.ProfileProvider.IMAGE;
import static com.myapp.risha.totalitycorporation.storage.ProfileProvider.NAME;
import static com.myapp.risha.totalitycorporation.storage.ProfileProvider.PHONENO;

public class SignUpActivity extends AppCompatActivity {

    private MaterialEditText username, email, password1, password2, phoneno;
    private Button signup;
    private TextView signin, validation;
    private CheckBox checkBox1, checkBox2;
    private static boolean passwordmatch = false;
    private static boolean passwordlength = false;
    private FirebaseAuth auth;
    private ConstraintLayout layout;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        }

        username = findViewById(R.id.name);
        email = findViewById(R.id.emailid);
        password1 = findViewById(R.id.password1);
        password2 = findViewById(R.id.password2);
        phoneno = findViewById(R.id.phone);
        signup = findViewById(R.id.signupbtn);
        signin = findViewById(R.id.signtextview);
        checkBox1 = findViewById(R.id.passcheck1);
        checkBox2 = findViewById(R.id.passcheck2);
        validation = findViewById(R.id.textValid);
        layout = findViewById(R.id.mylayout);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });


        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isValidEmail(email.getText().toString().trim())) {
                    validation.setText("Valid Email");
                    validation.setTextColor(Color.GREEN);
                } else {
                    validation.setText("Invalid Email");
                    validation.setTextColor(Color.RED);
                }
            }
        });


        password1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (password1.getText().length() < 6) {
                    validation.setText("Password length must be above 6");
                    validation.setTextColor(Color.RED);
                    passwordmatch = false;
                } else {
                    validation.setText("Good Password");
                    validation.setTextColor(Color.GREEN);
                    passwordlength = true;
                }

            }
        });


        password2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (password1.getText().toString().equals(password2.getText().toString())) {
                    validation.setText("Password match");
                    validation.setTextColor(Color.GREEN);
                    passwordmatch = true;
                } else {
                    validation.setText("Password not match please check");
                    validation.setTextColor(Color.RED);
                    passwordmatch = false;
                }
            }
        });


    }

    //Email validation
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public void SignUpUser(View view) {
        String uname = username.getText().toString().trim();
        String uemail = email.getText().toString().trim();
        String upass1 = password1.getText().toString();
        String upass2 = password2.getText().toString();
        String uphone = phoneno.getText().toString().trim();

        if (TextUtils.isEmpty(uname) || TextUtils.isEmpty(uemail) || TextUtils.isEmpty(upass1) || TextUtils.isEmpty(upass2) || TextUtils.isEmpty(uphone)) {
            showdialog("Warning!", "Please fill the all details");
        } else {
            if (passwordmatch && passwordlength) {
                progressDialog = new ProgressDialog(SignUpActivity.this);
                progressDialog.setTitle("Save Information");
                progressDialog.setCancelable(false);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage("Please Wait!  ^_^");
                progressDialog.show();

                createaccount(uemail, upass1, uname, uphone);
            } else {
                showdialog("Warning!", "Your both password not match or password length not less than 6 character");
            }
        }

    }

    public void ShowpasswordCheckBox(View view) {

        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()) {
            case R.id.passcheck1:
                if (!checked)
                    password1.setTransformationMethod(new PasswordTransformationMethod());  //hide the password from the edit text
                else
                    password1.setTransformationMethod(null); //show the password from the edit text
                break;
            case R.id.passcheck2:
                if (!checked)
                    password2.setTransformationMethod(new PasswordTransformationMethod()); //hide the password from the edit text
                else
                    password2.setTransformationMethod(null); // another option show the password from the edit text
                break;
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

    private void createaccount(final String useremail, String userpassword, final String username, final String usernum) {
        //create a new account with email and password
        auth.createUserWithEmailAndPassword(useremail, userpassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            //saving the user info in shared preference
                            UserSharedPreference preference = new UserSharedPreference(SignUpActivity.this);
                            preference.setEmail(useremail);
                            preference.setName(username);
                            preference.setPhoneno(usernum);
                            preference.setCustom(true);
                            preference.setSavetocloud(true);

                            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                            ProfileProviderHelper helper=new ProfileProviderHelper(SignUpActivity.this);
                            ContentValues args = new ContentValues();
                            args.put(NAME, username);
                            args.put(EMAIL,useremail);
                            args.put(PHONENO,usernum);
                            args.put(IMAGE,"");
                            args.put(CLOUD,"1");
                            args.put(GOOGLE,"0");
                            args.put(FACEBOOK,"0");
                            helper.insert(args);
                            helper.getContacts();



                            //also saving the info into firebase database
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("USER");
                            String id = reference.push().getKey();
                            Map<String, String> map = new HashMap<>();
                            map.put("name", username);
                            map.put("email", useremail);
                            map.put("phone", usernum);
                            reference.child(id).setValue(map, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    //suppose if the firebase server down or data corruption in between then this listener will help
                                    if (databaseError == null) {
                                        try {
                                            Thread.sleep(3000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }

                                        progressDialog.dismiss();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                                finish();
                                            }
                                        });
                                    } else {
                                        Snackbar.make(layout, "Something wrong please try again later", Snackbar.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    }).start();

                } else {
                    Snackbar.make(layout, "Something wrong please try again later", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }
}
