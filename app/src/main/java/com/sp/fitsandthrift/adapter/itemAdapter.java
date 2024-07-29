package com.sp.fitsandthrift.adapter;

import android.content.ClipData;
import android.content.Context;
import android.location.GnssAntennaInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.auth.User;

import java.time.format.TextStyle;
import java.util.List;
import com.bumptech.glide.Glide;
import com.sp.fitsandthrift.Item;
import com.sp.fitsandthrift.R;


public class itemAdapter extends RecyclerView.Adapter<itemAdapter.MyViewHolder> {

    private Context context;
    private List<Item> itemList;

    public itemAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }


    @NonNull
    @Override
    //new code
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate((R.layout.item), parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.itemDescription.setText(item.getItemDescription());

        Glide.with(context).load(item.getImageUri()).into(holder.itemImage);

    }

    @Override
    public int getItemCount() {

        return itemList.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView itemDescription;
        ImageView itemImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemDescription = itemView.findViewById(R.id.itemDescription);
            itemImage = itemView.findViewById(R.id.itemImage);
        }
    }

}