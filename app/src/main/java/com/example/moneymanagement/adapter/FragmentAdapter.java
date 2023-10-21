package com.example.moneymanagement.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.moneymanagement.fragment.FragmentCategory;
import com.example.moneymanagement.fragment.FragmentHome;

public class FragmentAdapter extends FragmentPagerAdapter {
    int pageNumber;
    public FragmentAdapter(@NonNull FragmentManager fm, int pageNumber) {
        super(fm, pageNumber);
        this.pageNumber = pageNumber;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FragmentHome();
            case 1:
                return new FragmentCategory();
        }
        return null;
    }

    @Override
    public int getCount() {
        return pageNumber;
    }
}
