package com.example.gorda.snapchatclone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gorda.snapchatclone.loginRegistration.SplashScreenActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    int REQUEST_GET_SINGLE_FILE = 1;

    String currentUsername, currentProfileImageUrl;
    String newUsername, newProfileImageUrl;

    ImageButton buttonBack, buttonSave;
    EditText usernameChange;
    ImageView profileChange;

    DatabaseReference user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        user = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUsername = dataSnapshot.child("username").getValue().toString();
                currentProfileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();

                usernameChange.setText(currentUsername);
                usernameChange.setSelection(usernameChange.getText().length());
                if (!currentProfileImageUrl.equals("default")) {
                    Glide.with(getApplication()).load(currentProfileImageUrl).into(profileChange);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        usernameChange = findViewById(R.id.editTextChangeUsername);
        profileChange = findViewById(R.id.imageViewProfile);

        profileChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GET_SINGLE_FILE);
            }
        });

        buttonBack = findViewById(R.id.imageButtonBack);
        buttonSave = findViewById(R.id.imageButtonSave);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileActivity.this.finish();
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProfileImage();
            }
        });

        Button logoutBtn = findViewById(R.id.buttonLogout);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });
    }

    private void saveProfile() {
        newUsername = usernameChange.getText().toString();
        Map userInfo = new HashMap();

        if (!newUsername.equals(currentUsername)) {
            userInfo.put("username", newUsername);
        }

        if (newProfileImageUrl != null) {
            userInfo.put("profileImageUrl", imageUrl);
        }

        user.updateChildren(userInfo).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                ProfileActivity.this.finish();
            }
        });
    }

    String imageUrl = "default";
    private void saveProfileImage() {
        if (newProfileImageUrl != null) {
            if (!currentProfileImageUrl.equals("default")) {
                StorageReference photoDelete = FirebaseStorage.getInstance().getReferenceFromUrl(currentProfileImageUrl);

                photoDelete.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        uploadImage();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        saveProfileImage();
                    }
                });
            } else {
                uploadImage();
            }
        } else {
            saveProfile();
        }
    }

    private void uploadImage() {
        final StorageReference path = FirebaseStorage.getInstance().getReference().child("profile").child(FirebaseAuth.getInstance().getUid());

        Bitmap img = ((BitmapDrawable)profileChange.getDrawable()).getBitmap();

        ByteArrayOutputStream baous = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.JPEG, 20, baous);
        byte[] dataToUpload = baous.toByteArray();

        UploadTask uploadTask = path.putBytes(dataToUpload);

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageUrl = uri.toString();
                        saveProfile();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        saveProfileImage();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                saveProfile();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (resultCode == RESULT_OK && requestCode == REQUEST_GET_SINGLE_FILE) {
                Uri uri = data.getData();
                String path = getPathFromUri(uri);

                if (path != null) {
                    File file = new File(path);
                    uri = Uri.fromFile(file);
                }

                profileChange.setImageURI(uri);
                newProfileImageUrl = uri.getPath();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private String getPathFromUri(Uri uri) {
        String res = null;
        return res;
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), SplashScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
