package com.sp.fitsandthrift;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class about_fragment extends Fragment {

    private TextView mailTextView;
    private TextView phoneTextView;

    public about_fragment() {

    }

    public static about_fragment newInstance(String email, String phone) {
        about_fragment fragment = new about_fragment();
        Bundle args = new Bundle();
        args.putString("mail", email);
        args.putString("phone", phone);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            updateDetails(getArguments().getString("mail"), getArguments().getString("phone"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_fragment, container, false);
        mailTextView = view.findViewById(R.id.gmail1);
        phoneTextView = view.findViewById(R.id.phone);

        // Refresh views with the latest arguments if available
        if (getArguments() != null) {
            updateDetails(getArguments().getString("mail"), getArguments().getString("phone"));
        }

        return view;
    }

    // Method to update details dynamically
    public void updateDetails(String email, String phone) {
        if (mailTextView != null && phoneTextView != null) {
            mailTextView.setText(email);
            phoneTextView.setText(phone);
        }
    }
}
