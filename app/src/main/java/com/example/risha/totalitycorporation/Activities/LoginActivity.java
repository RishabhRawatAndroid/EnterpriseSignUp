package com.example.risha.totalitycorporation.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.risha.totalitycorporation.R;

public class LoginActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    TextView forgetpassword;
    TextView signup;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //all the view which present in the activity login screen
        username=findViewById(R.id.username);
        password=findViewById(R.id.password);
        forgetpassword=findViewById(R.id.forgetpass);
        signup=findViewById(R.id.signup);
        login=findViewById(R.id.loginbtn);


    }
}
