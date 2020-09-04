package com.example.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private EditText mEmail;
    private EditText mPassword;
    private Button btnLogin;
    private TextView forgetPass;
    private TextView signup;

    private ProgressDialog dialog;
    //firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }

        dialog = new ProgressDialog(this);

        loginStart();


    }

    protected  void loginStart()
    {

        mEmail=findViewById(R.id.email_login);
        mPassword=findViewById(R.id.password_login);
        btnLogin=findViewById(R.id.btn_login);
        forgetPass=findViewById(R.id.forget_password);
        signup=findViewById(R.id.signup_reg);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=mEmail.getText().toString().trim();
                String password=mPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email))
                {
                    mEmail.setError("Email Required!");
                    return;
                }
                if(TextUtils.isEmpty(password))
                {
                    mPassword.setError("Password Required!");
                    return;
                }

                dialog.setMessage("Progressing");
                dialog.show();

                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful())
                            {
                                dialog.dismiss();
                                startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                                Toast.makeText(getApplicationContext(),"Login Successfull...",Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(),"Login Failed...",Toast.LENGTH_SHORT).show();
                            }
                    }
                });

            }
        });
        // for Registartion
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),RegistrationActivity.class));
            }
        });

        // for Forget Password

        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ResetActivity.class));
            }
        });



    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        FirebaseAuth.getInstance().signOut();

    }
}