package com.siddartharao.hifriends;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.icu.util.VersionInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.BuildConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class settingsActivity extends AppCompatActivity {

    TextView username,emailTxt,version,logout;
    ImageView wallpaper;
    EditText changeName,changeRoomName,enterRoomCode;
    Button change,changeRoomBtn,checkForUpdates;
    FirebaseAuth firebaseAuth;
    private ScreenOffReceiver screenOffReceiver;
    Switch sendMsgSwitch;
    private DatabaseHelper dbHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        //---------------screen off
        screenOffReceiver = new ScreenOffReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenOffReceiver, filter);
        //--------------------

        username = findViewById(R.id.name);
        emailTxt = findViewById(R.id.email);
        changeName = findViewById(R.id.changeName);
        change = findViewById(R.id.change);
        logout = findViewById(R.id.logout);
        wallpaper = findViewById(R.id.wallpaper);
        version = findViewById(R.id.version);
        changeRoomBtn = findViewById(R.id.changeNameBtn);
        changeRoomName = findViewById(R.id.changeRoomName);
        enterRoomCode = findViewById(R.id.EnterRoomCode);
        checkForUpdates = findViewById(R.id.checkForUpdates);
        sendMsgSwitch = findViewById(R.id.sendMsgSwitch);

        String versionName = getVersionName();
        version.setText("Version: " + versionName);

        firebaseAuth = FirebaseAuth.getInstance();

        wallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), imagesActivity.class));
            }
        });

        changeRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cName = changeRoomName.getText().toString();
                String room = enterRoomCode.getText().toString();
                if (cName.isEmpty() || room.isEmpty()){
                    Toast.makeText(settingsActivity.this, "enter data", Toast.LENGTH_SHORT).show();
                }else{
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference databaseReference1 = databaseReference.child("allRoomNames").child(room);
                    databaseReference1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                databaseReference1.setValue(cName);
                                changeRoomName.setText("");
                                enterRoomCode.setText("");
                            }else{
                                Toast.makeText(settingsActivity.this, "room not found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

            }
        });

        checkForUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),FileUploadActivity.class));
            }
        });

        //int emailLength = email.length();
        //String userEmail = email.substring(0,emailLength - 4);

        String emailUid = firebaseAuth.getCurrentUser().getUid();

        DatabaseReference nameRef = FirebaseDatabase.getInstance().getReference().child("users").child(emailUid);
        nameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    String user = snapshot.child("name").getValue().toString();
                    username.setText(user);
                } else {
                    Toast.makeText(settingsActivity.this, "username not found", Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(getApplicationContext(), settingsActivity.class));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        String email = firebaseAuth.getCurrentUser().getEmail();
        emailTxt.setText(email);

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = firebaseAuth.getCurrentUser().getUid();
                //int emailLength = email.length();
                //String userEmail = email.substring(0,emailLength - 4);

                DatabaseReference nameRef = FirebaseDatabase.getInstance().getReference().child("users").child(email);

                String name = changeName.getText().toString();
                nameRef.child("name")
                        .setValue(name).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(settingsActivity.this, "Name saved", Toast.LENGTH_SHORT).show();
                                changeName.setText("");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(settingsActivity.this, "Fail to save name", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(settingsActivity.this);
                alertDialog.setTitle("Remove from room");
                alertDialog.setMessage("Confirm to Remove")
                        .setIcon(R.drawable.ic_logout)
                        .setCancelable(false)
                        .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                removeToken();

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


        dbHelper = new DatabaseHelper(this);

        boolean switchState = dbHelper.getSwitchState();
        sendMsgSwitch.setChecked(switchState);

        sendMsgSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dbHelper.setSwitchState(isChecked);
                if (sendMsgSwitch.isChecked()){
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("settings")
                            .child(emailUid);
                    databaseReference.setValue("check");
                }else {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("settings")
                            .child(emailUid);
                    databaseReference.setValue("!check");
                }
            }
        });

    }

    private void removeToken(){
        String code = getIntent().getStringExtra("roomcode");
        String email = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("database").child(code).child("usersTokens");
        databaseReference.child(email)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(settingsActivity.this,MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("database").child(code).child("typing");
        databaseReference2.child(email)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //startActivity(new Intent(settingsActivity.this,MainActivity.class));
                        //finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference("database").child(code).child("onlineState");
        databaseReference3.child(email)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //startActivity(new Intent(settingsActivity.this,MainActivity.class));
                        //finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private String getVersionName() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "Unknown";
        }
    }

}