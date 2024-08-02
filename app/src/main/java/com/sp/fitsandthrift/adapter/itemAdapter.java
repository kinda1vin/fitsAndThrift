package com.sp.fitsandthrift.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import com.bumptech.glide.Glide;
import com.sp.fitsandthrift.Item;
import com.sp.fitsandthrift.R;
import com.sp.fitsandthrift.selectListener;


public class itemAdapter extends RecyclerView.Adapter<itemAdapter.MyViewHolder> {

    private final selectListener listener;
    private Context context;
    private List<Item> itemList;
    public itemAdapter(Context context, List<Item> itemList, selectListener listener) {
        this.context = context;
        this.itemList = itemList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate((R.layout.item), parent, false);
        return new MyViewHolder(v, listener);
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

    public void updateList(List<Item> newList) {
        itemList = newList;
        notifyDataSetChanged();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView itemDescription;
        ImageView itemImage;

        public MyViewHolder(@NonNull View itemView, selectListener listener) {
            super(itemView);
            itemDescription = itemView.findViewById(R.id.itemDescription);
            itemImage = itemView.findViewById(R.id.itemImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

}