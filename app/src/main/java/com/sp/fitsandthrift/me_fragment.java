package com.sp.fitsandthrift;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sp.fitsandthrift.model.Usermodel;

public class me_fragment extends Fragment {
    private static final String ARG_EMAIL = "email";

    private ImageView profilePic;
    private TextView usernameTextView, emailTextView;
    private Button editProfileButton, logoutButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // Updated newInstance method to handle email parameter correctly
    public static me_fragment newInstance(String email) {
        me_fragment fragment = new me_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me_fragment, container, false);
        initializeViews(view);
        setupTabHost(view);
        if (getArguments() != null) {
            String email = getArguments().getString(ARG_EMAIL);
            emailTextView.setText(email); // Set the email directly if it's passed via newInstance
        }
        loadUserData();
        return view;
    }

    private void initializeViews(View view) {
        profilePic = view.findViewById(R.id.imageView);
        usernameTextView = view.findViewById(R.id.username1);
        emailTextView = view.findViewById(R.id.gmail1);
        editProfileButton = view.findViewById(R.id.editprofile);
        logoutButton = view.findViewById(R.id.btnLogout);

        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getActivity(), Login.class));
            getActivity().finish();
        });

        editProfileButton.setOnClickListener(v -> startActivityForResult(new Intent(getActivity(), editprofile.class), 1));
    }

    private void setupTabHost(View view) {
        TabHost host = view.findViewById(R.id.tabhost);
        host.setup();

        TabHost.TabSpec spec = host.newTabSpec("Reviews");
        spec.setContent(R.id.review_tab);
        spec.setIndicator("Reviews");
        host.addTab(spec);

        spec = host.newTabSpec("About");
        spec.setContent(R.id.about_tab);
        spec.setIndicator("About");
        host.addTab(spec);

        loadReviewFragment();
    }

    private void loadUserData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userRef = db.collection("users").document(userId);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Usermodel usermodel = document.toObject(Usermodel.class);
                    updateUI(usermodel);
                } else {
                    Log.e("me_fragment", "No such document");
                }
            } else {
                Log.e("me_fragment", "Failed to load user data", task.getException());
            }
        });
    }

    private void updateUI(Usermodel usermodel) {
        usernameTextView.setText(usermodel.getUsername());
        if (!TextUtils.isEmpty(usermodel.getProfilePicUrl())) {
            Uri profilePicUri = Uri.parse(usermodel.getProfilePicUrl());
            setProfilePic(getActivity(), profilePicUri, profilePic);
        }
        updateAboutFragment(usermodel.getEmail(), usermodel.getPhoneNumber());
    }

    private void updateAboutFragment(String email, String phone) {
        about_fragment aboutFragment = (about_fragment) getChildFragmentManager().findFragmentById(R.id.about_tab);
        if (aboutFragment != null) {
            aboutFragment.updateDetails(email, phone);
        } else {
            aboutFragment = new about_fragment();
            Bundle args = new Bundle();
            args.putString("mail", email);
            args.putString("phone", phone);
            aboutFragment.setArguments(args);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.about_tab, aboutFragment);
            transaction.commit();
        }
    }

    private void loadReviewFragment() {
        review_fragment reviewFragment = new review_fragment();
        getChildFragmentManager().beginTransaction().replace(R.id.review_tab, reviewFragment).commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            loadUserData(); // Reload user data when returning from edit profile activity
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserData(); // Ensure updated data is shown upon returning
    }

    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView) {
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }
}
