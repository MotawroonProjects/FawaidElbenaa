package com.fawaid_elbenaa.models;

import java.io.Serializable;

public class CommentModel implements Serializable {
    private int id;
    private int user_id;
    private int product_id;
    private String country_guide_id;
    private String desc;
    private String is_shown;
    private UserModel.Data user;

    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public String getCountry_guide_id() {
        return country_guide_id;
    }

    public String getDesc() {
        return desc;
    }

    public String getIs_shown() {
        return is_shown;
    }

    public UserModel.Data getUser() {
        return user;
    }
}
