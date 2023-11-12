package com.example.moneymanagement.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.moneymanagement.R;
import com.example.moneymanagement.TransactionFormActivity;
import com.example.moneymanagement.adapter.TransactionChartItemAdapter;
import com.example.moneymanagement.models.Category;
import com.example.moneymanagement.models.Transaction;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FragmentChart extends Fragment {
    ListView listView;
    PieChart pieChart;
    TabLayout tabLayout;
    TextView fromDateTextView, toDateTextView;
    View fromDateView, toDateView;
    Button searchButton;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    List<Transaction> transactionList = new ArrayList<>();
    List<Category> adapterCategoryList = new ArrayList<>();
    Map<Integer, Long> cateAmount = new HashMap<>();
    long totalAmount = 0;
    TransactionChartItemAdapter adapter;
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chart, container, false);

        listView = v.findViewById(R.id.listView);
        pieChart = v.findViewById(R.id.pieChart);
        tabLayout = v.findViewById(R.id.tabLayout);
        fromDateTextView = v.findViewById(R.id.fromDateTextView);
        toDateTextView = v.findViewById(R.id.toDateTextView);
        fromDateView = v.findViewById(R.id.fromDateView);
        toDateView = v.findViewById(R.id.toDateView);
        searchButton = v.findViewById(R.id.searchButton);

        //from Date
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        final Date[] fromDate = {fromCalendar.getTime()};
        fromDateTextView.setText(dateFormat.format(fromDate[0]));
        fromDateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                c.setTime(fromDate[0]);
                int cy = c.get(Calendar.YEAR);
                int cm = c.get(Calendar.MONTH);
                int cd = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                                Calendar c = Calendar.getInstance();
                                c.set(y, m, d, 0, 0, 0);
                                fromDate[0] = c.getTime();

                                fromDateTextView.setText(""+dateFormat.format(fromDate[0]));
                            }
                        }, cy, cm, cd);
                dialog.show();
            }
        });

        //to Date
        Calendar toCalendar = Calendar.getInstance();
        toCalendar.set(Calendar.HOUR_OF_DAY, 23);
        toCalendar.set(Calendar.MINUTE, 59);
        toCalendar.set(Calendar.SECOND, 59);
        final Date[] toDate = {toCalendar.getTime()};
        toDateTextView.setText(dateFormat.format(toDate[0]));
        toDateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                c.setTime(toDate[0]);
                int cy = c.get(Calendar.YEAR);
                int cm = c.get(Calendar.MONTH);
                int cd = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int y, int m, int d) {
                                Calendar c = Calendar.getInstance();
                                c.set(y, m, d, 23,59,59);
                                toDate[0] = c.getTime();

                                toDateTextView.setText(""+dateFormat.format(toDate[0]));
                            }
                        }, cy, cm, cd);
                dialog.show();
            }
        });

        //ListView
        adapter = new TransactionChartItemAdapter(getContext(), adapterCategoryList, cateAmount);
        listView.setAdapter(adapter);

        //TabLayout
        tabLayout.addTab(tabLayout.newTab().setText("Expense"));
        tabLayout.addTab(tabLayout.newTab().setText("Income"));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                getTransactionByKind(tabLayout.getSelectedTabPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //SearchButton
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTransaction(new Timestamp(fromDate[0]), new Timestamp(toDate[0]));
            }
        });

        getTransaction(new Timestamp(fromDate[0]), new Timestamp(toDate[0]));

        return v;
    }

    private void getTransaction(Timestamp fromDate, Timestamp toDate){
        System.out.println(fromDate.toDate());
        System.out.println(toDate.toDate());
        Query transactionListRef = db.collection("user")
                .document(""+user.getEmail())
                .collection("transaction")
                .whereGreaterThanOrEqualTo("date", fromDate)
                .whereLessThanOrEqualTo("date", toDate)
                .orderBy("date");
        transactionListRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    transactionList.clear();

                    for (DocumentSnapshot document : snapshots.getDocuments()) {
                        Transaction transaction = document.toObject(Transaction.class);
                        transactionList.add(transaction);
                    }

                    getTransactionByKind(tabLayout.getSelectedTabPosition());

                }
            }
        });
    }

    private void getTransactionByKind(int kind) {
        cateAmount.clear();
        totalAmount = 0;
        adapterCategoryList.clear();

        for (Transaction transaction : transactionList) {
            Category transactionCategory = transaction.getCategory();

            if (transaction.getCategory().getKind() == kind) {
                totalAmount += transaction.getAmount();

                if (cateAmount.containsKey(transactionCategory.getId())) {
                    long currentAmount = cateAmount.get(transactionCategory.getId());
                    cateAmount.put(transactionCategory.getId(), currentAmount + transaction.getAmount());
                } else {
                    adapterCategoryList.add(transactionCategory);
                    cateAmount.put(transactionCategory.getId(), transaction.getAmount());
                }
            }
        }

        adapter.setTotalAmount(totalAmount);
        adapter.notifyDataSetChanged();
        configPieChart();

    }

    private void configPieChart(){
        List<PieEntry> entries = new ArrayList<PieEntry>();
        List<Integer> colors = new ArrayList<>();
        for (int key : cateAmount.keySet()) {
            Category category = getCateById(key);
            entries.add(new PieEntry(cateAmount.get(key), category.getName()));
            colors.add(Color.parseColor(category.getImageColor()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Category");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(12f);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getPieLabel(float value, PieEntry pieEntry) {
                return String.format("%s: %.2f", pieEntry.getLabel(), value*100/(float)totalAmount) + "%";
            }
        });

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.setDrawEntryLabels(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setHighlightPerTapEnabled(false);
        pieChart.setRotationEnabled(false);
        pieChart.setCenterText("Total Amount:\n" +totalAmount );
        pieChart.setCenterTextSize(12f);
        pieChart.invalidate();
    }

    private Category getCateById(int id) {
        return adapterCategoryList.stream().filter(new Predicate<Category>() {
            @Override
            public boolean test(Category category) {
                return category.getId() == id;
            }
        }).collect(Collectors.toList()).get(0);
    }
}
