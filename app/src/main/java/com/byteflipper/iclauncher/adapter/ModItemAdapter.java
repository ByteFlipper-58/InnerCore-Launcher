package com.byteflipper.iclauncher.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.byteflipper.iclauncher.ModItem;
import com.byteflipper.iclauncher.R;
import com.google.android.material.chip.Chip;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ModItemAdapter extends RecyclerView.Adapter<ModItemAdapter.ModItemViewHolder> {
    private List<ModItem> modItemList;

    public ModItemAdapter(List<ModItem> modItemList) {
        this.modItemList = modItemList;
    }

    @NonNull
    @Override
    public ModItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.mod_item_layout, parent, false);
        return new ModItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ModItemViewHolder holder, int position) {
        ModItem modItem = modItemList.get(position);
        holder.titleTextView.setText(modItem.getTitle());
        holder.descriptionTextView.setText(modItem.getDescription());
        holder.likesCount.setText(String.valueOf(modItem.getLikes()));
        Picasso.get().load("https://icmods.mineprogramming.org/api/img/" + modItem.getIcon()).into(holder.iconImageView);
    }

    @Override
    public int getItemCount() {
        return modItemList.size();
    }

    public class ModItemViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView descriptionTextView;
        public Chip likesCount;
        public ImageView iconImageView;

        public ModItemViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            likesCount = itemView.findViewById(R.id.likesCount);
            iconImageView = itemView.findViewById(R.id.iconImageView);
        }
    }

    public void setModItemList(List<ModItem> modItemList) {
        this.modItemList = modItemList;
        notifyDataSetChanged();
    }
}