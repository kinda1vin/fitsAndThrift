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
import com.sp.fitsandthrift.Firebase.Util;
import com.sp.fitsandthrift.model.Usermodel;

public class Register extends AppCompatActivity {

    private TextInputEditText editTextEmail, editTextPassword, cfmPassword;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    private TextView loginNow;
    private ProgressBar progressBar;

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
            String name,phone,gender=null;
            progressBar.setVisibility(View.VISIBLE);
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();
            String cfmPsw = cfmPassword.getText().toString();

            if (TextUtils.isEmpty(email)) {
                editTextEmail.setError(getString(R.string.email_empty_error));
                progressBar.setVisibility(View.GONE);
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editTextEmail.setError(getString(R.string.email_invalid_error));
                progressBar.setVisibility(View.GONE);
                return;
            }

            if (TextUtils.isEmpty(password)) {
                editTextPassword.setError(getString(R.string.password_empty_error));
                progressBar.setVisibility(View.GONE);
                return;
            }

            if (password.length() < 6) {
                editTextPassword.setError(getString(R.string.password_length_error));
                progressBar.setVisibility(View.GONE);
                return;
            }

            if (TextUtils.isEmpty(cfmPsw)) {
                cfmPassword.setError(getString(R.string.cfm_password_empty_error));
                progressBar.setVisibility(View.GONE);
                return;
            }

            if (!password.equals(cfmPsw)) {
                cfmPassword.setError(getString(R.string.password_mismatch_error));
                progressBar.setVisibility(View.GONE);
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) { //
                        Usermodel usermodel = new Usermodel(email);// create a new user model in database when signin process is successful
                        Util.currentUserDetails().set(usermodel).addOnCompleteListener(userTask -> {
                            if (userTask.isSuccessful()) {
                                Log.d(TAG, "User document added to Firestore");
                                Toast.makeText(Register.this, R.string.account_created, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), Login.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Log.e(TAG, "Failed to add user document to Firestore", userTask.getException());
                                Toast.makeText(Register.this, R.string.registration_failed, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Log.e(TAG, "Email already in use", task.getException());
                            Toast.makeText(Register.this, R.string.email_already_in_use, Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "Registration failed", task.getException());
                            Toast.makeText(Register.this, R.string.registration_failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        });
    }
}