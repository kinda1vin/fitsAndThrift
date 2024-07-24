package com.sp.fitsandthrift;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sp.fitsandthrift.model.Usermodel;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class editprofile extends AppCompatActivity {

    private static final String TAG = "EditProfile";

    private EditText editTextName;
    private EditText editTextGmail;
    private EditText editTextGender;
    private EditText editTextph;
    private EditText editTextPassword; // Add password field for re-authentication
    private Button buttonUpdate;
    private ImageView pic;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private Usermodel currentusermodel;
    private Uri selectedImageUri;

    ActivityResultLauncher<Intent> imagePickLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        pic = findViewById(R.id.pic);
        editTextName = findViewById(R.id.newname);
        editTextGmail = findViewById(R.id.newmail);
        editTextGender = findViewById(R.id.gender);
        editTextph = findViewById(R.id.newph);
        editTextPassword = findViewById(R.id.password); // Password field for re-authentication
        buttonUpdate = findViewById(R.id.save_btn);

        // Load current user data
        loadUserData();

        imagePickLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            pic.setImageURI(selectedImageUri);
                        }
                    }
                }
        );

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reAuthenticateAndUpdateProfile();
            }
        });

        pic.setOnClickListener((v) -> {
            ImagePicker.with(this).cropSquare().compress(512).maxResultSize(512, 512).createIntent(new Function1<Intent, Unit>() {
                @Override
                public Unit invoke(Intent intent) {
                    imagePickLauncher.launch(intent);
                    return null;
                }
            });
        });
    }

    private void loadUserData() {
        String userId = mAuth.getCurrentUser().getUid();
        DocumentReference userRef = db.collection("users").document(userId);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    currentusermodel = document.toObject(Usermodel.class);
                    if (currentusermodel != null) {
                        editTextName.setText(currentusermodel.getUsername());
                        editTextGmail.setText(currentusermodel.getEmail());
                        editTextGender.setText(currentusermodel.getGender());
                        editTextph.setText(currentusermodel.getPhoneNumber());
                        if (!TextUtils.isEmpty(currentusermodel.getProfilePicUrl())) {
                            Uri profilePicUri = Uri.parse(currentusermodel.getProfilePicUrl());
                            pic.setImageURI(profilePicUri);
                        }
                    }
                } else {
                    // Handle the case where the document doesn't exist
                    Log.e(TAG, "User document does not exist");
                }
            } else {
                // Handle the task failure
                Log.e(TAG, "Failed to get user document", task.getException());
            }
        });
    }

    private void reAuthenticateAndUpdateProfile() {
        String name = editTextName.getText().toString().trim();
        String gmail = editTextGmail.getText().toString().trim();
        String gender = editTextGender.getText().toString().trim();
        String phone = editTextph.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim(); // Password for re-authentication

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(gmail) || TextUtils.isEmpty(gender) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
            // Show error message if any field is empty
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        currentusermodel.setUsername(name);
        currentusermodel.setEmail(gmail);
        currentusermodel.setGender(gender);
        currentusermodel.setPhoneNumber(phone);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Correct method to get credentials for re-authentication
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Re-authentication successful");
                    updateProfile(gmail);
                } else {
                    Log.e(TAG, "Re-authentication failed", task.getException());
                    Toast.makeText(editprofile.this, "Re-authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e(TAG, "No current user");
            Toast.makeText(this, "No current user", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProfile(String newEmail) {
        if (selectedImageUri != null) {
            StorageReference profilePicRef = storage.getReference().child("profile_pics/" + mAuth.getCurrentUser().getUid() + ".jpg");
            profilePicRef.putFile(selectedImageUri)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                currentusermodel.setProfilePicUrl(uri.toString());
                                updateFirestoreAndAuth(newEmail);
                            });
                        } else {
                            Log.e(TAG, "Failed to upload profile picture", task.getException());
                            Toast.makeText(editprofile.this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            updateFirestoreAndAuth(newEmail);
        }
    }

    private void updateFirestoreAndAuth(String newEmail) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.updateEmail(newEmail).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Email update successful");
                    user.sendEmailVerification().addOnCompleteListener(emailTask -> {
                        if (emailTask.isSuccessful()) {
                            Toast.makeText(editprofile.this, "Verification email sent to " + newEmail, Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "Failed to send verification email", emailTask.getException());
                            Toast.makeText(editprofile.this, "Failed to send verification email: " + emailTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    String userId = user.getUid();
                    DocumentReference userRef = db.collection("users").document(userId);
                    userRef.set(currentusermodel).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(editprofile.this, "Profile updated", Toast.LENGTH_SHORT).show();
                            setResult(Activity.RESULT_OK);
                            finish();
                        } else {
                            Log.e(TAG, "Failed to update profile in Firestore", task1.getException());
                            Toast.makeText(editprofile.this, "Failed to update profile in Firestore", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Log.e(TAG, "Email already in use", task.getException());
                        Toast.makeText(editprofile.this, "Email already in use.", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "Failed to update email in Firebase Auth", task.getException());
                        Toast.makeText(editprofile.this, "Failed to update email in Firebase Auth: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Log.e(TAG, "No current user to update email");
            Toast.makeText(this, "No current user to update email", Toast.LENGTH_SHORT).show();
        }
    }
}




