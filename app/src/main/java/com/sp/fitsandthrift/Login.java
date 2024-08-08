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

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.sp.fitsandthrift.model.Usermodel;

public class Login extends AppCompatActivity {
    private TextInputEditText editTextEmail, editTextPassword;
    private Button btnLogin;
    private FirebaseAuth mAuth;
    private TextView registerNow;
    private TextView forgotPassword;
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
        TextView forgotPassword = findViewById(R.id.forgotPassword);

        registerNow.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Get_otp.class);
            startActivity(intent);

        });

        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
            startActivity(intent);
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
                            Toast.makeText(Login.this, "Account login successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(Login.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

}

