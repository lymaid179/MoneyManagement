package com.example.moneymanagement.models;

import java.io.Serializable;

public class Category implements Serializable {
    private int id;
    private String name;
    private int kind; //0: expense, 1: income
//    private int imageId;
    private String imageColor;
    private CategoryImage categoryImage;

    public Category() {
    }

    public Category(int id, String name, int kind, String imageColor, CategoryImage categoryImage) {
        this.id = id;
        this.name = name;
        this.kind = kind;
        this.imageColor = imageColor;
        this.categoryImage = categoryImage;
    }

    public Category(String name, int kind, String imageColor, CategoryImage categoryImage) {
        this.name = name;
        this.kind = kind;
        this.imageColor = imageColor;
        this.categoryImage = categoryImage;
    }

    public CategoryImage getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(CategoryImage categoryImage) {
        this.categoryImage = categoryImage;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public String getImageColor() {
        return imageColor;
    }

    public void setImageColor(String imageColor) {
        this.imageColor = imageColor;
    }
}
