package com.example.part2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class EditUserProfileActivity extends AppCompatActivity {

    TextView cancelEditProfile;
    TextView saveProfile;
    ImageView userProfilePicture;
    EditText userName, userBio, userEmail, userPassword, userConfirmPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        cancelEditProfile = findViewById(R.id.cancelEditProfile);
        saveProfile = findViewById(R.id.saveProfile);
        userProfilePicture = findViewById(R.id.userProfilePictureEdit);
        userName = findViewById(R.id.usernameEdit);
        userBio = findViewById(R.id.userBioEdit);
        userEmail = findViewById(R.id.userEmailEdit);
        userPassword = findViewById(R.id.userPasswordEdit);
        userConfirmPassword = findViewById(R.id.userPasswordConfirm);

        cancelEditProfile.setOnClickListener(view -> {
            startActivity(new Intent(EditUserProfileActivity.this, UserProfileActivity.class));
        });

        saveProfile.setOnClickListener(view -> {
            startActivity(new Intent(EditUserProfileActivity.this, UserProfileActivity.class));
        });

        userProfilePicture.setOnClickListener(view -> {

        });
    }
}