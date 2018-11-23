package com.example.gorda.snapchatclone;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.gorda.snapchatclone.recyclerViewFollow.FollowAdapter;
import com.example.gorda.snapchatclone.recyclerViewFollow.FollowObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class FindUsersActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private EditText mInput;

    private ArrayList<FollowObject> results = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_users);

        mInput = findViewById(R.id.editTextFindUsers);
        Bundle bundle = getIntent().getExtras();

        mInput.setText(bundle.getString("currentUsername"));
        mInput.setSelection(mInput.getText().length());

        ImageButton mSearch = findViewById(R.id.findUsersSearch);
        ImageButton mBack = findViewById(R.id.findUsersBack);
        ImageButton mClear = findViewById(R.id.findUsersSearch);

        mRecyclerView = findViewById(R.id.recyclerViewFindUsers);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);

        layoutManager = new LinearLayoutManager(getApplication());
        mRecyclerView.setLayoutManager(layoutManager);

        adapter = new FollowAdapter(getDataset(), getApplication());

        mRecyclerView.setAdapter(adapter);

        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear();
                listenForData();
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInput.setText("");
            }
        });

        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                clear();
                listenForData();
            }
        });
    }

    private void listenForData() {
        DatabaseReference usersDB = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = usersDB.orderByChild("username").startAt(mInput.getText().toString()).endAt(mInput.getText().toString() + "\uf8ff");

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String username = "";
                String profileImageUrl = "";
                String uid = dataSnapshot.getRef().getKey();

                if (dataSnapshot.child("username").getValue() != null) {
                    username = dataSnapshot.child("username").getValue().toString();
                }

                if (dataSnapshot.child("profileImageUrl").getValue().toString() != null) {
                    profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                }

                if (!uid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    FollowObject obj = new FollowObject(username, uid, profileImageUrl);
                    results.add(obj);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void clear() {
        int size = this.results.size();
        this.results.clear();
        adapter.notifyItemRangeChanged(0, size);
    }

    private ArrayList<FollowObject> getDataset() {
        listenForData();
        return results;
    }
}
