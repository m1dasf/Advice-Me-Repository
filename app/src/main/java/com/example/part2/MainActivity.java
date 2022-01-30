package com.example.part2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button logOutButton;
    FloatingActionButton newCaseButton;
    ImageView profilePicture;
    RecyclerView recyclerView;
    MyAdapter myAdapter;
    ArrayList<Cases> list;
    MyAdapter.RecycleViewClickListener clickListener;

    FirebaseAuth mAuth;
    DatabaseReference casesDbReference;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseRecyclerOptions<Cases> options = new FirebaseRecyclerOptions.Builder<Cases>().setQuery(casesDbReference, Cases.class).build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Hide Action Bar
        getSupportActionBar().hide();

        //logOutButton = findViewById(R.id.logOutButton);
        newCaseButton = findViewById(R.id.newCaseButton);
        profilePicture = findViewById(R.id.userProfilePictureMenu);
        recyclerView = findViewById(R.id.recycleView);

        mAuth = FirebaseAuth.getInstance();
        casesDbReference = firebaseDatabase.getReference("Cases");
        list = new ArrayList<>();

       setAdapter();

        casesDbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Cases singleCase = dataSnapshot.getValue(Cases.class);
                    list.add(singleCase);
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /*logOutButton.setOnClickListener(view -> {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });*/

        newCaseButton.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, NewCaseActivity.class));
        });

        profilePicture.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
        });
    }

    private void setAdapter() {
        setOnClickListener();
        myAdapter = new MyAdapter(this, list, clickListener);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapter);
    }

    private void setOnClickListener() {
        clickListener = new MyAdapter.RecycleViewClickListener() {
            @Override
            public void click(View view, int position) {
                Intent intent = new Intent(MainActivity.this, ViewCaseActivity.class);
                intent.putExtra("caseId", list.get(position).getKey());
                intent.putExtra("userId", list.get(position).getUserId());
                startActivity(intent);
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }
}