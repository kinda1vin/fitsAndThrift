package com.sp.fitsandthrift;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class DesiredItemsAdapter extends RecyclerView.Adapter<DesiredItemsAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Item> desiredItems;

    public DesiredItemsAdapter(Context context, ArrayList<Item> desiredItems) {
        this.context = context;
        this.desiredItems = desiredItems;
    }


    @NonNull
    @Override
    public DesiredItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.desired_items_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DesiredItemsAdapter.ViewHolder holder, int position) {
        Item item = desiredItems.get(position);

        Glide.with(context).load(item.getImageUri()).into(holder.itemImage);

    }

    @Override
    public int getItemCount() {
        return desiredItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CardView desiredCard;
        ImageView itemImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            desiredCard = itemView.findViewById(R.id.desiredCard);
            itemImage = itemView.findViewById(R.id.itemImageView);
        }
    }
}
