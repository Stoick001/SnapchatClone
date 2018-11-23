package com.example.gorda.snapchatclone.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.gorda.snapchatclone.R;
import com.example.gorda.snapchatclone.UserInformation;
import com.example.gorda.snapchatclone.recyclerViewStory.StoryAdapter;
import com.example.gorda.snapchatclone.recyclerViewStory.StoryObject;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StoryFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<StoryObject> results = new ArrayList<>();

    public static StoryFragment newInstance() {
        StoryFragment fragment = new StoryFragment();
        return fragment;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) {
            clear();
            listenForData();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_story, container, false);

        mRecyclerView = view.findViewById(R.id.recyclerViewStory);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);

        layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        adapter = new StoryAdapter(getDataset(), getContext());

        mRecyclerView.setAdapter(adapter);

        final SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeStory);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                clear();
                listenForData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        clear();
        listenForData();

        return view;
    }

    private void clear() {
        int size = this.results.size();
        this.results.clear();
        adapter.notifyItemRangeChanged(0, size);
    }

    private ArrayList<StoryObject> getDataset() {
        listenForData();
        return results;
    }

    private void listenForData() {
        for (int i = 0; i < UserInformation.listFollowing.size(); i++) {
            DatabaseReference followingStoryDB = FirebaseDatabase.getInstance().getReference().child("users").child(UserInformation.listFollowing.get(i));
            followingStoryDB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String username = dataSnapshot.child("username").getValue().toString();
                    String profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                    String uid = dataSnapshot.getRef().getKey();

                    long timestampBeg = 0, timestampEnd = 0;

                    for (DataSnapshot storySnapshot : dataSnapshot.child("story").getChildren()) {
                        if (storySnapshot.child("timestampBeg").getValue() != null) {
                            timestampBeg = Long.parseLong(storySnapshot.child("timestampBeg").getValue().toString());
                        }
                        if (storySnapshot.child("timestampEnd").getValue() != null) {
                            timestampEnd = Long.parseLong(storySnapshot.child("timestampEnd").getValue().toString());
                        }

                        long currentTime = System.currentTimeMillis();

                        if (currentTime >= timestampBeg && currentTime <= timestampEnd) {
                            StoryObject object = new StoryObject(username, uid, profileImageUrl,"story");
                            if (!results.contains(object)) {
                                results.add(object);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
