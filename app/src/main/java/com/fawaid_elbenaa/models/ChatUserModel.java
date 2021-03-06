package com.fawaid_elbenaa.models;

import java.io.Serializable;

public class ChatUserModel implements Serializable {
    private int id;
    private String name;
    private String image;
    private int room_id;
    private String  ad_id;

    public ChatUserModel() {
    }

    public ChatUserModel(int id, String name, String image, int room_id, String  ad_id) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.room_id = room_id;
        this.ad_id = ad_id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public String  getAd_id() {
        return ad_id;
    }
}
