package com.example.moneymanagement.models;

import java.io.Serializable;

public class CategoryImage implements Serializable {
    private int id;
    private String imageName;

    private boolean isSelected = false;

    public CategoryImage() {
    }

    public CategoryImage(int id, String imageName) {
        this.id = id;
        this.imageName = imageName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageName() {
        return imageName;
    }
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
