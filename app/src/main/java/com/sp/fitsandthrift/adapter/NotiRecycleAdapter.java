package com.sp.fitsandthrift.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.sp.fitsandthrift.model.Notification;
import com.sp.fitsandthrift.R;

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
        View v= LayoutInflater.from(context).inflate(R.layout.notii_item,parent,false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Notification noti= NotiArrayList.get(position);
        holder.tvHeading.setText(noti.getHeading());
        holder.titleImage.setImageResource(noti.getTitleImage());


    }

    @Override
    public int getItemCount() {
        return NotiArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tvHeading;
        ShapeableImageView titleImage;

        public MyViewHolder(@NonNull View itemView) {

            super(itemView);
            titleImage= itemView.findViewById(R.id.title_image);
            tvHeading= itemView.findViewById(R.id.tvHeading);
        }
    }
}