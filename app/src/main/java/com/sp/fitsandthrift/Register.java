package com.sp.fitsandthrift;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sp.fitsandthrift.model.Usermodel;

public class Register extends AppCompatActivity {

    private TextInputEditText editTextEmail, editTextPassword, cfmPassword;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    private TextView loginNow;
    private ProgressBar progressBar;
    private FirebaseFirestore db;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().signOut(); // Ensure the user is signed out
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextEmail = findViewById(R.id.em);
        editTextPassword = findViewById(R.id.psw);
        cfmPassword = findViewById(R.id.pswCfm);
        btnRegister = findViewById(R.id.btnLogout);
        progressBar = findViewById(R.id.progressBar);
        loginNow = findViewById(R.id.LoginNow);

        loginNow.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        btnRegister.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String cfmPsw = cfmPassword.getText().toString().trim();

            if (!validateInput(email, password, cfmPsw)) {
                progressBar.setVisibility(View.GONE);
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();
                        Usermodel usermodel = new Usermodel(email);
                        db.collection("users").document(userId).set(usermodel).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Register.this, "Registration successful! Please log in.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), Login.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Log.e(TAG, "Error creating user model: ", task.getException());
                                    Toast.makeText(Register.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Log.e(TAG, "Email already in use", task.getException());
                            Toast.makeText(Register.this, "Email already in use.", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "Registration failed", task.getException());
                            Toast.makeText(Register.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        });
    }

    private boolean validateInput(String email, String password, String cfmPsw) {
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email is required");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Invalid email");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password is required");
            return false;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Password must be at least 6 characters");
            return false;
        }

        if (TextUtils.isEmpty(cfmPsw)) {
            cfmPassword.setError("Confirm your password");
            return false;
        }

        if (!password.equals(cfmPsw)) {
            cfmPassword.setError("Passwords do not match");
            return false;
        }

        return true;
    }
}
