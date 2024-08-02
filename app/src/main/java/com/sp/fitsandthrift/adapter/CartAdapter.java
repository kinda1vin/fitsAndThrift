package com.sp.fitsandthrift.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sp.fitsandthrift.R;
import com.sp.fitsandthrift.model.CartItem;

import java.util.List;
import androidx.recyclerview.widget.RecyclerView;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder>{
    private List<CartItem> cartItems;
    private Context context;

    public CartAdapter(Context context, List<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);

        holder.itemDescription.setText(cartItem.getItemDescription());
        Glide.with(context).load(cartItem.getImageUri()).into(holder.itemImage);
        holder.removeBin.setOnClickListener(v -> removeItem(position, cartItem));
    }

    public int getItemCount() {
        return cartItems.size();
    }

    private void removeItem(int position, CartItem item) {
        // Remove item from the list
        cartItems.remove(position);
        notifyItemRemoved(position);

        // Remove item from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();

        db.collection("carts").document(userId).collection("Items").document(item.getItemID())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Item removed from cart", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error removing item", Toast.LENGTH_SHORT).show();
                });
    }

    public void updateList(List<CartItem> newList) {
        cartItems = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemImage;
        private TextView itemDescription;
        private ImageView removeBin;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemDescription = itemView.findViewById(R.id.itemDescription);
            removeBin = itemView.findViewById(R.id.removeBin);
        }
    }
}