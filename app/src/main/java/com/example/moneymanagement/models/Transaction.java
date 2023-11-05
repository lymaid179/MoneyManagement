package com.example.moneymanagement.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class Transaction implements Parcelable {
    private String note;
    private long amount;
    private Timestamp date;
    private Category category;
    private int id;

    public Transaction() {
    }

    public Transaction(int id, long amount, String note, Timestamp date, Category category) {
        this.id = id;
        this.note = note;
        this.amount = amount;
        this.date = date;
        this.category = category;
    }


    protected Transaction(Parcel in) {
        note = in.readString();
        amount = in.readLong();
        date = in.readParcelable(Timestamp.class.getClassLoader());
        id = in.readInt();
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    //Bug fix Serialize
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(note);
        dest.writeLong(amount);
        dest.writeParcelable(date, flags);
        dest.writeInt(id);
    }

}
