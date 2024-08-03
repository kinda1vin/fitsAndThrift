package com.sp.fitsandthrift;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sp.fitsandthrift.model.Usermodel;

public class editprofile extends AppCompatActivity {

    private static final String TAG = "EditProfile";
    private EditText editTextName, editTextph;
    private Spinner genderSpinner;
    private Button buttonUpdate;
    private ImageView pic;
    private ProgressBar progressBar;
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

        initView();
        loadUserData();
        setupListeners();
    }

    private void initView() {
        pic = findViewById(R.id.pic);
        editTextName = findViewById(R.id.newname);
        genderSpinner = findViewById(R.id.gender_spinner);
        editTextph = findViewById(R.id.newph);
        buttonUpdate = findViewById(R.id.save_btn);
        progressBar = findViewById(R.id.profile_progress_bar);

        // Initialize the back button
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());



        // Set up Spinner with gender options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);

        imagePickLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            selectedImageUri = data.getData();
                            setProfilePic(this, selectedImageUri, pic);
                        }
                    }
                }
        );
    }

    private void setupListeners() {
        buttonUpdate.setOnClickListener(v -> {
            if (!validateInputs()) return;
            setInProgress(true);
            updateProfile();
        });
        pic.setOnClickListener(v -> {
            ImagePicker.with(this)
                    .cropSquare()
                    .compress(512)
                    .maxResultSize(512, 512)
                    .createIntent(intent -> {
                        imagePickLauncher.launch(intent);
                        return null;
                    });
        });
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(editTextName.getText()) ||
                TextUtils.isEmpty(editTextph.getText())) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void updateProfile() {
        // Extract gender from Spinner
        String selectedGender = genderSpinner.getSelectedItem().toString();
        currentusermodel.setGender(selectedGender);

        if (selectedImageUri != null) {
            updateProfilePictureAndUserData();
        } else {
            updateFirestoreWithNewData(currentusermodel.getProfilePicUrl()); // Use existing profile pic URL
        }
    }

    private void updateProfilePictureAndUserData() {
        StorageReference profilePicRef = storage.getReference().child("profile_pics/" + mAuth.getCurrentUser().getUid() + ".jpg");
        profilePicRef.putFile(selectedImageUri)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            currentusermodel.setProfilePicUrl(uri.toString());
                            updateFirestoreWithNewData(uri.toString());
                        });
                    } else {
                        Toast.makeText(editprofile.this, "Failed to upload profile picture", Toast.LENGTH_SHORT).show();
                        setInProgress(false);
                    }
                });
    }

    private void updateFirestoreWithNewData(String imageUrl) {
        DocumentReference userRef = db.collection("users").document(mAuth.getCurrentUser().getUid());

        userRef.update("profilePicUrl", imageUrl,
                        "gender", currentusermodel.getGender(),
                        "username", editTextName.getText().toString(),
                        "phoneNumber", editTextph.getText().toString())
                .addOnCompleteListener(task -> {
                    setInProgress(false);
                    if (task.isSuccessful()) {
                        Toast.makeText(editprofile.this, "Profile updated", Toast.LENGTH_SHORT).show();
                        setResult(Activity.RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(editprofile.this, "Failed to update profile in Firestore: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setInProgress(boolean inProgress) {
        progressBar.setVisibility(inProgress ? View.VISIBLE : View.GONE);
        buttonUpdate.setEnabled(!inProgress);
    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            DocumentReference userRef = db.collection("users").document(user.getUid());
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.getData() != null) {
                        currentusermodel = document.toObject(Usermodel.class);
                        if (currentusermodel != null) {
                            editTextName.setText(currentusermodel.getUsername());
                            editTextph.setText(currentusermodel.getPhoneNumber());

                            // Set Spinner to current gender
                            if (!TextUtils.isEmpty(currentusermodel.getGender())) {
                                int spinnerPosition = ((ArrayAdapter) genderSpinner.getAdapter()).getPosition(currentusermodel.getGender());
                                genderSpinner.setSelection(spinnerPosition);
                            }

                            // Load and display the current profile picture
                            if (!TextUtils.isEmpty(currentusermodel.getProfilePicUrl())) {
                                Uri profilePicUri = Uri.parse(currentusermodel.getProfilePicUrl());
                                setProfilePic(this, profilePicUri, pic);
                            } else {
                                // Set a placeholder if no image URL is available
                                pic.setImageResource(R.drawable.profile1); // Make sure you have this placeholder image
                            }
                        }
                    } else {
                        Log.e(TAG, "User document does not exist");
                    }
                } else {
                    Log.e(TAG, "Failed to get user document", task.getException());
                }
            });
        }
    }

    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView) {
        Glide.with(context)
                .load(imageUri)
                .apply(RequestOptions.circleCropTransform()
                        .placeholder(R.drawable.profile1) // Placeholder image
                        .error(R.drawable.profile)) // Error image if loading fails
                .into(imageView);
    }

    @Override

    public void onBackPressed() {
        Intent intent = new Intent(editprofile.this, MainActivity.class);
        intent.putExtra("loadMeFragment", true);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

}