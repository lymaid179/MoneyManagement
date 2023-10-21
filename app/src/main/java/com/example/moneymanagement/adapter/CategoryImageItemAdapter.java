package com.example.moneymanagement.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneymanagement.R;
import com.example.moneymanagement.models.CategoryImage;
import com.example.moneymanagement.support.CategoryImageOnItemSelected;

import java.util.ArrayList;
import java.util.List;

public class CategoryImageItemAdapter extends RecyclerView.Adapter<CategoryImageItemAdapter.ViewHolder> {
    Context context;
    List<CategoryImage> categoryImageList = new ArrayList<>();
    CategoryImageOnItemSelected itemSelectedListener;
    String color;
    int selected = 0;

    public CategoryImageItemAdapter(Context context, List<CategoryImage> categoryImageList, String color, CategoryImageOnItemSelected itemSelectedListener) {
        this.context = context;
        this.categoryImageList = categoryImageList;
        this.color = color;
        this.itemSelectedListener = itemSelectedListener;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_image, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryImage categoryImage = categoryImageList.get(position);

        String imageName = categoryImage.getImageName();
        int resID = context.getResources().getIdentifier(imageName , "drawable" , context.getPackageName());

        holder.imageView.setImageResource(resID);
        if (categoryImage.isSelected()) {
            holder.imageView.setImageTintList(ColorStateList.valueOf(Color.WHITE));
            holder.colorView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));
        } else {
            holder.imageView.setImageTintList(ColorStateList.valueOf(Color.parseColor("#535354")));
            holder.colorView.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        }

        holder.colorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CategoryImage oldSelectedImage = categoryImageList.get(selected);
                oldSelectedImage.setSelected(false);

                categoryImage.setSelected(true);
                selected = holder.getAdapterPosition();

                itemSelectedListener.onItemClick(v, selected);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryImageList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        View colorView;

        public ViewHolder(@NonNull View v) {
            super(v);

            imageView = v.findViewById(R.id.imageView);
            colorView = v.findViewById(R.id.colorView);
        }
    }
}
