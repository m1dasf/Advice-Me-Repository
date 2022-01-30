package com.example.part2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ViewCaseActivity extends AppCompatActivity {

    TextView title;
    TextView category;
    TextView remainingTime;
    TextView description;
    TextView userName;
    TextView userBio;
    TextView goBack;
    TextView getAdvice;
    ImageView profilePicture;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference, databaseReferenceUser;
    private String caseId;
    private String userId;
    private Cases singleCase;
    private Users singleUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_case);
        getSupportActionBar().hide();

        title = findViewById(R.id.viewCaseCaseTitle);
        category = findViewById(R.id.viewCaseCategory);
        remainingTime = findViewById(R.id.viewCaseRemainingTime);
        description = findViewById(R.id.viewCaseDescription);
        userName = findViewById(R.id.viewCaseUsername);
        userBio = findViewById(R.id.viewCaseUserBio);
        goBack = findViewById(R.id.viewCaseBack);
        getAdvice = findViewById(R.id.viewCaseAdvice);
        profilePicture = findViewById(R.id.viewCaseProfilePicture);

        Bundle bundle = getIntent().getExtras();
        caseId = bundle.getString("caseId");
        userId = bundle.getString("userId");

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Cases");
        databaseReferenceUser = firebaseDatabase.getReference("All Users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                displayData(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReferenceUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                displayDataUser(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void displayDataUser(DataSnapshot snapshot) {
        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
            singleUser = dataSnapshot.getValue(Users.class);
            if (userId.equals(singleCase.getUserId())) {
                userName.setText(singleUser.getUsername());
                userBio.setText(singleUser.getBio());
            }
        }
    }

    private void displayData(DataSnapshot snapshot) {
        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
            singleCase = dataSnapshot.getValue(Cases.class);
            if (caseId.equals(singleCase.getKey())) {
                title.setText(singleCase.getTitle());
                category.setText(singleCase.getCategory());
                remainingTime.setText(findDifference(singleCase.getDueDate()));
                description.setText(singleCase.getDescription());
            }
        }
    }

    private static String findDifference(String dueDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");

        Date currentDate = new Date();
        Calendar dueDateCal = Calendar.getInstance();
        try {
            dueDateCal.setTime(simpleDateFormat.parse(dueDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date dueDateDate = dueDateCal.getTime();

        long difference = dueDateDate.getTime() - currentDate.getTime();
        long differenceInDays = TimeUnit.MILLISECONDS.toDays(difference);
        long differenceInHours = TimeUnit.MILLISECONDS.toHours(difference) % 24;
        long differenceInMinutes = TimeUnit.MILLISECONDS.toMinutes(difference) % 60;

        String remainingTime = "";
        if (differenceInDays > 0) {
            remainingTime = differenceInDays + "d. ";
        }
        if (differenceInHours > 0) {
            remainingTime = remainingTime + differenceInHours + "h. ";
        }
        if (differenceInMinutes > 0) {
            remainingTime = remainingTime + differenceInMinutes + "m. ";
        } else {
            remainingTime = "Expired";
        }

        return remainingTime;
    }


    @Override
    protected void onStart() {
        super.onStart();

        /*FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = currentUser.getUid();
        DocumentReference documentReference;
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        documentReference = firebaseFirestore.collection("cases").document()*/
    }
}