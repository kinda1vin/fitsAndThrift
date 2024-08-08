package com.sp.fitsandthrift;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sp.fitsandthrift.model.Usermodel;

public class otheruser_fragment extends Fragment {
    private static final String ARG_OTHERUSERNAME = "otherusername";
    private static final String ARG_OTHERUSERGMAIL = "otherusergmail";
    private static final String ARG_OTHERUSERID = "otheruserid";

    private ImageView profilePic;
    private TextView usernameTextView, emailTextView;
    private ImageView chatIcon;
    private FirebaseFirestore db;
    private Usermodel otherUserModel; // Add this field

    public static otheruser_fragment newInstance(String otherusername, String otherusergmail, String otheruserid) {
        otheruser_fragment fragment = new otheruser_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_OTHERUSERNAME, otherusername);
        args.putString(ARG_OTHERUSERGMAIL, otherusergmail);
        args.putString(ARG_OTHERUSERID, otheruserid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.other_user_fragment, container, false);
        initializeViews(view);
        setupTabHost(view);
        if (getArguments() != null) {
            String userId = getArguments().getString(ARG_OTHERUSERID);
            loadUserData(userId);
        }
        return view;
    }

    private void initializeViews(View view) {
        profilePic = view.findViewById(R.id.otheruserpfp);
        usernameTextView = view.findViewById(R.id.otherusername);
        emailTextView = view.findViewById(R.id.otherusergmail);
        chatIcon = view.findViewById(R.id.otherchat);

        chatIcon.setOnClickListener(v -> {
            if (otherUserModel != null) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("username", otherUserModel.getUsername());
                intent.putExtra("email", otherUserModel.getEmail());
                intent.putExtra("profilePicUrl", otherUserModel.getProfilePicUrl());
                intent.putExtra("userId", otherUserModel.getCurrentUserId());
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "User information is not available yet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupTabHost(View view) {
        TabHost host = view.findViewById(R.id.tabhost1);
        host.setup();

        TabHost.TabSpec spec = host.newTabSpec("Reviews");
        spec.setContent(R.id.other_review_tab);
        spec.setIndicator(createTabIndicator("Review"));
        host.addTab(spec);

        spec = host.newTabSpec("About");
        spec.setContent(R.id.other_about_tab);
        spec.setIndicator(createTabIndicator("About"));
        host.addTab(spec);

        loadReviewFragment();
    }
    private View createTabIndicator(String title) {
        TextView tabIndicator = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.tab_item, null);
        tabIndicator.setText(title);
        return tabIndicator;
    }

    private void loadUserData(String userId) {
        DocumentReference userRef = db.collection("users").document(userId);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Usermodel usermodel = document.toObject(Usermodel.class);
                    updateUI(usermodel);
                    otherUserModel = usermodel; // Store the other user model
                } else {
                    Log.e("otheruser_fragment", "No such document");
                }
            } else {
                Log.e("otheruser_fragment", "Failed to load user data", task.getException());
            }
        });
    }

    private void updateUI(Usermodel usermodel) {
        if (usermodel != null) {
            usernameTextView.setText(usermodel.getUsername());
            emailTextView.setText(usermodel.getEmail());
            if (!TextUtils.isEmpty(usermodel.getProfilePicUrl())) {
                Uri profilePicUri = Uri.parse(usermodel.getProfilePicUrl());
                setProfilePic(getActivity(), profilePicUri, profilePic);
            }
            updateAboutFragment(usermodel.getEmail(), usermodel.getPhoneNumber());
        }
    }

    private void updateAboutFragment(String email, String phone) {
        about_fragment aboutFragment = (about_fragment) getChildFragmentManager().findFragmentById(R.id.other_about_tab);
        if (aboutFragment != null) {
            aboutFragment.updateDetails(email, phone);
        } else {
            aboutFragment = new about_fragment();
            Bundle args = new Bundle();
            args.putString("mail", email);
            args.putString("phone", phone);
            aboutFragment.setArguments(args);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.other_about_tab, aboutFragment);
            transaction.commit();
        }
    }

    private void loadReviewFragment() {
        otheruser_review reviewFragment = otheruser_review.newInstance(getArguments().getString(ARG_OTHERUSERID));
        getChildFragmentManager().beginTransaction().replace(R.id.other_review_tab, reviewFragment).commit();
    }

    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView) {
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).placeholder(R.drawable.baseline_account_box_24).into(imageView);
    }
}