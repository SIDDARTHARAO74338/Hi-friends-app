package com.siddartharao.hifriends;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class imageFullScreen extends AppCompatActivity {

    ImageView imageViewFullScreen,download;
    FirebaseAuth firebaseAuth;
    String username,theRoomCode;
    BitmapDrawable bitmapDrawable;
    Bitmap bitmap;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_full_screen);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        imageViewFullScreen = findViewById(R.id.imageViewFullScreen);
        download = findViewById(R.id.download);

        Glide.with(this).load(getIntent().getStringExtra("image")).into(imageViewFullScreen);
        //----------------

        theRoomCode = getIntent().getStringExtra("code").toString();

        firebaseAuth = FirebaseAuth.getInstance();
        String email = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference nameRef = FirebaseDatabase.getInstance().getReference().child("users").child(email);
        nameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    username = snapshot.child("name").getValue().toString();

                    watching();
                    //addRoomName();
                } else {
                    Toast.makeText(imageFullScreen.this, "username not found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Download_Image();
            }
        });


    }

    private void Download_Image() {

        bitmapDrawable = (BitmapDrawable) imageViewFullScreen.getDrawable();
        bitmap = bitmapDrawable.getBitmap();

        FileOutputStream fileOutputStream = null;

        File sdCard = Environment.getExternalStorageDirectory();
        File Directory = new File(sdCard.getAbsolutePath() + "/Download");
        Directory.mkdir();

        String imageuid = "HiFriends-"+getCurrentTime();
        String filename = String.format(imageuid + ".jpg");//String filename = String.format("%d.jpg",System.currentTimeMillis());
        File outfile = new File(Directory, filename);

        try {
            fileOutputStream = new FileOutputStream(outfile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(outfile));
            sendBroadcast(intent);
            Toast.makeText(imageFullScreen.this, "done", Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String getCurrentTime(){
        return new SimpleDateFormat("hh-mma_dd-MM-yyyy", Locale.getDefault()).format(new Date());
    }

    private void watching(){
        String onlineName = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("database").child(theRoomCode).child("onlineState");
        databaseReference.child(onlineName)
                .setValue(username + " watching image").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //homeLayout.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

}