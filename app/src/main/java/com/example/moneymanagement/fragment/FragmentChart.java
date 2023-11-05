package com.example.moneymanagement.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.moneymanagement.R;
import com.github.mikephil.charting.charts.PieChart;

public class FragmentChart extends Fragment {
    ListView listView;
    PieChart pieChart;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chart, container, false);
        System.out.println("--Chart--");
        listView = v.findViewById(R.id.listView);
        pieChart = v.findViewById(R.id.pieChart);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        System.out.println(listView.getWidth());
        System.out.println(pieChart.getWidth());
    }
}
