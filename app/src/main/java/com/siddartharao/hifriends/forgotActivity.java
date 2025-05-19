package com.siddartharao.hifriends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class forgotActivity extends AppCompatActivity {

    private EditText mforgotpassword;
    private TextView mpasswordrecoverbutton;
    private TextView mgobacktologin;
    private Toast toast;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        mforgotpassword = findViewById(R.id.forgotpassword);
        mpasswordrecoverbutton = findViewById(R.id.passwordrecoverbutton);
        mgobacktologin = findViewById(R.id.gotologin);

        firebaseAuth = FirebaseAuth.getInstance();

        mgobacktologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(forgotActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mpasswordrecoverbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = mforgotpassword.getText().toString().trim();
                if (mail.isEmpty()){
                    toast.makeText(forgotActivity.this, "Enter your Email", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    firebaseAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                toast.makeText(forgotActivity.this, "Mail Sent, you can recover your password using mail", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(forgotActivity.this,LoginActivity.class));
                                finish();
                            }
                            else
                            {
                                toast.makeText(forgotActivity.this, "Email is Wrong or Account Not Exist", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }
}