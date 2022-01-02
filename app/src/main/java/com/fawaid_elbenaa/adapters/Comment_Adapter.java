package com.fawaid_elbenaa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.fawaid_elbenaa.R;
import com.fawaid_elbenaa.databinding.CatogryRowBinding;
import com.fawaid_elbenaa.databinding.CommentRowBinding;
import com.fawaid_elbenaa.models.CommentModel;

import java.util.List;

import io.paperdb.Paper;

public class Comment_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<CommentModel> list;
    private Context context;
    private LayoutInflater inflater;
    private String lang;


    public Comment_Adapter(List<CommentModel> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        Paper.init(context);
        lang = Paper.book().read("lang", java.util.Locale.getDefault().getLanguage());
    }

    public Comment_Adapter(Context context) {
        inflater = LayoutInflater.from(context);

    }

    @androidx.annotation.NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {


        CommentRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.comment_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull RecyclerView.ViewHolder holder, int position) {
        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setModel(list.get(position));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        public CommentRowBinding binding;

        public MyHolder(@androidx.annotation.NonNull CommentRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}
