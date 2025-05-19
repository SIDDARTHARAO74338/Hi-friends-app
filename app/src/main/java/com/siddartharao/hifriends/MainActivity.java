package com.siddartharao.hifriends;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    Button BtnRoomCode,allRooms;
    EditText roomCode;
    View mainPage;
    FirebaseAuth firebaseAuth;
    AlertDialog alertDialogLoading,alertDialogDelete,alertDialogCreate,alertDialog_security_system;
    ImageView roomOptions;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        TokenGenerator tokenGenerator = new TokenGenerator();
        tokenGenerator.gen();

        BtnRoomCode = findViewById(R.id.BtnRoomCode);
        roomCode = findViewById(R.id.roomCode);
        mainPage = findViewById(R.id.mainLayout);
        roomOptions = findViewById(R.id.roomOptions);

        //allRooms = findViewById(R.id.savedCodes);

        firebaseAuth = FirebaseAuth.getInstance();

        //String topic = note.getText().toString();
        //FirebaseMessaging.getInstance().subscribeToTopic("sid");

        if (firebaseAuth.getCurrentUser()==null){
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
            finish();
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mainPage.setVisibility(View.GONE);
                    checkNetwork();
                    checkNotificationSettings(MainActivity.this);
                }
            },1500);

            /*allRooms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(),userListActivity.class));
                    finish();
                }
            });*/

            BtnRoomCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String RC = roomCode.getText().toString();
                    if (RC.isEmpty()){
                        Toast.makeText(MainActivity.this, "Enter Room Code", Toast.LENGTH_SHORT).show();
                    }else{
                        loading();
                        checkDatabase(RC);
                    }
                }
            });
        }

        roomOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, roomOptions);
                popupMenu.getMenuInflater().inflate(R.menu.room_options, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.createroom) {
                            creatRoom();
                        } else if (item.getItemId() == R.id.deleteroom) {
                            deleteTheRoom();
                        } else if (item.getItemId() == R.id.settings) {
                            startActivity(new Intent(getApplicationContext(), settingsActivity.class));
                        } else if (item.getItemId() == R.id.logoutroom) {
                            logoutTheRoom();
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

    }

    private void security_system(){
        if (alertDialog_security_system == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.security_system_layout,
                    (ViewGroup) findViewById(R.id.security_layout)
            );
            builder.setView(view);
            alertDialog_security_system = builder.create();
            if (alertDialog_security_system.getWindow() != null){
                alertDialog_security_system.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
        }
        alertDialog_security_system.setCancelable(false);
        alertDialog_security_system.show();
    }

    private void checkNetwork(){
        if (Network.isNetworkAvailable(MainActivity.this)) {
            // Internet is available, proceed with Firebase Database access
        } else {
            Network.showToast(MainActivity.this, "No internet connection");
            // Handle no internet connection
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Error!")
                    .setMessage("No internet connection")
                    .setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.this.recreate();
                        }
                    })
                    .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .show();
        }

    }

    private void loading(){
        if (alertDialogLoading == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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

    private void popup(String roomName){
        new AlertDialog.Builder(MainActivity.this)
                //.setIcon(R.drawable.)
                .setTitle("New Room: "+roomName)
                .setMessage("Do you want to create...?")
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setNegativeButton("Cancel",null)
                .show();
    }

    private void checkDatabase(final String childKey) {

        DatabaseReference childRef = FirebaseDatabase.getInstance().getReference("database").child(childKey);
        // Add a ValueEventListener to check for the existence of the child
        childRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // The child exists
                    if (alertDialogLoading.isShowing())
                        alertDialogLoading.dismiss();
                    String RC = roomCode.getText().toString();
                    Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                    intent.putExtra("roomCode",RC);
                    roomCode.setText("");
                    startActivity(intent);
                    finish();
                } else {
                    if (alertDialogLoading.isShowing())
                        alertDialogLoading.dismiss();
                    new AlertDialog.Builder(MainActivity.this)
                            .setIcon(R.drawable.ic_warning)
                            .setTitle("Wrong Code!")
                            .setMessage("Try Again")
                            .setNegativeButton("Ok",null)
                            .show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that may occur during the check
                Toast.makeText(MainActivity.this, "Error checking for room: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void logoutTheRoom(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Logging Out");
        alertDialog.setMessage("Confirm to LogOut")
                .setIcon(R.drawable.ic_logout)
                .setCancelable(false)
                .setPositiveButton("LogOut", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(MainActivity.this,LoginActivity.class));
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

    private void deleteTheRoom(){
        if (alertDialogDelete == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.room_code,
                    (ViewGroup) findViewById(R.id.room_layout)
            );
            builder.setView(view);
            alertDialogDelete = builder.create();
            if (alertDialogDelete.getWindow() != null){
                alertDialogDelete.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            final EditText n = view.findViewById(R.id.roomName);
            n.setVisibility(View.GONE);

            final EditText d = view.findViewById(R.id.roomCode);
            view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialogDelete.dismiss();
                    d.setText("");
                }
            });


            view.findViewById(R.id.BtnRoomCode).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String s = d.getText().toString();
                    if (s.isEmpty()){
                        Toast.makeText(MainActivity.this, "Enter code", Toast.LENGTH_SHORT).show();
                    }else{
                        d.setText("");
                        DatabaseReference childRef = FirebaseDatabase.getInstance().getReference("database").child(s);
                        // Add a ValueEventListener to check for the existence of the child
                        childRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    // The child exists
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("database").child(s);
                                    databaseReference.removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // Data was successfully deleted.
                                                    Toast.makeText(MainActivity.this, "Room Deleted", Toast.LENGTH_SHORT).show();
                                                    alertDialogDelete.dismiss();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Handle the error if the data deletion fails.
                                                    Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    // The child does not exist
                                    Toast.makeText(MainActivity.this, "room no longer exist", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle any errors that may occur during the check
                                Toast.makeText(MainActivity.this, "Error checking for room: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("allRoomNames").child(s);
                                databaseReference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            // The child exists
                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("allRoomNames").child(s);
                                            databaseReference.removeValue()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // Data was successfully deleted.
                                                            //Toast.makeText(MainActivity.this, "Room Deleted", Toast.LENGTH_SHORT).show();
                                                            alertDialogDelete.dismiss();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            // Handle the error if the data deletion fails.
                                                            Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        } else {
                                            // The child does not exist
                                            Toast.makeText(MainActivity.this, "name no longer exist", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }

                }
            });

        }
        alertDialogDelete.setCancelable(false);
        alertDialogDelete.show();
    }

    private void creatRoom(){
        if (alertDialogCreate == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.room_code,
                    (ViewGroup) findViewById(R.id.room_layout)
            );
            builder.setView(view);
            alertDialogCreate = builder.create();
            if (alertDialogCreate.getWindow() != null){
                alertDialogCreate.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            final EditText d = view.findViewById(R.id.roomCode);
            final EditText name = view.findViewById(R.id.roomName);
            view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialogCreate.dismiss();
                    d.setText("");
                }
            });


            final Button b = view.findViewById(R.id.BtnRoomCode);
            b.setText("Create");

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String s = d.getText().toString();
                    String n = name.getText().toString();
                    if (s.isEmpty() && n.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Enter code", Toast.LENGTH_SHORT).show();
                    }else {
                        Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                        intent.putExtra("roomCode",s);
                        //intent.putExtra("roomName",n);
                        //---------------------
                        String mm = firebaseAuth.getCurrentUser().getUid();
                        DatabaseReference roomName = FirebaseDatabase.getInstance().getReference("allRoomNames").child(s);

                        roomName.setValue(n).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                //Toast.makeText(HomeActivity.this, "token saved", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Fail to save token", Toast.LENGTH_SHORT).show();
                            }
                        });
                        //---------------------
                        //roomCode.setText("");
                        startActivity(intent);
                        d.setText("");
                        finish();

                    }

                }
            });

        }
        alertDialogCreate.setCancelable(false);
        alertDialogCreate.show();
    }

    public static void checkNotificationSettings(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null && !notificationManager.areNotificationsEnabled()) {
            // Notifications are disabled, prompt the user to enable them
            new AlertDialog.Builder(context)
                    .setIcon(R.drawable.ic_notifications)
                    .setTitle("Your Notification Are Disabled")
                    .setMessage("Turn on notifications")
                    .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EnableNotifications(context);
                        }
                    }).setNegativeButton("ignore",null)
                    .show();

        }
    }

    @SuppressLint("InlinedApi")
    private static void EnableNotifications(Context context) {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        } else {
            intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    .setData(Uri.fromParts("package", context.getPackageName(), null));
        }
        context.startActivity(intent);
    }

}