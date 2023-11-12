package com.example.moneymanagement.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.moneymanagement.R;
import com.example.moneymanagement.models.Category;
import com.example.moneymanagement.models.Transaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionChartItemAdapter extends ArrayAdapter {
    Context context;
    List<Category> categoryListByCate;
    Map<Integer, Long> cateAmountList;
    long totalAmount = 0;
    public TransactionChartItemAdapter(@NonNull Context context, @NonNull List<Category> categoryListByCate, Map<Integer, Long> cateAmountList) {
        super(context, R.layout.item_transaction_chart, categoryListByCate);

        this.context = context;
        this.cateAmountList = cateAmountList;
        this.categoryListByCate = categoryListByCate;
    }

    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_transaction_chart, null, true);

        ImageView imageView = v.findViewById(R.id.imageView);
        View colorView = v.findViewById(R.id.colorView);
        TextView cateNameTextView = v.findViewById(R.id.categoryNameTextView);
        TextView percentTextView = v.findViewById(R.id.percentTextView);
        View backgroundView = v.findViewById(R.id.backgroundView);
        View progressView = v.findViewById(R.id.progressView);

        Category category = categoryListByCate.get(position);
        Long cateAmount = cateAmountList.get(category.getId());

        String imageName = category.getCategoryImage().getImageName();
        int resID = context.getResources().getIdentifier(imageName , "drawable" , context.getPackageName());

        imageView.setImageResource(resID);
        colorView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(category.getImageColor())));
        cateNameTextView.setText(category.getName());
        progressView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(category.getImageColor())));

        float percent = (float) cateAmount / (float) totalAmount;
        percentTextView.setText(String.format("%.2f", percent*100)+"%");

        // Wait for the view to be laid out before getting its width
        ViewTreeObserver vto = backgroundView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Remove the listener so it only gets called once
                backgroundView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // Get the width now that the layout is complete
                int width = (int) (percent*(backgroundView.getWidth()));
                ViewGroup.LayoutParams params = progressView.getLayoutParams();
                params.width = width;
                progressView.setLayoutParams(params);
            }
        });

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(category.getName());
            }
        });

        return v;
    }
}
