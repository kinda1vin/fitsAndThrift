package com.sp.fitsandthrift;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class QuantityAdapter extends RecyclerView.Adapter<QuantityAdapter.ViewHolder>{

    Context context;
    ArrayList<Item> quantityList;
    QuantityListener quantityListener;
    ArrayList<String> selectedItems = new ArrayList<>();

    public QuantityAdapter(Context context, ArrayList<Item> quantityList, QuantityListener quantityListener) {
        this.context = context;
        this.quantityList = quantityList;
        this.quantityListener = quantityListener;
    }

    @NonNull
    @Override
    public QuantityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.trade_request_item_list_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuantityAdapter.ViewHolder holder, int position) {
        if (quantityList != null && !quantityList.isEmpty()) {
            Item item = quantityList.get(position);

            holder.itemDescription.setText(item.itemDescription);
            Glide.with(context).load(item.imageUri).into(holder.itemImage);

            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.checkBox.isChecked()) {
                        selectedItems.add(item.getItemID());
                    } else {
                        selectedItems.remove(item.getItemID());
                    }
                    quantityListener.onQuantityChanged(selectedItems);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return quantityList.size();
    }

    public ArrayList<String> getSelectedItems() {
        return selectedItems;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        ImageView itemImage;
        TextView itemDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox);
            itemImage = itemView.findViewById(R.id.item_image);
            itemDescription = itemView.findViewById(R.id.item_list_description);
        }
    }
}
