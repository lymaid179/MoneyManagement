package com.example.moneymanagement.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.moneymanagement.R;
import com.example.moneymanagement.models.Transaction;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TransactionDateItemAdapter extends ArrayAdapter {
    Context context;
    List<Transaction> transactionList;
    List<Transaction> transactionListByDate = new ArrayList<>();
    List<Timestamp> timestampList;
    TransactionItemAdapter adapter;
    int currentIndex = 0;
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
    public TransactionDateItemAdapter(@NonNull Context context, @NonNull List<Timestamp> timestampList, List<Transaction> transactionList) {
        super(context, R.layout.item_transaction_date, timestampList);

        this.context = context;
        this.transactionList = transactionList;
        this.timestampList = timestampList;
    }

    @NonNull
    @Override
    @SuppressLint("MissingInflatedId")
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_transaction_date, null, true);

        TextView dateTextView = v.findViewById(R.id.textViewDate);
        ListView listView = v.findViewById(R.id.listView);

        Timestamp timestamp = timestampList.get(position);
        dateTextView.setText(""+dateFormat.format(timestamp.toDate()));

        adapter = new TransactionItemAdapter(context, transactionListByDate);
        listView.setAdapter(adapter);

        getTransactionListByDate(timestamp);

        //Set list View
        int totalHeight = 0;
        for (int i = 0; i < transactionListByDate.size(); i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();

        return v;
    }

    private void getTransactionListByDate(Timestamp timestamp) {
        transactionListByDate.clear();
        System.out.println("----");
        for (int i = 0; i<transactionList.size(); i++){
            Transaction transaction = transactionList.get(i);
            System.out.println(transaction.getCategory().getName());
            Timestamp transactionDate = transaction.getDate();
            if (dateFormat.format(timestamp.toDate()).equals(dateFormat.format(transactionDate.toDate()))) {
                transactionListByDate.add(transaction);
//                currentIndex++;
            } else {
//                adapter.notifyDataSetChanged();
//                return;
            }
        }
        adapter.notifyDataSetChanged();

        return;
    }
}
