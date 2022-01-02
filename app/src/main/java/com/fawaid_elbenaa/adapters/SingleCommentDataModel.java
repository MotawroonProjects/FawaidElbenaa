package com.fawaid_elbenaa.adapters;

import com.fawaid_elbenaa.models.CommentModel;
import com.fawaid_elbenaa.models.StatusResponse;

import java.io.Serializable;

public class SingleCommentDataModel extends StatusResponse implements Serializable {

    private CommentModel data;

    public CommentModel getData() {
        return data;
    }

}
