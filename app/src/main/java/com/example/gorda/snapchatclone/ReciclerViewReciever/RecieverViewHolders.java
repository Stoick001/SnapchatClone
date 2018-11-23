package com.example.gorda.snapchatclone.ReciclerViewReciever;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gorda.snapchatclone.R;

public class RecieverViewHolders extends RecyclerView.ViewHolder {
    public TextView username;
    public CheckBox receiveCheck;
    public ImageView profileImage;

    public RecieverViewHolders(View itemView) {
        super(itemView);

        username = itemView.findViewById(R.id.textViewUsernameFollowItem);
        receiveCheck = itemView.findViewById(R.id.recieverId);
        profileImage = itemView.findViewById(R.id.imageViewProfileFollowItem);
    }

}
