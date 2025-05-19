package com.siddartharao.hifriends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button loginBtn;
    private TextView backSignUp,forgot;
    private EditText CheckEmail,CheckPassword;
    private FirebaseAuth firebaseAuth;
    private Toast toast;
    private AlertDialog alertDialogLoading;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        CheckEmail = findViewById(R.id.CheckEmail);
        CheckPassword = findViewById(R.id.CheckPassword);

        firebaseAuth = FirebaseAuth.getInstance();

        forgot = findViewById(R.id.forgot);
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),forgotActivity.class));
                finish();
            }
        });

        backSignUp = findViewById(R.id.goSignUp);
        backSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),signupActivity.class));
                finish();
            }
        });

        loginBtn = findViewById(R.id.logInBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loading();

                String email = CheckEmail.getText().toString();
                String password = CheckPassword.getText().toString();

                if (email.isEmpty()||password.isEmpty()){
                    if (alertDialogLoading.isShowing())
                        alertDialogLoading.dismiss();
                    toast.makeText(LoginActivity.this, "Email and password is required", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //login the user
                    firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                chickmailverfication();
                            }
                            else
                            {
                                toast.makeText(LoginActivity.this, "Account Doesn't Exist", Toast.LENGTH_SHORT).show();
                                if (alertDialogLoading.isShowing())
                                    alertDialogLoading.dismiss();
                            }
                        }
                    });
                }
            }
        });

    }

    private void chickmailverfication(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser.isEmailVerified()==true){
            toast.makeText(this, "Logged In", Toast.LENGTH_SHORT).show();
            finish();
            if (alertDialogLoading.isShowing())
                alertDialogLoading.dismiss();
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();

        }
        else
        {
            if (alertDialogLoading.isShowing())
                alertDialogLoading.dismiss();
            toast.makeText(this, "Verify your mail first", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }
    }

    private void loading(){
        if (alertDialogLoading == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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