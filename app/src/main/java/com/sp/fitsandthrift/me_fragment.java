package com.sp.fitsandthrift;

import android.content.Intent;
import android.os.Bundle;
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

import com.google.firebase.auth.FirebaseAuth;
import com.sp.fitsandthrift.model.Usermodel;

public class me_fragment extends Fragment {

    private static final String ARG_NAME = "name";
    private static final String ARG_MAIL = "mail";

    private TabHost host;
    private ImageView profilepic;
    private Button editProfile;
    private Button uploadItem;
    private Button logout;

    private String mail;
    private String name;
    Usermodel usermodel;

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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me_fragment, container, false);
        TextView gmailTextView = view.findViewById(R.id.gmail1);

        editProfile = view.findViewById(R.id.editprofile);
        logout = view.findViewById(R.id.btnLogout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), Login.class);
                startActivity(intent);

            }
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

        getChildFragmentManager().beginTransaction().replace(R.id.review_tab, new review_fragment()).commit();

        host.setOnTabChangedListener(tabId -> {
            if ("Reviews".equals(tabId)) {
                getChildFragmentManager().beginTransaction().replace(R.id.review_tab, new review_fragment()).commit();
            } else if ("About".equals(tabId)) {
                about_fragment aboutFragment = about_fragment.newInstance(mail, name);
                getChildFragmentManager().beginTransaction().replace(R.id.about_tab, aboutFragment).commit();
            }
        });

        gmailTextView.setText(mail);

        return view;
    }
}
