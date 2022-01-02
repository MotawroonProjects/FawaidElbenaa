package com.fawaid_elbenaa.models;

import java.io.Serializable;
import java.util.List;

public class PlaceDataModel extends StatusResponse implements Serializable {
    private List<PlaceModel> data;

    public List<PlaceModel> getData() {
        return data;
    }
}
