package com.sp.fitsandthrift;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;
import com.sp.fitsandthrift.model.Usermodel;
import com.google.android.material.textfield.TextInputEditText;

public class Get_otp extends AppCompatActivity {

    CountryCodePicker countryCode;
    private TextInputEditText phoneNum;
    private ProgressBar getOTP_progressBar;

    private Button getOTP;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_otp);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        phoneNum = findViewById(R.id.phoneNum);
        getOTP_progressBar = findViewById(R.id.getOTP_progressBar);
        getOTP_progressBar.setVisibility(View.GONE);

        countryCode = findViewById(R.id.countryCode);
        countryCode.registerCarrierNumberEditText(phoneNum);

        getOTP = findViewById(R.id.getOTP);

        getOTP.setOnClickListener((v) -> {
            if(!countryCode.isValidFullNumber()) {
                phoneNum.setError("Enter a valid phone number");
                return;
            }

            String phoneNumber = countryCode.getFullNumberWithPlus();
            db.collection("verifiedNumbers").document(phoneNumber).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Toast.makeText(Get_otp.this, "This phone number has already been verified.", Toast.LENGTH_SHORT).show();
                    } else {

                        // When starting Register activity
                        Intent intentRegister = new Intent(Get_otp.this, Register.class);
                        intentRegister.putExtra("phoneNumber", phoneNumber);
                        startActivity(intentRegister);

                    }
                } else {
                    Toast.makeText(Get_otp.this, "Failed to check phone number.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

}

