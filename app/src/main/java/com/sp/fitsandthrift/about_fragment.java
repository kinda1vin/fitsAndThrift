package com.sp.fitsandthrift;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class about_fragment extends Fragment {
    private String mail;
    private String phone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mail = getArguments().getString("mail");
            phone = getArguments().getString("phone");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about_fragment, container, false);
        TextView mailtext = view.findViewById(R.id.gmail1);
        TextView phonetext = view.findViewById(R.id.phone);
        mailtext.setText(mail);
        phonetext.setText(phone);
        return view;
    }

    @Override
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
