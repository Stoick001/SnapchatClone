package com.example.gorda.snapchatclone.recyclerViewFollow;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gorda.snapchatclone.R;

public class FollowViewHolders extends RecyclerView.ViewHolder {
    public TextView username;
    public Button follow;
    public ImageView profileImage;

    public FollowViewHolders(View itemView) {
        super(itemView);

        username = itemView.findViewById(R.id.textViewUsernameFollowItem);
        follow = itemView.findViewById(R.id.buttonFollowItem);
        profileImage = itemView.findViewById(R.id.imageViewFollower);
    }

}
