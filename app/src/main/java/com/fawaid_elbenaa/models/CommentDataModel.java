package com.fawaid_elbenaa.models;

import java.io.Serializable;
import java.util.List;

public class CommentDataModel extends StatusResponse implements Serializable {
    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data implements Serializable{
        private int count_of_all_comments;
        private List<CommentModel> comments;

        public int getCount_of_all_comments() {
            return count_of_all_comments;
        }

        public List<CommentModel> getComments() {
            return comments;
        }
    }

}
