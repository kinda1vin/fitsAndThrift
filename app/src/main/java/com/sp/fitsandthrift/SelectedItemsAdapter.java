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

public class SelectedItemsAdapter extends RecyclerView.Adapter<SelectedItemsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Item> selectedItems;

    public SelectedItemsAdapter(Context context, ArrayList<Item> selectedItems) {
        this.context = context;
        this.selectedItems = selectedItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.selected_items_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = selectedItems.get(position);

        Glide.with(context).load(item.getImageUri()).into(holder.itemImage);
    }

    @Override
    public int getItemCount() {
        return selectedItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView selectedCard;
        ImageView itemImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            selectedCard = itemView.findViewById(R.id.selectedCard);
            itemImage = itemView.findViewById(R.id.item_imageView);

        }
    }
}
