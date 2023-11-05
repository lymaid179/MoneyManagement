package com.example.moneymanagement.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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

import com.example.moneymanagement.R;
import com.example.moneymanagement.TransactionFormActivity;
import com.example.moneymanagement.models.Transaction;
import com.example.moneymanagement.support.TransactionOnItemSelected;

import java.util.List;

public class TransactionItemAdapter extends ArrayAdapter {
    Context context;
    List<Transaction> transactionListByDate;
    TransactionOnItemSelected transactionItemListener;
    public TransactionItemAdapter(@NonNull Context context, @NonNull List<Transaction> transactionListByDate) {
        super(context, R.layout.item_transaction_date, transactionListByDate);

        this.context = context;
        this.transactionListByDate = transactionListByDate;
    }

    public void setTransactionItemListener(TransactionOnItemSelected transactionItemListener) {
        this.transactionItemListener = transactionItemListener;
    }

    @NonNull
    @Override
    @SuppressLint("MissingInflatedId")
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_transaction, null, true);

        ImageView imageView = v.findViewById(R.id.imageView);
        View colorView = v.findViewById(R.id.colorView);
        TextView cateNameTextView = v.findViewById(R.id.categoryNameTextView);
        TextView noteTextView = v.findViewById(R.id.noteTextView);
        TextView amountTextView = v.findViewById(R.id.amountTextView);

        Transaction transaction = transactionListByDate.get(position);

        String imageName = transaction.getCategory().getCategoryImage().getImageName();
        int resID = context.getResources().getIdentifier(imageName , "drawable" , context.getPackageName());

        imageView.setImageResource(resID);
        colorView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(transaction.getCategory().getImageColor())));
        cateNameTextView.setText(transaction.getCategory().getName());

        if (transaction.getNote().isEmpty()) {
            noteTextView.setVisibility(View.GONE);
        } else {
            noteTextView.setVisibility(View.VISIBLE);
            noteTextView.setText(transaction.getNote());
        }

        if (transaction.getCategory().getKind() == 0) {
            amountTextView.setText("- "+transaction.getAmount());
            amountTextView.setTextColor(context.getResources().getColor(R.color.red));
        } else {
            amountTextView.setText("+ "+transaction.getAmount());
            amountTextView.setTextColor(context.getResources().getColor(R.color.green));
        }
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(transaction.getCategory().getName());
                Intent intent = new Intent(getContext(), TransactionFormActivity.class);

                intent.putExtra("transaction", transaction);
                intent.putExtra("category", transaction.getCategory());
                getContext().startActivity(intent);
            }
        });
        return v;
    }
}
