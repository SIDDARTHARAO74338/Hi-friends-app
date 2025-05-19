package com.siddartharao.hifriends;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class imagesActivity extends AppCompatActivity {

    AlertDialog alertDialogLoading;
    ImageView image;
    TextView select,upload,delete;
    private static int REQUEST = 100;
    Uri imageUri;
    ImageView wallView;
    FirebaseAuth firebaseAuth;
    StorageReference storageReference;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    SwipeRefreshLayout swipeRefreshLayout;
    private ScreenOffReceiver screenOffReceiver;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        //---------------screen off
        screenOffReceiver = new ScreenOffReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenOffReceiver, filter);
        //--------------------

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        loading();

        select = findViewById(R.id.select);
        upload = findViewById(R.id.upload);
        image = findViewById(R.id.image1);
        delete = findViewById(R.id.delete);

        wallView = findViewById(R.id.wallView);

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,REQUEST);
                image.setVisibility(View.VISIBLE);
            }
        });

        swipeRefreshLayout = findViewById(R.id.swiperFreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                imagesActivity.this.recreate();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading();

                String imageName = firebaseAuth.getCurrentUser().getEmail();
                StorageReference folderRef = FirebaseStorage.getInstance().getReference("wallpapers/"+imageName+".jpg");

                folderRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if(alertDialogLoading.isShowing())
                            alertDialogLoading.dismiss();
                        imagesActivity.this.recreate();
                        Toast.makeText(imagesActivity.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(alertDialogLoading.isShowing())
                            alertDialogLoading.dismiss();
                        Toast.makeText(imagesActivity.this, " Nothing to Delete", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri == null){
                    Toast.makeText(imagesActivity.this, "null", Toast.LENGTH_SHORT).show();
                }else {
                    loading();

                    String imageName = firebaseAuth.getCurrentUser().getEmail();
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference("wallpapers/"+imageName+".jpg");
                    storageReference.putFile(imageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    //imageView.setImageURI(null);
                                    Toast.makeText(imagesActivity.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                                    if (alertDialogLoading.isShowing())
                                        alertDialogLoading.dismiss();

                                    image.setVisibility(View.GONE);
                                    imagesActivity.this.recreate();
                                    //sendPushNotification();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    if (alertDialogLoading.isShowing())
                                        alertDialogLoading.dismiss();

                                    //delete.setVisibility(View.INVISIBLE);
                                    Toast.makeText(imagesActivity.this, "Failed to Upload", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        String imageName = firebaseAuth.getCurrentUser().getEmail();
        StorageReference imagesRef = storageRef.child("wallpapers/"+imageName+".jpg");

        try {
            File localfile = File.createTempFile("tempfile",".jpg");
            imagesRef.getFile(localfile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {


                            if(alertDialogLoading.isShowing())
                                alertDialogLoading.dismiss();

                            Bitmap bitmap = BitmapFactory.decodeFile(localfile.getAbsolutePath());
                            wallView.setImageBitmap(bitmap);


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            if(alertDialogLoading.isShowing())
                                alertDialogLoading.dismiss();

                            Toast.makeText(imagesActivity.this, "Image not found", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    private void loading(){
        if (alertDialogLoading == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(imagesActivity.this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST && data != null && data.getData() != null) {
            imageUri = data.getData();
            image.setImageURI(imageUri);

        } else {
            upload.setVisibility(View.INVISIBLE);
        }
    }

}