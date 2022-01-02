package com.fawaid_elbenaa.models;

import java.io.Serializable;
import java.util.List;

public class PlaceModel implements Serializable {

    private int id;
    private String title;
    private String desc;
    private String main_image;
    private int user_id;
    private int governorate_id;
    private int google_category_id;
    private String whatsapp_number;
    private String address;
    private double latitude;
    private double longitude;
    private String is_shown;
    private int view_counts;
    private GoogleCategory google_category;
    private UserModel.Data user;
    private List<Image> images;
    private Governorate governorate;
    private double distance;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getMain_image() {
        return main_image;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getGovernorate_id() {
        return governorate_id;
    }

    public int getGoogle_category_id() {
        return google_category_id;
    }

    public String getWhatsapp_number() {
        return whatsapp_number;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getIs_shown() {
        return is_shown;
    }

    public int getView_counts() {
        return view_counts;
    }

    public GoogleCategory getGoogle_category() {
        return google_category;
    }

    public UserModel.Data getUser() {
        return user;
    }

    public List<Image> getImages() {
        return images;
    }

    public Governorate getGovernorate() {
        return governorate;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public static class GoogleCategory implements Serializable{
        private int id;
        private String ar_title;
        private String en_title;
        private String keyword_google;
        private Object image;
        private String type;
        private String is_shown;

        public int getId() {
            return id;
        }

        public String getAr_title() {
            return ar_title;
        }

        public String getEn_title() {
            return en_title;
        }

        public String getKeyword_google() {
            return keyword_google;
        }

        public Object getImage() {
            return image;
        }

        public String getType() {
            return type;
        }

        public String getIs_shown() {
            return is_shown;
        }
    }
    public static class Image implements Serializable{
        private int id;
        private String image;
        private int country_guide_id;

        public int getId() {
            return id;
        }

        public String getImage() {
            return image;
        }

        public int getCountry_guide_id() {
            return country_guide_id;
        }
    }
    public static class Governorate implements Serializable{
        private int id;
        private String governorate_name;
        private String governorate_name_en;

        public int getId() {
            return id;
        }

        public String getGovernorate_name() {
            return governorate_name;
        }

        public String getGovernorate_name_en() {
            return governorate_name_en;
        }
    }

}
