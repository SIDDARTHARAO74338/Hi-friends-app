package com.siddartharao.hifriends;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.OnSwipe;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.CallLog;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Rational;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeActivity extends AppCompatActivity implements chatAdapter.OnItemClickListener ,SwipeToDeleteListener{

    View sendBtn,homeLayout,imageLayout,light,searchLayout,emojiLayout,mainLayout,audioLayout,limitLayout;
    EditText messageBox,search,enterEmoji;
    Button cancelSearch,setEmoji,cancelEmoji,clear30;
    TextView myName,typing,thatMsg,room,roomname;
    RecyclerView recyclerView;
    DatabaseReference database;
    FirebaseAuth firebaseAuth;
    ImageView settings,eye, call,image,sendImage,selectImage,uploadImage,deleteImage,sendAudio,resume,pause,stop;
    private static int REQUEST = 100;
    Uri imageUri;
    private ScreenOffReceiver screenOffReceiver;
    private TextView onlineState;
    AlertDialog alertDialogLoading,alertDialogCode,alertDialogEmoji;
    SimpleDateFormat simpleDateFormat;
    String currenttime;
    StorageReference storageReference;
    int il = 0;
    int wp = 0,co = 0,ar = 0,al = 0;
    String theRoomCode,theRoomName;
    String theToken;
    ArrayList<Messages> messagesArrayList;
    chatAdapter adater;
    private PopupWindow popupWindow;
    private String generatedKey,key;
    private MediaRecorder mediaRecorder;
    private String outputFile;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    private ListView listView;
    private List<String> mp3List;
    private MediaPlayer mediaPlayer;
    ArrayAdapter<String> adapter;

    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility", "SimpleDateFormat"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        firebaseAuth = FirebaseAuth.getInstance();
        String email = firebaseAuth.getCurrentUser().getUid();

        room = findViewById(R.id.room);
        roomname = findViewById(R.id.roomname);
        FirebaseMessaging.getInstance().subscribeToTopic(theToken);

        //---------------screen off
        screenOffReceiver = new ScreenOffReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenOffReceiver, filter);

        /*if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CALL_LOG},
                    PERMISSION_REQUEST_READ_CALL_LOG);
        } else {
            displayCallLog();
        }*/
        //-------------------------

        limitLayout = findViewById(R.id.limitLayout);
        clear30 = findViewById(R.id.clear30);

        sendBtn = findViewById(R.id.sendBtn);
        messageBox = findViewById(R.id.messageBox);
        myName = findViewById(R.id.myName);
        settings = findViewById(R.id.settings);
        typing = findViewById(R.id.typing);
        call = findViewById(R.id.call);
        sendImage = findViewById(R.id.sendImage);
        sendAudio = findViewById(R.id.sendAudio);
        homeLayout = findViewById(R.id.homeID);
        light = findViewById(R.id.light);

        pause = findViewById(R.id.pause);
        resume = findViewById(R.id.resume);
        stop = findViewById(R.id.stop);

        selectImage = findViewById(R.id.selectImage);
        uploadImage = findViewById(R.id.uploadImage);
        deleteImage = findViewById(R.id.deleteImage);
        imageLayout = findViewById(R.id.imageLayout);
        audioLayout = findViewById(R.id.audioLayout);
        image = findViewById(R.id.image);
        eye = findViewById(R.id.eye);

        searchLayout = findViewById(R.id.searchLayout);
        cancelSearch = findViewById(R.id.cancelSearch);

        emojiLayout = findViewById(R.id.emojiLayout);
        setEmoji = findViewById(R.id.setEmoji);
        enterEmoji = findViewById(R.id.enterEmoji);
        cancelEmoji = findViewById(R.id.cancelEmoji);
        mainLayout = findViewById(R.id.mainLayout);

        listView = findViewById(R.id.listView);
        mp3List = new ArrayList<>();

        adapter = new ArrayAdapter<>(HomeActivity.this,android.R.layout.simple_list_item_1,mp3List);
        listView.setAdapter(adapter);


        mediaPlayer = new MediaPlayer();

        onlineState = findViewById(R.id.online);

        recyclerView = findViewById(R.id.chats);
        thatMsg = findViewById(R.id.thatMsg);
        database = FirebaseDatabase.getInstance().getReference();

        search = findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (messagesArrayList.size() != 0){
                    adater.searchNotes(s.toString());
                }
            }
        });


        //String email = firebaseAuth.getCurrentUser().getUid();
        //int emailLength = email.length();
        //String userEmail = email.substring(0,emailLength - 4);

        theRoomCode = getIntent().getStringExtra("roomCode");
        //theRoomName = getIntent().getStringExtra("roomName");
        room.setText(theRoomCode);
        //roomname.setText(theRoomName);

        getMp3(theRoomCode);

        loading();

        DatabaseReference nameRef = FirebaseDatabase.getInstance().getReference().child("users").child(email);
        nameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    String username = snapshot.child("name").getValue().toString();
                    myName.setText(username);
                    if (alertDialogLoading.isShowing())
                        alertDialogLoading.dismiss();

                    addRoomName();

                    addToken();
                    showonline();
                    onResume();
                } else {
                    Toast.makeText(HomeActivity.this, "username not found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        cancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchLayout.setVisibility(View.GONE);
                search.setText("");
            }
        });


        DatabaseReference ty = FirebaseDatabase.getInstance().getReference("database").child(theRoomCode).child("typing");
        ty.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String type = myName.getText().toString();
                if (snapshot.exists()) {

                    List<String> values = new ArrayList<>();

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String value = dataSnapshot.getValue(String.class);
                        String key = dataSnapshot.getKey();
                        values.add(value);

                        String u = firebaseAuth.getCurrentUser().getUid();
                        if (key != null && !key.equals(u)) {
                            // Use the value as needed (e.g., update UI)
                            typing.setText(value);
                        }
                    }
                    //typing.setText(String.join("\n", values));
                    //String usertype = snapshot.child("typing").getValue().toString();
                    //typing.setText(usertype);
                } else {
                    Toast.makeText(HomeActivity.this, "start typing", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,REQUEST);
                image.setVisibility(View.VISIBLE);

                uploadImage.setVisibility(View.VISIBLE);
            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loading();

                String imageName = firebaseAuth.getCurrentUser().getEmail();
                StorageReference storageReference = FirebaseStorage.getInstance().getReference(theRoomCode+"/"+imageName+".jpg");
                storageReference.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //imageView.setImageURI(null);
                                Toast.makeText(HomeActivity.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                                if (alertDialogLoading.isShowing())
                                    alertDialogLoading.dismiss();

                                image.setVisibility(View.GONE);
                                HomeActivity.this.recreate();
                                //sendPushNotification();

                                DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference("database").child(theRoomCode).child("usersTokens");
                                tokenRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if (snapshot.exists()) {
                                            List<String> tokensvalue = new ArrayList<>();

                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                String value = dataSnapshot.getValue(String.class);
                                                theToken = value;
                                                tokensvalue.add(value);

                                                String user = myName.getText().toString();
                                                String title = user + ": " + timeStamp();
                                                String m = "New Image uploaded";
                                                FcmNotificationsSender notificationsSender = new FcmNotificationsSender(theToken,
                                                        title,m,getApplicationContext(),HomeActivity.this);
                                                notificationsSender.SendNotifications();
                                            }
                                            //onlineState.setText(String.join("\n", tokensvalue));

                                            if (alertDialogLoading.isShowing())
                                                alertDialogLoading.dismiss();

                                        } else {
                                            Toast.makeText(HomeActivity.this, "username not found", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if (alertDialogLoading.isShowing())
                                    alertDialogLoading.dismiss();

                                //delete.setVisibility(View.INVISIBLE);
                                Toast.makeText(HomeActivity.this, "Failed to Upload", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading();

                StorageReference folderRef = FirebaseStorage.getInstance().getReference(theRoomCode+"/");

                folderRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        // Iterate through each item in the folder
                        for (StorageReference item : listResult.getItems()) {
                            // Delete the item (file)
                            item.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // File deleted successfully
                                    if(alertDialogLoading.isShowing())
                                        alertDialogLoading.dismiss();
                                    HomeActivity.this.recreate();
                                    Toast.makeText(HomeActivity.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                                    Log.d("FirebaseStorage", "Item deleted: " + item.getPath());
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors during deletion
                                    if(alertDialogLoading.isShowing())
                                        alertDialogLoading.dismiss();
                                    Toast.makeText(HomeActivity.this, " Fail to Delete", Toast.LENGTH_SHORT).show();
                                    Log.e("FirebaseStorage", "Error deleting item: " + item.getPath(), exception);
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors during listing
                        if(alertDialogLoading.isShowing())
                            alertDialogLoading.dismiss();
                        Toast.makeText(HomeActivity.this, "Fail to Delete", Toast.LENGTH_SHORT).show();
                        Log.e("FirebaseStorage", "Error listing items in folder: " + folderRef.getPath(), exception);
                    }
                });
            }
        });


        sendAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ar == 0){
                    audioLayout.setVisibility(View.VISIBLE);
                    sendAudio.setImageResource(R.drawable.ic_mic_off);
                    mp3List.clear();
                    getMp3(theRoomCode);
                    ar=1;

                }else{
                    mp3List.clear();
                    getMp3(theRoomCode);
                    audioLayout.setVisibility(View.GONE);
                    sendAudio.setImageResource(R.drawable.ic_mic);
                    ar=0;
                }

            }
        });


        //----------------------------

        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getApplicationContext(), imagesActivity.class));
                //finish();
                RecyclerView recyclerView = findViewById(R.id.allImages);

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();

                StorageReference imagesRef = storageRef.child(theRoomCode+"/");

                imagesRef.listAll().addOnSuccessListener(listResult -> {

                    List<StorageReference> items = listResult.getItems();
                    List<String> imageUrls = new ArrayList<>();
                    //List<ImageInfo> imageList = new ArrayList<>();

                    if (items.isEmpty()){
                        Toast.makeText(HomeActivity.this, "No images found", Toast.LENGTH_SHORT).show();
                        deleteImage.setVisibility(View.INVISIBLE);
                    }else{
                        deleteImage.setVisibility(View.VISIBLE);
                        for (StorageReference item : items) {
                            item.getDownloadUrl().addOnSuccessListener(uri -> {
                                // Add the image URL to your list
                                imageUrls.add(uri.toString());

                                recyclerView.setHasFixedSize(true);
                                //staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
                                recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));

                                //recyclerView.setLayoutManager(staggeredGridLayoutManager);

                                ImageAdapter imageAdapter = new ImageAdapter(imageUrls,theRoomCode);
                                recyclerView.setAdapter(imageAdapter);


                            });
                        }
                    }


                }).addOnFailureListener(exception -> {
                    // Handle any errors that occur while listing the items in the folder
                    if (alertDialogLoading.isShowing())
                        alertDialogLoading.dismiss();
                    Toast.makeText(HomeActivity.this, "try again", Toast.LENGTH_SHORT).show();
                });

                if (il == 0){
                    imageLayout.setVisibility(View.VISIBLE);
                    sendImage.setImageResource(R.drawable.ic_hide_image);
                    il = 1;
                } else if (il == 1) {
                    imageLayout.setVisibility(View.GONE);
                    sendImage.setImageResource(R.drawable.ic_image);
                    il = 0;
                }

            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(HomeActivity.this, call);
                popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.Fast) {
                            sendFastNotification();
                        } else if (item.getItemId() == R.id.timePass) {
                            sendTimePassNotification();
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(HomeActivity.this, settings);
                popupMenu.getMenuInflater().inflate(R.menu.all_settings_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.search) {
                            searchLayout.setVisibility(View.VISIBLE);
                        } else if (item.getItemId() == R.id.settings) {
                            Intent intent = new Intent(getApplicationContext(),settingsActivity.class);
                            intent.putExtra("roomcode",theRoomCode);
                            startActivity(intent);
                        }else if(item.getItemId() == R.id.cover){
                            if (co==0){
                                recyclerView.setVisibility(View.INVISIBLE);
                                co=1;
                            }else{
                                recyclerView.setVisibility(View.VISIBLE);
                                co=0;
                            }
                        }else if (item.getItemId() == R.id.deleteAll){
                            deleteAll();
                        }
                        return true;
                    }
                });
                popupMenu.show();

            }
        });

        messageBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String mail = firebaseAuth.getCurrentUser().getUid();
                String type = myName.getText().toString();
                String mb = messageBox.getText().toString();
                if (mb.isEmpty()) {
                    database.child("database").child(theRoomCode).child("typing").child(mail)
                            .setValue("").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                } else {
                    database.child("database").child(theRoomCode).child("typing").child(mail)
                            .setValue(type + " is typing...").addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userUrl = firebaseAuth.getCurrentUser().getUid();
                String user = myName.getText().toString();
                String msg = messageBox.getText().toString();
                if (msg.isEmpty()) {
                    Toast.makeText(HomeActivity.this, "Enter message", Toast.LENGTH_SHORT).show();
                } else if (thatMsg.getVisibility() == View.VISIBLE) {
                    messageBox.setText("");
                    String topMsg = thatMsg.getText().toString();
                    thatMsg.setVisibility(View.GONE);
                    String emoji = "";
                    Messages messages = new Messages(msg,topMsg,user,time(),userUrl,emoji);
                    database.child("database").child(theRoomCode).child("messages").push().setValue(messages)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    //Toast.makeText(HomeActivity.this, "done", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnCanceledListener(new OnCanceledListener() {
                                @Override
                                public void onCanceled() {
                                    Toast.makeText(HomeActivity.this, "Fail to send message", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    messageBox.setText("");
                    String topMsg = "";
                    String emoji = "";
                    Messages messages = new Messages(msg,topMsg,user,time(),userUrl,emoji);
                    database.child("database").child(theRoomCode).child("messages").push().setValue(messages)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    //Toast.makeText(HomeActivity.this, "done", Toast.LENGTH_SHORT).show();
                                    generatedKey = database.getKey();
                                }
                            }).addOnCanceledListener(new OnCanceledListener() {
                                @Override
                                public void onCanceled() {
                                    Toast.makeText(HomeActivity.this, "Fail to send message", Toast.LENGTH_SHORT).show();
                                }
                            });
                }

                String state = onlineState.getText().toString();
                int emailLength = state.length();
                String onlineState = state.substring(emailLength - 6);

                String uid = firebaseAuth.getCurrentUser().getUid();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("settings")
                        .child(uid);

                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.exists()){
                            String checkState = snapshot.getValue().toString();

                            if (!onlineState.equals("online") && checkState.equals("check")){
                                sendTimePassNotification();
                            }

                            databaseReference.removeEventListener(this);
                        }

                        databaseReference.removeEventListener(this);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };

                databaseReference.addValueEventListener(valueEventListener);

            }
        });

        messagesArrayList = new ArrayList<>();
        adater = new chatAdapter(HomeActivity.this, messagesArrayList,this::onItemClick,this::onSwipe);
        recyclerView.setAdapter(adater);

        ItemTouchHelper.Callback callback = new SwipeToDeleteCallback(adater);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        DatabaseReference chatRefrece = database.child("database").child(theRoomCode).child("messages");
        chatRefrece.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                messagesArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    Messages messages = dataSnapshot.getValue(Messages.class);
                    //-----------------------
                    Messages modle = snapshot.getValue(Messages.class);
                    modle.setSenderId(snapshot.getKey());
                    key = snapshot.getKey();
                    //------------------------
                    messagesArrayList.add(messages);
                    //------------------------

                    if (messagesArrayList.size() > 100){
                        limitLayout.setVisibility(View.VISIBLE);
                    }else {
                        limitLayout.setVisibility(View.GONE);
                    }

                }

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(HomeActivity.this);
                linearLayoutManager.scrollToPosition(messagesArrayList.size()-1);
                recyclerView.setLayoutManager(linearLayoutManager);

                adater.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (wp == 0) {
                    //wallpaper();
                    String mail = myName.getText().toString();
                    outputFile = getExternalCacheDir().getAbsolutePath() + "/" + mail + ".mp3";
                    mediaRecorder = new MediaRecorder();
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mediaRecorder.setOutputFile(outputFile);
                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(HomeActivity.this, "Recording started", Toast.LENGTH_SHORT).show();
                    wp = 1;
                } else {
                    if (mediaRecorder != null) {
                        mediaRecorder.stop();
                        mediaRecorder.release();
                        mediaRecorder = null;

                        uploadAudio();
                        Toast.makeText(HomeActivity.this, "Recording Ended", Toast.LENGTH_SHORT).show();
                    }
                    //homeLayout.setBackgroundResource(R.color.white);
                    //Toast.makeText(HomeActivity.this, "Image removed", Toast.LENGTH_SHORT).show();
                    wp = 0;
                }

                return true;
            }
        });

        thatMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thatMsg.setVisibility(View.GONE);
            }
        });

        cancelEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emojiLayout.setVisibility(View.GONE);
                mainLayout.setVisibility(View.VISIBLE);
                enterEmoji.setText("");
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedMp3 = mp3List.get(position);
                // Play the selected MP3 file
                String code = room.getText().toString();
                playMp3(selectedMp3,code);
                //name.setText(selectedMp3);
                //buttonLayout.setVisibility(View.VISIBLE);
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(HomeActivity.this, "The end", Toast.LENGTH_SHORT).show();
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.pause();
            }
        });

        pause.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mediaPlayer.stop();
                return true;
            }
        });

        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.start();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAudio();
            }
        });

        clear30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAllItem();
            }
        });

    }

    @Override
    public void onItemClick(int position) {

        emojiLayout.setVisibility(View.VISIBLE);
        mainLayout.setVisibility(View.GONE);
        theEmoji(position);

    }

    private void deleteAll(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
        alertDialog.setTitle("All messages");
        alertDialog.setMessage("Confirm to delete")
                .setIcon(R.drawable.ic_delete)
                .setCancelable(false)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("database").child(theRoomCode).child("messages");
                        databaseReference.removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Data was successfully deleted.
                                        Toast.makeText(HomeActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Handle the error if the data deletion fails.
                                        Toast.makeText(HomeActivity.this, "error in deleting", Toast.LENGTH_SHORT).show();
                                    }
                                });

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

    private void deleteAudio(){
        String code = room.getText().toString();
        String name = myName.getText().toString();
        StorageReference storageReference1 = FirebaseStorage.getInstance().getReference();
        StorageReference storageReference2 = storageReference1.child(code+"audios/").child(name + ".mp3");
        storageReference2.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(HomeActivity.this, "Audio deleted", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void theEmoji(int pos){
        setEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String EN = enterEmoji.getText().toString();
                if (EN.isEmpty()) {
                    Toast.makeText(HomeActivity.this, "Enter emoji", Toast.LENGTH_SHORT).show();
                }else {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("database")
                            .child(theRoomCode).child("messages");
                    ArrayList<String> childs = new ArrayList<>();

                    ValueEventListener valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String data = dataSnapshot.getKey();
                                childs.add(data);
                            }
                            int c = Integer.parseInt(String.valueOf(pos));
                            String childName = childs.get(c);
                            DatabaseReference databaseReferenceemoji = FirebaseDatabase.getInstance().getReference("database")
                                    .child(theRoomCode).child("messages").child(childName).child("emoji");

                            databaseReferenceemoji.setValue(EN);
                            enterEmoji.setText("");
                            emojiLayout.setVisibility(View.GONE);
                            mainLayout.setVisibility(View.VISIBLE);

                            databaseReference.removeEventListener(this);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    };

                    databaseReference.addValueEventListener(valueEventListener);

                }
            }
        });

    }

    @Override
    public void onSwipe(int position, int direction) {
        if (direction == ItemTouchHelper.START) {
            // Handle swipe to the left
            //Toast.makeText(HomeActivity.this, "left", Toast.LENGTH_SHORT).show();
            String swipedText = String.valueOf(position);
            removeItem(swipedText);
        } else if (direction == ItemTouchHelper.END) {
            // Handle swipe to the right
            //Toast.makeText(HomeActivity.this, "right", Toast.LENGTH_SHORT).show();
            thatMsg.setVisibility(View.VISIBLE);
            String swipedText = adater.getItemText(position);
            thatMsg.setText(swipedText);
        }
    }

    private void removeItem(String p){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
        alertDialog.setTitle("messages");
        alertDialog.setMessage("Confirm to delete")
                .setIcon(R.drawable.ic_delete)
                .setCancelable(false)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("database")
                                .child(theRoomCode).child("messages");
                        ArrayList<String> childs = new ArrayList<>();
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    String data = dataSnapshot.getKey();
                                    childs.add(data);
                                }
                                int c = Integer.parseInt(p);
                                String childName = childs.get(c);
                                DatabaseReference databaseReferenceremove = FirebaseDatabase.getInstance().getReference("database")
                                        .child(theRoomCode).child("messages").child(childName);

                                databaseReferenceremove.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        //Toast.makeText(HomeActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(HomeActivity.this, "fail to delete", Toast.LENGTH_SHORT).show();
                                    }
                                });


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        Toast.makeText(HomeActivity.this, "Deleted", Toast.LENGTH_SHORT).show();

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

    private void removeAllItem(){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("database").child(theRoomCode).child("messages");
        ArrayList<String> childs = new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String data = dataSnapshot.getKey();
                    childs.add(data);
                }

                for (int i = 0; i < 30; i++) {
                    //int c = Integer.parseInt(p);
                    String childName = childs.get(i);
                    DatabaseReference databaseReferenceremove = FirebaseDatabase.getInstance().getReference("database")
                            .child(theRoomCode).child("messages").child(childName);
                    databaseReferenceremove.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //Toast.makeText(HomeActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(HomeActivity.this, "fail to delete", Toast.LENGTH_SHORT).show();
                        }
                    });
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        HomeActivity.this.recreate();
        //Toast.makeText(HomeActivity.this, "Deleted 30% messages", Toast.LENGTH_SHORT).show();

    }

    private void addRoomName(){
        DatabaseReference name1 = FirebaseDatabase.getInstance().getReference().child("allRoomNames").child(theRoomCode);
        name1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    String username = snapshot.getValue().toString();
                    roomname.setText(username);

                    if (alertDialogLoading.isShowing())
                        alertDialogLoading.dismiss();

                } else {
                    Toast.makeText(HomeActivity.this, "name not found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void wallpaper(){

        loading();

        String imageId = firebaseAuth.getCurrentUser().getEmail();
        storageReference = FirebaseStorage.getInstance().getReference("wallpapers/"+imageId+".jpg");

        try {
            File localfile = File.createTempFile("tempfile",".jpg");
            storageReference.getFile(localfile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                            if(alertDialogLoading.isShowing())
                                alertDialogLoading.dismiss();

                            Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                            Drawable drawable = new BitmapDrawable(getResources(),bitmap);
                            homeLayout.setBackground(drawable);
                            Toast.makeText(HomeActivity.this, "Image set", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            if(alertDialogLoading.isShowing())
                                alertDialogLoading.dismiss();

                            Toast.makeText(HomeActivity.this, "Image not found", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void uploadAudio() {
        Uri file = Uri.fromFile(new File(outputFile));
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference audioRef = storageRef.child(theRoomCode + "audios/" + file.getLastPathSegment());

        audioRef.putFile(file)
                .addOnSuccessListener(taskSnapshot -> {
                    // Upload successful
                    // You can get the download URL of the uploaded file here
                    audioRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        // Now you can save this URL to Firebase Database or do anything else with it
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle unsuccessful uploads
                    e.printStackTrace();
                    Toast.makeText(this, String.valueOf(e), Toast.LENGTH_SHORT).show();
                });
    }


    private String time(){
        simpleDateFormat = new SimpleDateFormat("EEE, d MMM hh:mm a");
        Date date = new Date();
        currenttime = simpleDateFormat.format(date.getTime());
        return currenttime;
    }

    private void multiMode(){
        String m = messageBox.getText().toString();

        StringBuilder repeatedText = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            repeatedText.append(m+"\n");
        }
        messageBox.setText(repeatedText.toString());

    }

    private void pipMode(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Rational rational = new Rational(16,9);
            PictureInPictureParams params = new PictureInPictureParams.Builder()
                    .setAspectRatio(rational)
                    .build();
            enterPictureInPictureMode(params);
        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode);
        if (isInPictureInPictureMode) {
            // The activity is in PiP mode

            View top = findViewById(R.id.top);
            View msgLayout = findViewById(R.id.msgLayout);

            top.setVisibility(View.GONE);
            msgLayout.setVisibility(View.GONE);

            Toast.makeText(this, "PIP Mode On", Toast.LENGTH_SHORT).show();
        } else {
            // The activity is not in PiP mode

            View top = findViewById(R.id.top);
            View msgLayout = findViewById(R.id.msgLayout);

            top.setVisibility(View.VISIBLE);
            msgLayout.setVisibility(View.VISIBLE);

            Toast.makeText(this, "PIP Mode Off", Toast.LENGTH_SHORT).show();
        }
    }




    private void showonline(){

        DatabaseReference onlineUpdata = FirebaseDatabase.getInstance().getReference("database").child(theRoomCode).child("onlineState");
        onlineUpdata.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    List<String> values = new ArrayList<>();

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String value = dataSnapshot.getValue(String.class);
                        String key = dataSnapshot.getKey();
                        values.add(value);

                        String u = firebaseAuth.getCurrentUser().getUid();
                        if (key != null && !key.equals(u)) {
                            // Use the value as needed (e.g., update UI)
                            onlineState.setText(value);
                        }

                    }
                    //onlineState.setText(String.join("\n", values));

                    //String online = snapshot.getValue().toString();
                    //onlineState.setText(online);
                } else {
                    Toast.makeText(HomeActivity.this, "onlineState not found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addToken() {

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        // Get the token
                        String token = task.getResult();
                        Log.d(TAG, "FCM Registration Token: " + token);

                        String RC = room.getText().toString();
                        String username = firebaseAuth.getCurrentUser().getUid();
                        DatabaseReference nameRef = FirebaseDatabase.getInstance().getReference("database").child(RC).child("usersTokens");

                        nameRef.child(username)
                                .setValue(token).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        //Toast.makeText(HomeActivity.this, "token saved", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(HomeActivity.this, "Fail to save token", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                });

    }

    private void sendFastNotification(){
        DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference("database").child(theRoomCode).child("usersTokens");
        tokenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    
                    List<String> tokensvalue = new ArrayList<>();

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String value = dataSnapshot.getValue(String.class);
                        theToken = value;
                        String key = dataSnapshot.getKey();
                        tokensvalue.add(value);

                        String u = firebaseAuth.getCurrentUser().getUid();
                        if (key != null && !key.equals(u)) {
                            String name = myName.getText().toString();
                            String groupName = roomname.getText().toString();
                            String title = "FROM: " + groupName + "~" + timeStamp();
                            String msg = "Fast ⏳: " + name;
                            FcmNotificationsSender notificationsSender = new FcmNotificationsSender(theToken,
                                    title,msg,getApplicationContext(),HomeActivity.this);
                            notificationsSender.SendNotifications();
                            Toast.makeText(HomeActivity.this, "Notification sent", Toast.LENGTH_SHORT).show();
                        }


                    }
                    //onlineState.setText(String.join("\n", tokensvalue));

                    if (alertDialogLoading.isShowing())
                        alertDialogLoading.dismiss();

                } else {
                    Toast.makeText(HomeActivity.this, "username not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendTimePassNotification(){
        DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference("database").child(theRoomCode).child("usersTokens");
        tokenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    //theToken = snapshot.child("phone").getValue().toString();
                    //myName.setText(username);

                    List<String> tokensvalue = new ArrayList<>();

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String value = dataSnapshot.getValue(String.class);
                        theToken = value;
                        String key = dataSnapshot.getKey();
                        tokensvalue.add(value);

                        String u = firebaseAuth.getCurrentUser().getUid();
                        if (key != null && !key.equals(u)) {
                            String name = myName.getText().toString();
                            String groupName = roomname.getText().toString();
                            String title = "FROM: " + groupName + "~" + timeStamp();
                            String msg = "TimePass 😝: " + name;
                            FcmNotificationsSender notificationsSender = new FcmNotificationsSender(theToken,
                                    title,msg,getApplicationContext(),HomeActivity.this);
                            notificationsSender.SendNotifications();
                            Toast.makeText(HomeActivity.this, "Notification sent", Toast.LENGTH_SHORT).show();
                        }


                    }
                    //onlineState.setText(String.join("\n", tokensvalue));

                    if (alertDialogLoading.isShowing())
                        alertDialogLoading.dismiss();

                } else {
                    Toast.makeText(HomeActivity.this, "username not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendMsgNotification(String m){
        DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference("database").child(theRoomCode).child("usersTokens");
        tokenRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    List<String> tokensvalue = new ArrayList<>();

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String value = dataSnapshot.getValue(String.class);
                        theToken = value;
                        String key = dataSnapshot.getKey();
                        tokensvalue.add(value);


                        String u = firebaseAuth.getCurrentUser().getUid();
                        if (key != null && !key.equals(u)) {

                            String name = myName.getText().toString();
                            String groupName = roomname.getText().toString();
                            String title = "FROM: " + groupName + "~" + timeStamp();
                            String msg = "User: " + name + "\n" + m;
                            //String r = theRoomCode;
                            FcmNotificationsSender notificationsSender = new FcmNotificationsSender(theToken,
                                    title,msg,getApplicationContext(),HomeActivity.this);
                            notificationsSender.SendNotifications();
                        }


                    }
                    //onlineState.setText(String.join("\n", tokensvalue));

                    if (alertDialogLoading.isShowing())
                        alertDialogLoading.dismiss();

                } else {
                    Toast.makeText(HomeActivity.this, "username not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        /*DatabaseReference onlineUpdata = FirebaseDatabase.getInstance().getReference("database").child(theRoomCode).child("onlineState");
        onlineUpdata.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    List<String> values = new ArrayList<>();

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String value = dataSnapshot.getValue(String.class);
                        String key = dataSnapshot.getKey();
                        values.add(value);

                        String u = firebaseAuth.getCurrentUser().getUid();
                        if (key != null && !key.equals(u)) {
                            // Use the value as needed (e.g., update UI)
                            onlineState.setText(value);
                        }

                    }
                    //onlineState.setText(String.join("\n", values));

                    //String online = snapshot.getValue().toString();
                    //onlineState.setText(online);
                } else {
                    Toast.makeText(HomeActivity.this, "onlineState not found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/


    }

    private String timeStamp(){
        return new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();

        homeLayout.setVisibility(View.GONE);
        eye.setVisibility(View.VISIBLE);

        if (isInPictureInPictureMode()) {
            // Exit PiP mode
            finish();
        } else {
            super.onBackPressed();
        }
    }

    private void loading(){
        if (alertDialogLoading == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
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

    private void roomCodePopup(){
        if (alertDialogCode == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.room_code,
                    (ViewGroup) findViewById(R.id.room_layout)
            );
            builder.setView(view);
            alertDialogCode = builder.create();
            if (alertDialogCode.getWindow() != null){
                alertDialogCode.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            final EditText rc = view.findViewById(R.id.roomCode);
            rc.requestFocus();

            view.findViewById(R.id.BtnRoomCode).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String r = rc.getText().toString();
                    theRoomCode = r;
                    room.setText(theRoomCode);
                    alertDialogCode.dismiss();
                }
            });

        }
        alertDialogCode.setCancelable(false);
        alertDialogCode.show();
    }

    @Override
    protected void onResume() {
        homeLayout.setVisibility(View.VISIBLE);
        eye.setVisibility(View.GONE);
        String nameR = myName.getText().toString();
        String onlineName = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("database").child(theRoomCode).child("onlineState");
        databaseReference.child(onlineName)
                .setValue(nameR + " is online").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        homeLayout.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
        super.onResume();
    }

    @Override
    protected void onPause() {
        homeLayout.setVisibility(View.GONE);
        eye.setVisibility(View.VISIBLE);
        String nameP = myName.getText().toString();
        String onlineName = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("database").child(theRoomCode).child("onlineState");
        databaseReference.child(onlineName)
                .setValue(nameP + " is offline, " + timeStamp()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        homeLayout.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(screenOffReceiver);
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void playMp3(String mp3FileName,String code) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child(code+"audios/").child(mp3FileName);

        try {
            // Create a local file to store the downloaded MP3
            File localFile = File.createTempFile("temp", "3gp");

            // Download the MP3 file to the local file
            storageRef.getFile(localFile)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Local file has been created
                        // Now you can play the M
                        try {
                            mediaPlayer.reset();
                            mediaPlayer.setDataSource(localFile.getAbsolutePath());
                            mediaPlayer.prepare();
                            mediaPlayer.start();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    })
                    .addOnFailureListener(exception -> {
                        // Handle errors
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private void getMp3(String code) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child(code+"audios/");

        storageRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference item : listResult.getItems()) {
                            // Add the MP3 file names to the list
                            mp3List.add(item.getName());
                        }
                        adapter.notifyDataSetChanged();

                        // Notify the adapter that the data set has changed
                        //adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle errors
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST && data != null && data.getData() != null) {
            imageUri = data.getData();
            image.setImageURI(imageUri);

        } else {
            uploadImage.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }

}