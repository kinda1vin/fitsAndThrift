package com.sp.fitsandthrift;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class SubmitOTP extends AppCompatActivity {

    String phoneNumber;
    Long timeoutSeconds = 60L;
    String verificationCode;
    PhoneAuthProvider.ForceResendingToken resendingToken;
    TextInputEditText otpNum;
    Button submitOTP;
    ProgressBar submitOTP_progressBar;
    TextView resendOTP;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance(); // Initialize db

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_otp);

        otpNum = findViewById(R.id.otpNum);
        resendOTP = findViewById(R.id.resendOTP);
        submitOTP_progressBar = findViewById(R.id.submitOTP_progressBar);

        submitOTP = findViewById(R.id.submitOTP);

        phoneNumber = getIntent().getStringExtra("phoneNumber");

        sendOTP(phoneNumber, false);

        resendOTP.setOnClickListener(v -> {
            sendOTP(phoneNumber,true);
        });

        submitOTP.setOnClickListener(v -> {
            String otp = otpNum.getText().toString();
            if (otp.isEmpty()) {
                otpNum.setError("Enter OTP");
                return;
            }

            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, otp);
            mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "OTP verified", Toast.LENGTH_SHORT).show();

                    // Add the phone number to the verifiedNumbers collection
                    db.collection("verifiedNumbers").document(phoneNumber).set(new HashMap<>()).addOnCompleteListener(task2 -> {
                        if (!task2.isSuccessful()) {
                            Toast.makeText(SubmitOTP.this, "Failed to store phone number.", Toast.LENGTH_SHORT).show();
                        }
                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    Toast.makeText(this, "OTP verification failed", Toast.LENGTH_SHORT).show();
                }
            });
        });




    }

    void sendOTP(String phoneNumber,boolean isResend) {
        startResendTimer();
        setInProgress(true);
        PhoneAuthOptions.Builder builder = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        setInProgress(false);
                        Toast.makeText(SubmitOTP.this, "Verification completed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        setInProgress(false);
                        Toast.makeText(SubmitOTP.this, "Verification failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationCode = s;
                        resendingToken = forceResendingToken;
                        setInProgress(false);
                        Toast.makeText(SubmitOTP.this, "Code sent successfully", Toast.LENGTH_SHORT).show();
                    }
                });

        if(isResend){
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        }else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }

    }

    void setInProgress (boolean inProgress) {
        if(inProgress) {
            submitOTP_progressBar.setVisibility(View.VISIBLE);
            submitOTP.setEnabled(false);
        } else {
            submitOTP_progressBar.setVisibility(View.GONE);
            submitOTP.setEnabled(true);
        }
    }

    void startResendTimer(){
        resendOTP.setEnabled(false);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                resendOTP.setText("Resend OTP in "+timeoutSeconds+" seconds");
                timeoutSeconds--;
                if(timeoutSeconds<=0){
                    timeoutSeconds = 60L;
                    timer.cancel();
                    runOnUiThread(()->{

                        resendOTP.setEnabled(true);
                    });
                }
            }
        },0,1000);

    }

}