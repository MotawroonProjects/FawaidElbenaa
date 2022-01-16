package com.fawaid_elbenaa.models;

import java.io.Serializable;

public class PackageModel implements Serializable {
    private int id;
    private String title;
    private int price;
    private int count_of_dates;
    private int count_of_posts;
    private int is_default;
    private int is_active;
    private boolean isSelected = false;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getPrice() {
        return price;
    }

    public int getCount_of_dates() {
        return count_of_dates;
    }

    public int getCount_of_posts() {
        return count_of_posts;
    }

    public int getIs_default() {
        return is_default;
    }

    public int getIs_active() {
        return is_active;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
