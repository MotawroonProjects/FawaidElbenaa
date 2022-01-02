package com.fawaid_elbenaa.models;

import java.io.Serializable;
import java.util.List;

public class ItemAddAdsDataModel extends StatusResponse implements Serializable {
    private List<ItemAddAds> date;

    public List<ItemAddAds> getData() {
        return date;
    }
}
