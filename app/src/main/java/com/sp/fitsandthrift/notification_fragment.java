package com.sp.fitsandthrift;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sp.fitsandthrift.adapter.NotiRecycleAdapter;
import com.sp.fitsandthrift.model.Notification;

import java.util.ArrayList;

public class notification_fragment extends Fragment {
    private ArrayList<Notification> NotiArrayList;
    private String[] newsHeading;
    private RecyclerView recycleview;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dataInitialize();
        recycleview = view.findViewById(R.id.notirecycle);
        recycleview.setLayoutManager(new LinearLayoutManager(getContext()));
        recycleview.setHasFixedSize(true);
        NotiRecycleAdapter notiRecycleAdapter=new NotiRecycleAdapter(getContext(),NotiArrayList);
        recycleview.setAdapter(notiRecycleAdapter);
        notiRecycleAdapter.notifyDataSetChanged();
    }
    private void dataInitialize(){
        NotiArrayList = new ArrayList<>();
        newsHeading = new String[]{
                getString(R.string.head_1),
                getString(R.string.head_2),
                getString(R.string.head_3),
                getString(R.string.head_4),
                getString(R.string.head_5),
                "User have accepted ur request",
                "User have declined ur request",
                "User have accepted ur request",
                "User have declined ur request",
                "User have accepted ur request",
                "User have declined ur request",


        };
        int[] ImageId = new int[]{
                R.drawable.profile,
                R.drawable.chat,
                R.drawable.chat,
                R.drawable.bell,
                R.drawable.bell,
                R.drawable.profile,
                R.drawable.chat,
                R.drawable.chat,
                R.drawable.bell,
                R.drawable.bell,
                R.drawable.bell
        };
        for(int i=0; i< newsHeading.length;i++){
            Notification noti= new Notification(newsHeading[i],ImageId[i] );
            NotiArrayList.add(noti);

        }

    }

}