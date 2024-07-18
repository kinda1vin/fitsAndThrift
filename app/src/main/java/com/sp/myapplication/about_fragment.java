package com.sp.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jetbrains.annotations.Nullable;

public class about_fragment extends Fragment {
    private static final String ARG_MAIL = "mail";
    private static final String ARG_PH = "phone";
    private String mail;
    private String phone;

    public static about_fragment newInstance(String mail, String phone) {
        about_fragment fragment = new about_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_MAIL, mail);
        args.putString(ARG_PH, phone);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            mail = getArguments().getString(ARG_MAIL);
            phone = getArguments().getString(ARG_PH);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about_fragment, container, false);
        TextView mailtext = view.findViewById(R.id.gmail1);
        TextView phonetext = view.findViewById(R.id.phone);
        mailtext.setText(mail);
        phonetext.setText(phone);
        return view;
    }
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        if (getView() != null) {
            TextView mailtext = getView().findViewById(R.id.gmail1);
            TextView phonetext = getView().findViewById(R.id.phone);
            mailtext.setText(mail);
            phonetext.setText(phone);
        }
    }
}
