package com.example.gorda.snapchatclone.recyclerViewStory;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gorda.snapchatclone.DisplayImageActivity;
import com.example.gorda.snapchatclone.R;

public class StoryViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView username;
    public LinearLayout layout;
    public ImageView profileImage;
    public CardView cardView;

    public StoryViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        username = itemView.findViewById(R.id.textViewUsernameStory);
        layout = itemView.findViewById(R.id.layoutStory);
        profileImage = itemView.findViewById(R.id.imageViewStoryProfile);
        cardView = itemView.findViewById(R.id.cardViewStory);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), DisplayImageActivity.class);
        Bundle b = new Bundle();
        b.putString("uid", username.getTag().toString());
        b.putString("profileImageUrl", cardView.getTag().toString());
        b.putString("username", username.getText().toString());
        b.putString("chatOrStory", layout.getTag().toString());
        intent.putExtras(b);
        view.getContext().startActivity(intent);
    }
}
