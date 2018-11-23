package com.example.gorda.snapchatclone;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestFutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.gorda.snapchatclone.recyclerViewStory.StoryObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DisplayImageActivity extends AppCompatActivity {

    String uid, chatOrStory, profileImageUrl, username;
    private ImageView mImage;
    private boolean started = false;
    private ArrayList<String> imageUrlList = new ArrayList<>();

    private LinearLayout info;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);

        Bundle b = getIntent().getExtras();
        uid = b.getString("uid");
        chatOrStory = b.getString("chatOrStory");
        username = b.getString("username");
        profileImageUrl = b.getString("profileImageUrl");

        mImage = findViewById(R.id.imageViewDisplayedImage);

        ImageView profile = findViewById(R.id.userProfile);
        TextView user = findViewById(R.id.usernameDisplay);
        info = findViewById(R.id.userInfo);
        progressBar = findViewById(R.id.progressBar);

        if (profileImageUrl.equals("default")) {
            profile.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.profile));
        } else {
            Glide.with(getApplication()).load(profileImageUrl).into(profile);
        }

        user.setText(username);

        if (chatOrStory.equals("chat")) {
            listenForChat();
        }

        if (chatOrStory.equals("story")) {
            listenForStory();
        }
    }

    private void listenForChat() {
        final DatabaseReference chatDB = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getUid()).child("received").child(uid);

        chatDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String imgUrl = "";

                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {

                    if (chatSnapshot.child("imageUrl").getValue() != null) {
                        imgUrl = chatSnapshot.child("imageUrl").getValue().toString();
                    }

                    imageUrlList.add(imgUrl);
                    if (!started) {
                        started = true;
                        initializeDisplay();
                    }
                    chatDB.child(chatSnapshot.getKey()).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void listenForStory() {
        DatabaseReference followingStoryDB = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        followingStoryDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String imgUrl = "";
                long timestampBeg = 0, timestampEnd = 0;

                for (DataSnapshot storySnapshot : dataSnapshot.child("story").getChildren()) {
                    if (storySnapshot.child("timestampBeg").getValue() != null) {
                        timestampBeg = Long.parseLong(storySnapshot.child("timestampBeg").getValue().toString());
                    }
                    if (storySnapshot.child("timestampEnd").getValue() != null) {
                        timestampEnd = Long.parseLong(storySnapshot.child("timestampEnd").getValue().toString());
                    }
                    if (storySnapshot.child("imageUrl").getValue() != null) {
                        imgUrl = storySnapshot.child("imageUrl").getValue().toString();
                    }

                    long currentTime = System.currentTimeMillis();

                    if (currentTime >= timestampBeg && currentTime <= timestampEnd) {
                        imageUrlList.add(imgUrl);
                        if (!started) {
                            started = true;
                            initializeDisplay();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private int imgIterator = 0;
    private void initializeDisplay() {
        progressBar.setVisibility(View.VISIBLE);
        Glide.with(getApplication()).load(imageUrlList.get(imgIterator))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.INVISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.INVISIBLE);
                        return false;
                    }
                }).into(mImage);
        info.bringToFront();

        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeImage();
            }
        });

        final Handler handler = new Handler();

        final int delay = 5000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeImage();
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    private void changeImage() {
        progressBar.setVisibility(View.VISIBLE);
        imgIterator++;
        if (imgIterator >= imageUrlList.size()-1) {
            finish();
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            Glide.with(getApplication()).load(imageUrlList.get(imgIterator)).into(mImage);
            info.bringToFront();
        }
    }
}
