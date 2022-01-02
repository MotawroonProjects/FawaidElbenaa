package com.fawaid_elbenaa.models;

import java.io.Serializable;
import java.util.List;

public class SponsorsModel implements Serializable {
    private List<Sponsors> date;

    public List<Sponsors> getSponsors() {
        return date;
    }

    public class Sponsors {
        private int id;
        private String image;

        public int getId() {
            return id;
        }

        public String getLogo() {
            return image;
        }
    }
}
