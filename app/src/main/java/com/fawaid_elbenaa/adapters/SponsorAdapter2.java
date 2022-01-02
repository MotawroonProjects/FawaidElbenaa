package com.fawaid_elbenaa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.fawaid_elbenaa.R;
import com.fawaid_elbenaa.databinding.SponsorRow2Binding;
import com.fawaid_elbenaa.models.SponsorsModel;

import java.util.List;

public class SponsorAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SponsorsModel.Sponsors> list;
    private Context context;
    private LayoutInflater inflater;
    private Fragment fragment;

    public SponsorAdapter2(List<SponsorsModel.Sponsors> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        SponsorRow2Binding binding = DataBindingUtil.inflate(inflater, R.layout.sponsor_row2, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;

        myHolder.binding.setModel(list.get(position));



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public SponsorRow2Binding binding;

        public MyHolder(@NonNull SponsorRow2Binding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}
