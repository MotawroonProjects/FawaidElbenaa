package com.fawaid_elbenaa.models;

import java.io.Serializable;
import java.util.List;

public class NewsModel implements Serializable {
    private List<News> data;

    public List<News> getData() {
        return data;
    }

    public class News implements Serializable {
        private int id;
        private String title;
        private String desc;
        private String image;
        private String date;
        private String time;
        private String created_at;
        private String updated_at;

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getDesc() {
            return desc;
        }

        public String getImage() {
            return image;
        }

        public String getDate() {
            return date;
        }

        public String getTime() {
            return time;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }
    }
}
