package com.example.moneymanagement.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.example.moneymanagement.R;
import com.example.moneymanagement.models.Category;
import com.example.moneymanagement.models.CategoryImage;
import com.example.moneymanagement.support.CategoryOnItemSelected;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CategoryItemAdapter extends ArrayAdapter {
    Context context;
    List<Category> categoryList = new ArrayList<>();
    CategoryOnItemSelected categoryItemListener;

    public CategoryItemAdapter(@NonNull Context context, @NonNull List<Category> categoryList, CategoryOnItemSelected categoryItemListener) {
        super(context, R.layout.item_category, categoryList);

        this.context = context;
        this.categoryList = categoryList;
        this.categoryItemListener = categoryItemListener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_category, null, true);

        View colorView = v.findViewById(R.id.colorView);
        ImageView imageView = v.findViewById(R.id.imageView);
        TextView categoryNameText = v.findViewById(R.id.categoryNameTextView);

        Category category = categoryList.get(position);

        String imageName = category.getCategoryImage().getImageName();
        int resID = context.getResources().getIdentifier(imageName , "drawable" , context.getPackageName());

        imageView.setImageResource(resID);
        colorView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(category.getImageColor())));
        categoryNameText.setText(category.getName());

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryItemListener.onItemClick(v, position);
            }
        });

        return v;

    }
}
