package com.sp.fitsandthrift;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sp.fitsandthrift.model.Usermodel;

public class me_fragment extends Fragment {

    private static final String ARG_NAME = "name";
    private static final String ARG_MAIL = "mail";

    private String name;
    private String mail;
    private FirebaseAuth mAuth;

    private TabHost host;
    private ImageView profilepic;
    private Button editProfile;
    private Button logout;

    private Usermodel usermodel;

    public static me_fragment newInstance(String name, String mail) {
        me_fragment fragment = new me_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, name);
        args.putString(ARG_MAIL, mail);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(ARG_NAME);
            mail = getArguments().getString(ARG_MAIL);
        }
        mAuth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me_fragment, container, false);
        TextView gmailTextView = view.findViewById(R.id.gmail1);
        TextView nameTextView = view.findViewById(R.id.username1);
        profilepic = view.findViewById(R.id.imageView);

        editProfile = view.findViewById(R.id.editprofile);
        logout = view.findViewById(R.id.btnLogout);

        logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getContext(), Login.class);
            startActivity(intent);
            getActivity().finish();
        });

        editProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), editprofile.class);
            startActivityForResult(intent, 1); // Use requestCode 1
        });

        // Initialize TabHost
        host = view.findViewById(R.id.tabhost);
        host.setup();

        // Create "Reviews" tab
        TabHost.TabSpec spec = host.newTabSpec("Reviews");
        spec.setContent(R.id.review_tab);
        spec.setIndicator("Reviews");
        host.addTab(spec);

        // Create "About" tab
        spec = host.newTabSpec("About");
        spec.setContent(R.id.about_tab);
        spec.setIndicator("About");
        host.addTab(spec);

        loadReviewFragment();

        // Load user data when the view is created
        loadUserData(view);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            // Reload user data when returning from edit profile activity
            loadUserData(getView());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload user data when fragment is resumed
        loadUserData(getView());
    }

    private void loadUserData(View view) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(userId);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    usermodel = document.toObject(Usermodel.class);
                    if (usermodel != null) {
                        TextView gmailTextView = view.findViewById(R.id.gmail1);
                        TextView nameTextView = view.findViewById(R.id.username1);

                        gmailTextView.setText(usermodel.getEmail());
                        nameTextView.setText(usermodel.getUsername());

                        if (!TextUtils.isEmpty(usermodel.getProfilePicUrl())) {
                            Uri profilePicUri = Uri.parse(usermodel.getProfilePicUrl());
                            profilepic.setImageURI(profilePicUri);
                        }

                        // Update the AboutFragment with the new details
                        updateAboutFragment(usermodel.getEmail(), usermodel.getPhoneNumber());
                    }
                } else {
                    // Handle the case where the document doesn't exist
                }
            } else {
                // Handle the task failure
            }
        });
    }

    private void loadReviewFragment() {
        getChildFragmentManager().beginTransaction().replace(R.id.review_tab, new review_fragment()).commitAllowingStateLoss();
    }

    private void updateAboutFragment(String email, String phone) {
        about_fragment aboutFragment = new about_fragment();
        Bundle args = new Bundle();
        args.putString("mail", email);
        args.putString("phone", phone);
        aboutFragment.setArguments(args);

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.about_tab, aboutFragment);
        transaction.commitAllowingStateLoss();
    }
}
