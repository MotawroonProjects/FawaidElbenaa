package com.fawaid_elbenaa.models;

import java.io.Serializable;

public class InvoiceModel implements Serializable {
    private boolean IsSuccess;
    private Data Data;

    public boolean isSuccess() {
        return IsSuccess;
    }

    public Data getData() {
        return Data;
    }

    public class Data implements Serializable{
        private String InvoiceURL;

        public String getInvoiceURL() {
            return InvoiceURL;
        }
    }

}
