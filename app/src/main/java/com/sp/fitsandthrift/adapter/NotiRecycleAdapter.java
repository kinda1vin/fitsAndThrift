package com.sp.fitsandthrift.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sp.fitsandthrift.Notification;

import java.util.ArrayList;

public class NotiRecycleAdapter extends RecyclerView.Adapter<NotiRecycleAdapter.MyViewHolder> {

    Context context;
    ArrayList<Notification> NotiArrayList;

    public NotiRecycleAdapter(Context context, ArrayList<Notification> NotiArrayList) {
        this.context=context;
        this.NotiArrayList= NotiArrayList;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return NotiArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
