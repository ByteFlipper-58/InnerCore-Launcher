package com.byteflipper.iclauncher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.byteflipper.iclauncher.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ScreenshotsAdapter extends RecyclerView.Adapter<ScreenshotsAdapter.ViewHolder> {
    private Context context;
    private List<String> arrayList;

    private OnItemClickListener onItemClickListener;

    public ScreenshotsAdapter(Context context, List<String> arrayList) {
        this.context = context;
        this.arrayList = arrayList != null ? arrayList : new ArrayList<>();
    }

    // Метод для обновления списка скриншотов
    public void setScreenshotsList(List<String> screenshotsList) {
        this.arrayList = screenshotsList;
        notifyDataSetChanged();
    }

    // Создание нового ViewHolder при необходимости
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_item_layout, parent, false);
        return new ViewHolder(view);
    }

    // Привязка данных к ViewHolder в указанной позиции
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Используем Picasso для загрузки изображения из URL и отображения его в ImageView
        Picasso.get().load("https://icmods.mineprogramming.org/api/img/" + arrayList.get(position)).into(holder.imageView);
        // Устанавливаем обработчик нажатия на элемент списка
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(view -> onItemClickListener.onClick(holder.imageView, arrayList.get(position)));
        }
    }

    // Возвращает общее количество элементов в списке
    @Override
    public int getItemCount() {
        return arrayList != null ? arrayList.size() : 0;
    }

    // ViewHolder представляет элемент списка и содержит его Views
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_item);
        }
    }

    // Устанавливаем обработчик нажатия на элемент списка
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    // Интерфейс для обработки нажатия на элемент списка
    public interface OnItemClickListener {
        void onClick(ImageView imageView, String path);
    }
}