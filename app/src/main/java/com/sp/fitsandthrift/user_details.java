package com.sp.fitsandthrift;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sp.fitsandthrift.model.Usermodel;
import com.google.android.material.textfield.TextInputEditText;

public class user_details extends AppCompatActivity {

    private TextInputEditText editTextUsername, editTextGender, editTextPhoneNumber;
    private Button btnContinue;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextUsername = findViewById(R.id.registername);
        editTextGender = findViewById(R.id.registergender);
        editTextPhoneNumber = findViewById(R.id.registerphone);
        btnContinue = findViewById(R.id.btnLogout);
        progressBar = findViewById(R.id.progressBar);

        email = mAuth.getCurrentUser().getEmail();

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserDetails();
            }
        });
    }

    private void updateUserDetails() {
        progressBar.setVisibility(View.VISIBLE);

        String username = editTextUsername.getText().toString().trim();
        String gender = editTextGender.getText().toString().trim();
        String phoneNumber = editTextPhoneNumber.getText().toString().trim();

        Usermodel usermodel = new Usermodel(email, username, gender, phoneNumber, true);

        DocumentReference userRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
        userRef.set(usermodel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Toast.makeText(user_details.this, "User details updated", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(user_details.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(user_details.this, "Failed to update user details", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

