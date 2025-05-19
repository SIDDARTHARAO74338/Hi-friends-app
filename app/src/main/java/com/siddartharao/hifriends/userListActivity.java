package com.siddartharao.hifriends;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

import java.util.ArrayList;
import java.util.List;

public class userListActivity extends AppCompatActivity {

    EditText addUser;
    Button addUserBtn;
    ListView allUsers;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        addUser = findViewById(R.id.addUser);
        addUserBtn = findViewById(R.id.addUserBtn);
        allUsers = findViewById(R.id.allUsers);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        String userUid = firebaseAuth.getCurrentUser().getUid();

        addUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String AD = addUser.getText().toString();

                if (AD.isEmpty()){
                    Toast.makeText(userListActivity.this, "Enter code to add", Toast.LENGTH_SHORT).show();
                }else {
                    String userKey = addUser.getText().toString();
                    DatabaseReference nameRef2 = FirebaseDatabase.getInstance().getReference().child("allRoomNames").child(userKey);
                    nameRef2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists()){

                                String keyValue = snapshot.getValue().toString();

                                databaseReference.child("users").child(userUid)
                                        .child("list")
                                        .push()
                                        .setValue(keyValue)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });
                            }else{
                                Toast.makeText(userListActivity.this, "Room not found", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



                }
            }
        });

        DatabaseReference nameRef = FirebaseDatabase.getInstance().getReference().child("users").child(userUid).child("list");
        nameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<String> items = new ArrayList<>();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    String item = snapshot1.getValue(String.class);
                    items.add(item);
                }

                // Populate ListView
                ArrayAdapter<String> adapter = new ArrayAdapter<>(userListActivity.this, R.layout.all_user_list_back,R.id.nameItem, items);
                allUsers.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        allUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String searchValue = (String) parent.getItemAtPosition(position);


                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("allRoomNames");

                databaseRef.orderByValue().equalTo(searchValue).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String key = snapshot.getKey();
                                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                                intent.putExtra("roomCode",key);
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(userListActivity.this, "error missing data", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Failed to read value

                    }
                });




            }
        });




    }
}