package com.example.moneymanagement.models;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class Transaction implements Serializable {
    private String note;
    private long amount;
    private Timestamp date;
    private Category category;

    public Transaction() {
    }

    public Transaction(long amount, String note, Timestamp date, Category category) {
        this.note = note;
        this.amount = amount;
        this.date = date;
        this.category = category;
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
}
