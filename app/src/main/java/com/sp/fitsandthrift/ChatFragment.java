package com.sp.fitsandthrift;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ChatFragment extends Fragment {

    ImageView searchChat;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        searchChat = rootView.findViewById(R.id.searchChat);
        searchChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Search for user
                Intent intent = new Intent(getActivity(), SearchUser.class);
                startActivity(intent);
            }
        });


        return rootView;

    }
}