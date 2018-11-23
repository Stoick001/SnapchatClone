package com.example.gorda.snapchatclone.ReciclerViewReciever;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class RecieverAdapter extends RecyclerView.Adapter<RecieverViewHolders> {

    private List<RecieverObject> usersList;
    private Context context;

    public RecieverAdapter(List<RecieverObject> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;
    }

        @NonNull
        @Override
    public RecieverViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_reciever_item, null);
        RecieverViewHolders rcv = new RecieverViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecieverViewHolders rcViewHolders, int i) {
        rcViewHolders.username.setText(usersList.get(i).getUsername());

        String imageUrl = usersList.get(i).getProfileImageUrl();

        if (imageUrl.equals("default")) {
            rcViewHolders.profileImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.profile));
        } else {
            Glide.with(context).load(imageUrl).into(rcViewHolders.profileImage);
        }

        rcViewHolders.receiveCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean receiveState = !usersList.get(rcViewHolders.getLayoutPosition()).getReceive();
                usersList.get(rcViewHolders.getLayoutPosition()).setReceive(receiveState);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.usersList.size();
    }
}
