package com.example.gorda.snapchatclone.recyclerViewStory;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.gorda.snapchatclone.R;
import com.example.gorda.snapchatclone.UserInformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryViewHolders> {

    private List<StoryObject> usersList;
    private Context context;

    public StoryAdapter(List<StoryObject> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;
    }

    @NonNull
    @Override
    public StoryViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_story_item, null);
        StoryViewHolders rcv = new StoryViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull final StoryViewHolders rcViewHolders, int i) {
        rcViewHolders.username.setText(usersList.get(i).getUsername());
        rcViewHolders.username.setTag(usersList.get(i).getUid());

        rcViewHolders.layout.setTag(usersList.get(i).getChatOrStory());

        String imageUrl = usersList.get(i).getProfileImageUrl();

        if (imageUrl.equals("default")) {
            rcViewHolders.profileImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.profile));
        } else {
            rcViewHolders.cardView.setTag(imageUrl);
            Glide.with(context).load(imageUrl).into(rcViewHolders.profileImage);
        }
    }

    @Override
    public int getItemCount() {
        return this.usersList.size();
    }
}
