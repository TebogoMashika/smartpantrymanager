package com.school.smartpantrymanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.smartpantrymanager.R;
import com.school.smartpantrymanager.model.PantryItem;

import java.util.List;

/**
 * Adapter used to display pantry items in a RecyclerView.
 */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    private final List<PantryItem> pantryItems;
    private final OnPantryItemClickListener listener;

    /**
     * Listener for pantry item actions.
     */
    public interface OnPantryItemClickListener {

        void onEdit(PantryItem pantryItem);

        void onDelete(PantryItem pantryItem);
    }

    /**
     * Creates a pantry adapter.
     *
     * @param pantryItems pantry items to display
     * @param listener listener for edit and delete actions
     */
    public PantryAdapter(List<PantryItem> pantryItems, OnPantryItemClickListener listener) {
        this.pantryItems = pantryItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_pantry,
                parent,
                false
        );

        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {

        PantryItem pantryItem = pantryItems.get(position);

        holder.nameTextView.setText(pantryItem.getName());

        String quantityText = pantryItem.getQuantity() + " " + pantryItem.getUnit();

        holder.quantityTextView.setText(quantityText);

        if (pantryItem.getExpiryDate() == null || pantryItem.getExpiryDate().isEmpty()) {
            holder.expiryTextView.setText("No expiry date");
        } else {
            holder.expiryTextView.setText("Expires: " + pantryItem.getExpiryDate());
        }

        holder.itemView.setOnClickListener(view -> listener.onEdit(pantryItem));

        holder.itemView.setOnLongClickListener(view -> {
            listener.onDelete(pantryItem);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return pantryItems.size();
    }

    /**
     * View holder for pantry items.
     */
    public static class PantryViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        TextView quantityTextView;
        TextView expiryTextView;

        public PantryViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.textViewPantryName);
            quantityTextView = itemView.findViewById(R.id.textViewPantryQuantity);
            expiryTextView = itemView.findViewById(R.id.textViewPantryExpiry);
        }
    }
}