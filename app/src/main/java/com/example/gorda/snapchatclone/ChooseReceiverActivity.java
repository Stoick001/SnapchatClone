package com.example.gorda.snapchatclone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;

import com.example.gorda.snapchatclone.ReciclerViewReciever.RecieverAdapter;
import com.example.gorda.snapchatclone.ReciclerViewReciever.RecieverObject;
import com.example.gorda.snapchatclone.fragment.CameraFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChooseReceiverActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    String Uid;
    Bitmap bitmap;

    private ArrayList<RecieverObject> results = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_receiver);

        byte[] img = CameraFragment.imageByte;

        try {
            bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }

        Uid = FirebaseAuth.getInstance().getUid();

        mRecyclerView = findViewById(R.id.recyclerViewFindUsers);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);

        layoutManager = new LinearLayoutManager(getApplication());
        mRecyclerView.setLayoutManager(layoutManager);

        adapter = new RecieverAdapter(getDataset(), getApplication());

        mRecyclerView.setAdapter(adapter);

        FloatingActionButton mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToStories();
            }
        });
    }

    private ArrayList<RecieverObject> getDataset() {
        listenForData();
        return results;
    }

    private void listenForData() {
        for (int i = 0; i < UserInformation.listFollowing.size(); i++) {
            DatabaseReference usersDB = FirebaseDatabase.getInstance().getReference().child("users").child(UserInformation.listFollowing.get(i));


            usersDB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String username = "";
                    String profileImageUrl = "";
                    String uid = dataSnapshot.getRef().getKey();

                    if (dataSnapshot.child("username").getValue() != null) {
                        username = dataSnapshot.child("username").getValue().toString();
                    }
                    if (dataSnapshot.child("profileImageUrl").getValue().toString() != null) {
                        profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                    }

                    RecieverObject obj = new RecieverObject(username, uid, profileImageUrl, false);
                    if (!results.contains(obj)) {
                        results.add(obj);
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void saveToStories() {
        final DatabaseReference userStoryDB = FirebaseDatabase.getInstance().getReference().child("users").child(Uid).child("story");
        final String key = userStoryDB.push().getKey();

        final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("captures").child(key);

        ByteArrayOutputStream baous = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 20, baous);
        byte[] dataToUpload = baous.toByteArray();

        UploadTask uploadTask = filePath.putBytes(dataToUpload);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filePath.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Long currentTimestamp = System.currentTimeMillis();
                        Long endTimestamp = currentTimestamp + (24 * 60 * 60 * 1000);

                        CheckBox mStory = findViewById(R.id.checkBoxStory);

                        if(mStory.isChecked()) {
                            Map<String, Object> mapToUpload = new HashMap();
                            mapToUpload.put("imageUrl", uri.toString());
                            mapToUpload.put("timestampBeg", currentTimestamp);
                            mapToUpload.put("timestampEnd", endTimestamp);

                            userStoryDB.child(key).setValue(mapToUpload);
                        }

                        for (int i = 0; i < results.size(); i++) {
                            if (results.get(i).getReceive()) {
                                DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("users").child(results.get(i).getUid()).child("received").child(Uid);
                                Map<String, Object> mapToUpload = new HashMap();
                                mapToUpload.put("imageUrl", uri.toString());
                                mapToUpload.put("timestampBeg", currentTimestamp);
                                mapToUpload.put("timestampEnd", endTimestamp);

                                userDB.child(key).setValue(mapToUpload);
                            }
                        }

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        finish();
                    }
                });
            }
        });

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                finish();
            }
        });
    }
}
