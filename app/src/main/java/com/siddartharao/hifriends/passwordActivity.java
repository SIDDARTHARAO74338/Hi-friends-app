package com.siddartharao.hifriends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class passwordActivity extends AppCompatActivity {

    TextView getCode, alertState;
    Button id1, id2, id3, id4, id5, id6, id7, id8, id9, id0, remove, changeBtn, cancel, unlockBtn;
    ImageView go,LockLogout;
    ImageButton top_text;
    View changeLayout, unlock,mainPage;
    EditText oldPasscode, changePasscode, pass_view, unlockCode;
    ImageView dot1, dot2, dot3, dot4;
    int count = 3;
    FirebaseAuth firebaseAuth;
    String user;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser()==null){
            startActivity(new Intent(passwordActivity.this,LoginActivity.class));
            finish();
        }else{
            String email = firebaseAuth.getCurrentUser().getUid();
            //int emailLength = email.length();
            //String userEmail = email.substring(0,emailLength - 4);

            DatabaseReference nameRef = FirebaseDatabase.getInstance().getReference().child("users").child(email);
            nameRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()){
                       user = snapshot.child("name").getValue().toString();
                    } else {
                        Toast.makeText(passwordActivity.this, "username not found", Toast.LENGTH_SHORT).show();
                        //startActivity(new Intent(getApplicationContext(), settingsActivity.class));
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        top_text = findViewById(R.id.top_text);
        pass_view = findViewById(R.id.pass_view);
        getCode = findViewById(R.id.getCode);
        LockLogout = findViewById(R.id.LockLogout);
        mainPage = findViewById(R.id.mainPage);

        id1 = findViewById(R.id.id1);
        id2 = findViewById(R.id.id2);
        id3 = findViewById(R.id.id3);
        id4 = findViewById(R.id.id4);
        id5 = findViewById(R.id.id5);
        id6 = findViewById(R.id.id6);
        id7 = findViewById(R.id.id7);
        id8 = findViewById(R.id.id8);
        id9 = findViewById(R.id.id9);
        id0 = findViewById(R.id.id0);
        remove = findViewById(R.id.remove);
        go = findViewById(R.id.go);

        changeLayout = findViewById(R.id.changeLayout);
        unlock = findViewById(R.id.unlock);
        changeBtn = findViewById(R.id.changeBtn);
        cancel = findViewById(R.id.cancel);
        oldPasscode = findViewById(R.id.oldPasscode);
        changePasscode = findViewById(R.id.changePasscode);

        unlockCode = findViewById(R.id.unlockCode);
        unlockBtn = findViewById(R.id.unlockBtn);
        alertState = findViewById(R.id.alertState);

        dot1 = findViewById(R.id.dot1);
        dot2 = findViewById(R.id.dot2);
        dot3 = findViewById(R.id.dot3);
        dot4 = findViewById(R.id.dot4);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mainPage.setVisibility(View.GONE);
            }
        },2000);

        id1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = pass_view.getText().toString();
                pass_view.setText(id + "1");

            }
        });

        id2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = pass_view.getText().toString();
                pass_view.setText(id + "2");

            }
        });

        id3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = pass_view.getText().toString();
                pass_view.setText(id + "3");

            }
        });

        id4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = pass_view.getText().toString();
                pass_view.setText(id + "4");

            }
        });

        id5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = pass_view.getText().toString();
                pass_view.setText(id + "5");

            }
        });

        id6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = pass_view.getText().toString();
                pass_view.setText(id + "6");

            }
        });

        id7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = pass_view.getText().toString();
                pass_view.setText(id + "7");

            }
        });

        id8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = pass_view.getText().toString();
                pass_view.setText(id + "8");

            }
        });

        id9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = pass_view.getText().toString();
                pass_view.setText(id + "9");

            }
        });

        id0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = pass_view.getText().toString();
                pass_view.setText(id + "0");

            }
        });

        pass_view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (pass_view.length() == 0) {
                    dot1.setBackgroundResource(R.drawable.background_done_button);
                    dot2.setBackgroundResource(R.drawable.background_done_button);
                    dot3.setBackgroundResource(R.drawable.background_done_button);
                    dot4.setBackgroundResource(R.drawable.background_done_button);
                } else if (pass_view.length() == 1) {
                    dot1.setBackgroundResource(R.drawable.passcode_dot);
                    dot2.setBackgroundResource(R.drawable.background_done_button);
                    dot3.setBackgroundResource(R.drawable.background_done_button);
                    dot4.setBackgroundResource(R.drawable.background_done_button);
                } else if (pass_view.length() == 2) {
                    dot1.setBackgroundResource(R.drawable.passcode_dot);
                    dot2.setBackgroundResource(R.drawable.passcode_dot);
                    dot3.setBackgroundResource(R.drawable.background_done_button);
                    dot4.setBackgroundResource(R.drawable.background_done_button);
                } else if (pass_view.length() == 3) {
                    dot1.setBackgroundResource(R.drawable.passcode_dot);
                    dot2.setBackgroundResource(R.drawable.passcode_dot);
                    dot3.setBackgroundResource(R.drawable.passcode_dot);
                    dot4.setBackgroundResource(R.drawable.background_done_button);
                } else if (pass_view.length() == 4) {
                    dot1.setBackgroundResource(R.drawable.passcode_dot);
                    dot2.setBackgroundResource(R.drawable.passcode_dot);
                    dot3.setBackgroundResource(R.drawable.passcode_dot);
                    dot4.setBackgroundResource(R.drawable.passcode_dot);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*String oldStr = pass_view.getText().toString();
                int cursorPos2 = pass_view.getSelectionStart();
                String leftStr = oldStr.substring(0,cursorPos2);
                String rightStr = oldStr.substring(cursorPos2);
                pass_view.setText(String.format("%s%s%s",leftS));*/
                pass_view.setSelection(pass_view.getText().length());
                int cursorPos = pass_view.getSelectionStart();
                int textLen = pass_view.getText().length();
                if (cursorPos != 0 && textLen != 0) {
                    SpannableStringBuilder selection = (SpannableStringBuilder) pass_view.getText();
                    selection.replace(cursorPos - 1, cursorPos, "");
                    pass_view.setText(selection);
                    pass_view.setSelection(cursorPos - 1);
                }

            }
        });

        alertState.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (alertState.getText().toString().equals("lock")) {
                    unlock.setVisibility(View.VISIBLE);
                    id1.setEnabled(false);
                    id2.setEnabled(false);
                    id3.setEnabled(false);
                    id4.setEnabled(false);
                    id5.setEnabled(false);
                    id6.setEnabled(false);
                    id7.setEnabled(false);
                    id8.setEnabled(false);
                    id9.setEnabled(false);
                    id0.setEnabled(false);
                    remove.setEnabled(false);
                    go.setEnabled(false);
                } else if (alertState.getText().toString().equals("unlock")) {
                    unlock.setVisibility(View.GONE);
                    id1.setEnabled(true);
                    id2.setEnabled(true);
                    id3.setEnabled(true);
                    id4.setEnabled(true);
                    id5.setEnabled(true);
                    id6.setEnabled(true);
                    id7.setEnabled(true);
                    id8.setEnabled(true);
                    id9.setEnabled(true);
                    id0.setEnabled(true);
                    remove.setEnabled(true);
                    go.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        getCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String mainCode = getCode.getText().toString();
                if (mainCode.isEmpty()) {
                    oldPasscode.setVisibility(View.GONE);
                    changeLayout.setVisibility(View.VISIBLE);
                    cancel.setVisibility(View.GONE);
                    id1.setEnabled(false);
                    id2.setEnabled(false);
                    id3.setEnabled(false);
                    id4.setEnabled(false);
                    id5.setEnabled(false);
                    id6.setEnabled(false);
                    id7.setEnabled(false);
                    id8.setEnabled(false);
                    id9.setEnabled(false);
                    id0.setEnabled(false);
                    remove.setEnabled(false);
                    go.setEnabled(false);
                } else {
                    changeLayout.setVisibility(View.GONE);
                    oldPasscode.setVisibility(View.VISIBLE);
                    cancel.setVisibility(View.VISIBLE);
                    id1.setEnabled(true);
                    id2.setEnabled(true);
                    id3.setEnabled(true);
                    id4.setEnabled(true);
                    id5.setEnabled(true);
                    id6.setEnabled(true);
                    id7.setEnabled(true);
                    id8.setEnabled(true);
                    id9.setEnabled(true);
                    id0.setEnabled(true);
                    remove.setEnabled(true);
                    go.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pw = getCode.getText().toString();
                String PV = pass_view.getText().toString();

                if (PV.isEmpty() || pass_view.length() != 4) {
                    Toast.makeText(passwordActivity.this, "Enter code", Toast.LENGTH_SHORT).show();
                } else {
                    if (PV.equals(pw)) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    } else {
                        count--;
                        pass_view.setText("");

                        //----------------------------------

                        dot1.setBackgroundResource(R.drawable.background_red_button);
                        dot2.setBackgroundResource(R.drawable.background_red_button);
                        dot3.setBackgroundResource(R.drawable.background_red_button);
                        dot4.setBackgroundResource(R.drawable.background_red_button);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dot1.setBackgroundResource(R.drawable.background_done_button);
                                dot2.setBackgroundResource(R.drawable.background_done_button);
                                dot3.setBackgroundResource(R.drawable.background_done_button);
                                dot4.setBackgroundResource(R.drawable.background_done_button);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dot1.setBackgroundResource(R.drawable.background_red_button);
                                        dot2.setBackgroundResource(R.drawable.background_red_button);
                                        dot3.setBackgroundResource(R.drawable.background_red_button);
                                        dot4.setBackgroundResource(R.drawable.background_red_button);

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                dot1.setBackgroundResource(R.drawable.background_done_button);
                                                dot2.setBackgroundResource(R.drawable.background_done_button);
                                                dot3.setBackgroundResource(R.drawable.background_done_button);
                                                dot4.setBackgroundResource(R.drawable.background_done_button);

                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        dot1.setBackgroundResource(R.drawable.background_red_button);
                                                        dot2.setBackgroundResource(R.drawable.background_red_button);
                                                        dot3.setBackgroundResource(R.drawable.background_red_button);
                                                        dot4.setBackgroundResource(R.drawable.background_red_button);

                                                        new Handler().postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                dot1.setBackgroundResource(R.drawable.background_done_button);
                                                                dot2.setBackgroundResource(R.drawable.background_done_button);
                                                                dot3.setBackgroundResource(R.drawable.background_done_button);
                                                                dot4.setBackgroundResource(R.drawable.background_done_button);
                                                            }
                                                        }, 200);

                                                    }
                                                }, 200);

                                            }
                                        }, 200);

                                    }
                                }, 200);

                            }
                        }, 200);

                        //----------------------------------

                    /*Toast.makeText(passcodeActivity.this, "you have " + count + " chances left",
                            Toast.LENGTH_SHORT).show();*/

                        if (count == 0) {
                            id1.setEnabled(false);
                            id2.setEnabled(false);
                            id3.setEnabled(false);
                            id4.setEnabled(false);
                            id5.setEnabled(false);
                            id6.setEnabled(false);
                            id7.setEnabled(false);
                            id8.setEnabled(false);
                            id9.setEnabled(false);
                            id0.setEnabled(false);
                            remove.setEnabled(false);
                            go.setEnabled(false);
                            unlock.setVisibility(View.VISIBLE);

                            Notification();

                            String b = "lock";
                            SharedPreferences shrd = getSharedPreferences("alert", MODE_PRIVATE);
                            SharedPreferences.Editor editor = shrd.edit();
                            editor.putString("lock", b);
                            editor.apply();
                            alertState.setText(b);

                        }
                    }
                }

            }
        });

        LockLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(passwordActivity.this);
                alertDialog.setTitle("Logging Out");
                alertDialog.setMessage("Confirm to LogOut")
                        .setIcon(R.drawable.ic_logout)
                        .setCancelable(false)
                        .setPositiveButton("LogOut", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                String g = "unlock";
                                SharedPreferences shrd = getSharedPreferences("alert", MODE_PRIVATE);
                                SharedPreferences.Editor editor = shrd.edit();
                                editor.putString("lock", g);
                                editor.apply();
                                alertState.setText(g);

                                firebaseAuth.signOut();
                                finish();
                                startActivity(new Intent(passwordActivity.this,LoginActivity.class));
                                finish();

                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                dialogInterface.cancel();

                            }
                        });

                AlertDialog alertDialog1 = alertDialog.create();
                alertDialog1.show();
            }
        });

        unlockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String unCode = unlockCode.getText().toString();
                String code = getCode.getText().toString();

                if (unCode.isEmpty()) {
                    Toast.makeText(passwordActivity.this, "Enter code", Toast.LENGTH_SHORT).show();
                } else if (code.equals(unCode)) {
                    closeKeyword();
                    unlockCode.setText("");
                    String g = "unlock";
                    SharedPreferences shrd = getSharedPreferences("alert", MODE_PRIVATE);
                    SharedPreferences.Editor editor = shrd.edit();
                    editor.putString("lock", g);
                    editor.apply();
                    alertState.setText(g);
                } else {
                    Toast.makeText(passwordActivity.this, "incorrect code", Toast.LENGTH_SHORT).show();
                }
            }
        });

        SharedPreferences state = getSharedPreferences("alert", MODE_PRIVATE);
        String alert = state.getString("lock", "unlock");
        alertState.setText(alert);

        //-------------------------------

        top_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLayout.setVisibility(View.VISIBLE);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeLayout.setVisibility(View.GONE);
                closeKeyword();
            }
        });

        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt = getCode.getText().toString();
                String old = oldPasscode.getText().toString();
                String change = changePasscode.getText().toString();
                //---------------------------------------
                if (change.isEmpty() || changePasscode.length() != 4) {
                    Toast.makeText(passwordActivity.this, "Enter passcode", Toast.LENGTH_SHORT).show();
                } else if (old.equals(txt)) {
                    closeKeyword();
                    SharedPreferences shrd2 = getSharedPreferences("personal", MODE_PRIVATE);
                    SharedPreferences.Editor editor = shrd2.edit();
                    editor.putString("pl", change);
                    editor.apply();
                    getCode.setText(change);

                    changeLayout.setVisibility(View.GONE);
                    oldPasscode.setText("");
                    changePasscode.setText("");

                    Toast.makeText(passwordActivity.this, "successfully changed", Toast.LENGTH_SHORT).show();

                } else if (!old.equals(txt)) {

                    oldPasscode.setText("");
                    changePasscode.setText("");
                    Toast.makeText(passwordActivity.this, "passcode not matched", Toast.LENGTH_SHORT).show();
                }
                //-------------------------------
            }
        });

        SharedPreferences getShared2 = getSharedPreferences("personal", MODE_PRIVATE);
        String value2 = getShared2.getString("pl", "");
        getCode.setText(value2);

    }

    private void closeKeyword() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void Notification() {

        String title = "Hi friends! "+timeStamp();
        String msg = "SECURITY ALERT From: " + user;
        FcmNotificationsSender notificationsSender = new FcmNotificationsSender("/topics/sr",
                title,msg,getApplicationContext(),passwordActivity.this);
        notificationsSender.SendNotifications();
    }

    private String timeStamp(){
        return new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
    }
}