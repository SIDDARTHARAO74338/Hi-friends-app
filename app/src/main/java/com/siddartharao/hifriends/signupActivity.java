package com.siddartharao.hifriends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signupActivity extends AppCompatActivity {

    private Button signUp;

    private TextView goLoginBtn;

    private EditText ReadName,ReadEmail,ReadPassword;

    private FirebaseAuth firebaseAuth;
    private Toast toast;

    private AlertDialog alertDialogLoading;

    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        ReadName = findViewById(R.id.ReadName);
        ReadEmail = findViewById(R.id.ReadEmail);
        ReadPassword = findViewById(R.id.ReadPassword);
        signUp = findViewById(R.id.signUpBtn);
        goLoginBtn = findViewById(R.id.goLoginBtn);

        firebaseAuth = FirebaseAuth.getInstance();

        goLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(signupActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loading();

                String name = ReadName.getText().toString().trim();
                String mail = ReadEmail.getText().toString().trim();
                String password = ReadPassword.getText().toString().trim();
                if (mail.isEmpty()||password.isEmpty()||name.isEmpty()){
                    if (alertDialogLoading.isShowing())
                        alertDialogLoading.dismiss();
                    toast.makeText(signupActivity.this, "Email and password is required", Toast.LENGTH_SHORT).show();
                }
                else if (password.length()<5)
                {
                    if (alertDialogLoading.isShowing())
                        alertDialogLoading.dismiss();
                    toast.makeText(signupActivity.this, "password should greater then 5 digits", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    firebaseAuth.createUserWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                toast.makeText(signupActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                sendEmailVerification();
                            }
                            else
                            {
                                if (alertDialogLoading.isShowing())
                                    alertDialogLoading.dismiss();
                                toast.makeText(signupActivity.this, "Failed to Registration", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });

    }

    private void sendEmailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    String emailUid = firebaseAuth.getCurrentUser().getUid();
                    //String email = ReadEmail.getText().toString();
                    //int emailLength = email.length();
                    //String userEmail = email.substring(0,emailLength - 4);

                    String name = ReadName.getText().toString();
                    database = FirebaseDatabase.getInstance().getReference();
                    database.child("users")
                            .child(emailUid)
                            .child("name")
                            .setValue(name).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(signupActivity.this, "Data saved", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(signupActivity.this, "Fail to save data", Toast.LENGTH_SHORT).show();
                                }
                            });

                    if (alertDialogLoading.isShowing())
                        alertDialogLoading.dismiss();
                    startActivity(new Intent(signupActivity.this,LoginActivity.class));
                    finish();


                    Toast.makeText(signupActivity.this, "Verification Email Sent,Verify and Log In Again", Toast.LENGTH_SHORT).show();
                    firebaseAuth.signOut();
                    finish();


                }
            });
        }
        else
        {
            if (alertDialogLoading.isShowing())
                alertDialogLoading.dismiss();
            toast.makeText(this, "Failed to Send Verification Email", Toast.LENGTH_SHORT).show();
        }
    }


    private void loading(){
        if (alertDialogLoading == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(signupActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.loading,
                    (ViewGroup) findViewById(R.id.load)
            );
            builder.setView(view);
            alertDialogLoading = builder.create();
            if (alertDialogLoading.getWindow() != null){
                alertDialogLoading.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
        }
        alertDialogLoading.setCancelable(false);
        alertDialogLoading.show();
    }

}