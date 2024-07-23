package com.sp.fitsandthrift;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sp.fitsandthrift.model.Usermodel;

public class Login extends AppCompatActivity {
    private TextInputEditText editTextEmail, editTextPassword;
    private Button btnLogin;
    private FirebaseAuth mAuth;
    private TextView registerNow;
    private ProgressBar progressBar;
    private Usermodel usermodel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.em);
        editTextPassword = findViewById(R.id.psw);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        registerNow = findViewById(R.id.registerNow);

        registerNow.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Register.class);
            startActivity(intent);
            finish();
        });

        btnLogin.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();

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

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            checkUserDetails();
                        } else {
                            Toast.makeText(Login.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void checkUserDetails() {
        String userId = mAuth.getCurrentUser().getUid();
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(userId);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    usermodel = document.toObject(Usermodel.class);
                    if (usermodel != null && !usermodel.isDetailsProvided()) {
                        Intent intent = new Intent(Login.this, user_details.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);
                    }
                    finish();
                } else {
                    Toast.makeText(Login.this, "Failed to get user details.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Login.this, "Failed to get user details.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

