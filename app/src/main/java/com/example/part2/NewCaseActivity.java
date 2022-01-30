package com.example.part2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.joda.time.PeriodType;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class NewCaseActivity extends AppCompatActivity {

    EditText caseTitle;
    EditText caseDescription;
    Spinner caseCategorySpinner;
    TextView caseDueDate;
    TextView caseDueTime;
    TextView remainingTime;
    TextView placeNewCase;
    TextView cancelNewCase;

    DatabaseReference casesDatabaseRef, userCasesDatabaseRef;
    DocumentReference documentReference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    Calendar pickedDateAndTime = Calendar.getInstance();

    Uri imageUri;

    String name;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_case);
        getSupportActionBar().hide();

        caseTitle = findViewById(R.id.newCaseTitle);
        caseDescription = findViewById(R.id.newCaseDescription);
        caseCategorySpinner = findViewById(R.id.newCaseCategorySpinner);
        caseDueDate = findViewById(R.id.newCaseDueDate);
        caseDueTime = findViewById(R.id.newCaseDueTime);
        remainingTime = findViewById(R.id.remainingTime);
        placeNewCase = findViewById(R.id.placeNewCase);
        cancelNewCase = findViewById(R.id.cancelButton);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        //Get current date and time
        Calendar calendar = Calendar.getInstance();
        final int YEAR = calendar.get(Calendar.YEAR);
        final int MONTH = calendar.get(Calendar.MONTH);
        final int DAY = calendar.get(Calendar.DAY_OF_MONTH);
        final int HOUR = calendar.get(Calendar.HOUR_OF_DAY);
        final int MINUTE = calendar.get(Calendar.MINUTE);

        pickedDateAndTime.set(YEAR, MONTH, DAY, HOUR + 1, MINUTE);

        SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd LLL yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");

        String currentDate = dateFormat.format(pickedDateAndTime.getTime());
        String currentTime = timeFormat.format(pickedDateAndTime.getTime());

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = currentUser.getUid();

        caseDueDate.setText(currentDate);
        caseDueTime.setText(currentTime);

        documentReference = firebaseFirestore.collection("Cases").document(currentUserId);
        casesDatabaseRef = firebaseDatabase.getReference("Cases");
        userCasesDatabaseRef = firebaseDatabase.getReference("User cases").child(currentUserId);





        caseDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(NewCaseActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        pickedDateAndTime.set(i, i1, i2);

                        if (pickedDateAndTime.before(calendar)) {
                            pickedDateAndTime = Calendar.getInstance();
                            caseDueDate.setText(dateFormat.format(pickedDateAndTime.getTime()).toString());
                            caseDueTime.setText(timeFormat.format(pickedDateAndTime.getTime()).toString());
                            Toast.makeText(NewCaseActivity.this, "The past date cannot be selected", Toast.LENGTH_SHORT).show();
                        } else {
                            caseDueDate.setText(dateFormat.format(pickedDateAndTime.getTime()).toString());
                        }
                    }
                }, YEAR, MONTH, DAY);
                datePickerDialog.show();
            }
        });

        caseDueTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(NewCaseActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        int year = pickedDateAndTime.get(Calendar.YEAR);
                        int month = pickedDateAndTime.get(Calendar.MONTH);
                        int day = pickedDateAndTime.get(Calendar.DAY_OF_MONTH);

                        pickedDateAndTime.set(year, month, day, i, i1);

                        if (pickedDateAndTime.before(calendar)) {
                            pickedDateAndTime = Calendar.getInstance();
                            caseDueTime.setText(timeFormat.format(pickedDateAndTime.getTime()).toString());
                            Toast.makeText(NewCaseActivity.this, "The past time cannot be selected", Toast.LENGTH_SHORT).show();
                        } else {
                            caseDueTime.setText(timeFormat.format(pickedDateAndTime.getTime()).toString());
                        }
                    }
                }, HOUR, MINUTE, false);
                timePickerDialog.show();
            }
        });

        cancelNewCase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NewCaseActivity.this, MainActivity.class));
            }
        });

        placeNewCase.setOnClickListener(view -> {
            createNewCase();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        documentReference.get().addOnCompleteListener(task -> {
            if (task.getResult().exists()) {
                name = task.getResult().getString("name");
                uid = task.getResult().getString("uid");
            }
        });
    }

    /*private void updateRemainingTime() {



        Calendar pickedCal = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        pickedCal.set(year, month, day, hour , minute);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date date1 = simpleDateFormat.parse(pickedCal.getTime().toString());
            Date date2 = simpleDateFormat.parse(calendar.getTime().toString());

            long startDate = date1.getTime();
            long endDate = date2.getTime();

            if (startDate <= endDate) {
                Period period = new Period(startDate, endDate, PeriodType.yearMonthDay());

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }*/

    private void createNewCase() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm a");


        String date, time;
        date = simpleDateFormat.format(pickedDateAndTime.getTime()).toString();
        time = simpleTimeFormat.format(pickedDateAndTime.getTime()).toString();


        String title = caseTitle.getText().toString();
        String description = caseDescription.getText().toString();
        String category = caseCategorySpinner.getSelectedItem().toString();
        String dueDate = date + " " + time;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userUID  = user.getUid();

        if (TextUtils.isEmpty(title)) {
            caseTitle.setError("Title cannot remains empty");
            caseTitle.requestFocus();
        } else if (TextUtils.isEmpty(description)) {
            caseDescription.setError("Description cannot remains empty");
            caseDescription.requestFocus();
        } else {
            Cases newCase = new Cases(title, description, category, dueDate, userUID);
            String id = userCasesDatabaseRef.push().getKey();
            userCasesDatabaseRef.child(id).setValue(newCase);

            String child = casesDatabaseRef.push().getKey();
            newCase.setKey(id);
            casesDatabaseRef.child(child).setValue(newCase);

            documentReference.set(newCase);


            Toast.makeText(NewCaseActivity.this, "New case is created", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(NewCaseActivity.this, MainActivity.class));
        }
    }
}