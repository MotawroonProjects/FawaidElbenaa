package com.fawaid_elbenaa.models;

import java.io.Serializable;

public class AdminRoomDataModel extends StatusResponse implements Serializable {
    private AdminRoomModel data;

    public AdminRoomModel getData() {
        return data;
    }

}
