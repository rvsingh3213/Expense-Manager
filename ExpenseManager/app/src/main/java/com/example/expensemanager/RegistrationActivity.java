package com.example.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

public class RegistrationActivity extends AppCompatActivity {
    private static final String TAG=RegistrationActivity.class.getSimpleName();
    private EditText regEmail;
    private EditText regPassword;
    private Button btnReg;
    private TextView singin_here;

    private ProgressDialog mDialog;
    //Firebase
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mAuth=FirebaseAuth.getInstance();
        mDialog=new ProgressDialog(this);

        registration();
    }   // oncreate() Ends
    private void registration()
    {   regEmail=findViewById(R.id.email_reg);
        regPassword=findViewById(R.id.password_reg);
        btnReg=findViewById(R.id.btn_reg);
        singin_here=findViewById(R.id.signin_here);

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=regEmail.getText().toString().trim();
                String password=regPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email))
                {
                    regEmail.setError("Email required!");
                    return;
                }
                if(TextUtils.isEmpty(password))
                {
                    regPassword.setError("Password Required!");
                    return;
                }
                // If password Length is <6 then also wont complete
                //before will show progress Dialog
                mDialog.setMessage("Processing");
                mDialog.show();
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {   mDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Registation Complete",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),HomeActivity.class));

                        }
                      else {

                            mDialog.dismiss();
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Registration Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        singin_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });


    }
}