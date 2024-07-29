package com.sp.fitsandthrift;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.sp.fitsandthrift.Firebase.Util;
import com.sp.fitsandthrift.adapter.RecentChatRecyclerAdapter;
import com.sp.fitsandthrift.adapter.SearchUserRecyclerAdapter;
import com.sp.fitsandthrift.model.ChatroomModel;
import com.sp.fitsandthrift.model.Usermodel;

public class ChatFragment extends Fragment {

    ImageView searchChat;
    RecentChatRecyclerAdapter adapter;

    RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        searchChat = rootView.findViewById(R.id.searchChat);
        recyclerView = rootView.findViewById(R.id.chatRecyclerView); // Use class field instead of local variable


        searchChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Search for user
                Intent intent = new Intent(getActivity(), SearchUser.class);
                startActivity(intent);
            }
        });

        setupRecyclerView();

        return rootView;

    }
    void setupRecyclerView(){

        Query query = Util.allChatroomCollectionReference()
                .whereArrayContains("userIds", Util.currentUserId())
                .orderBy("lastMessageTimestamp",Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatroomModel> options = new FirestoreRecyclerOptions.Builder<ChatroomModel>()
                .setQuery(query, ChatroomModel.class).build();


        adapter = new RecentChatRecyclerAdapter( options, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext() ));
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    public void onStart() {
        super.onStart();
        if(adapter != null)
            adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(adapter != null)
            adapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adapter != null)
            adapter.notifyDataSetChanged();
    }
}