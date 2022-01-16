package com.fawaid_elbenaa.models;

import java.io.Serializable;
import java.util.List;

public class PackageDataModel extends StatusResponse implements Serializable {
    private List<PackageModel> data;

    public List<PackageModel> getData() {
        return data;
    }
}
