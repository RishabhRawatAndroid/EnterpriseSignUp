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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.myapp.risha.totalitycorporation.R;

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText gmail;
    private Button recover;
    private TextView valid;
    private ConstraintLayout layout;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        gmail=findViewById(R.id.gmail);
        recover=findViewById(R.id.recoverbtn);
        valid=findViewById(R.id.textView);
        layout=findViewById(R.id.recoverlayout);
        toolbar=findViewById(R.id.toolbar2);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_black_24dp);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //display back button in toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        gmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(isValidEmail(gmail.getText().toString().trim()))
                {
                    valid.setTextColor(Color.GREEN);
                    valid.setText("Email is Valid");
                }
                else {
                    valid.setTextColor(Color.RED);
                    valid.setText("Email is Invalid");
                }
            }
        });

        recover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(gmail.getText().toString().trim()))
                {
                    showdialog("Warning!","Please Fill the Gmail Id");
                }
                else
                {
                    sendpasswordtogmail(gmail.getText().toString().trim());
                }
            }
        });


    }

    //Email validation
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            startActivity(new Intent(ForgetPasswordActivity.this,LoginActivity.class));
            overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
        }

        return super.onOptionsItemSelected(item);
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

    private void sendpasswordtogmail(String mygmail)
    {
        FirebaseAuth auth=FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(mygmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful())
                {
                    valid.setTextColor(Color.BLACK);
                    valid.setTextSize(23);
                    valid.setText("Please check your Email id");
                    recover.setEnabled(false);
                }
                else {
                    Snackbar.make(layout,"Something wrong or you may enter wrong Gmail",Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

}
