package com.fawaid_elbenaa.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;

import com.fawaid_elbenaa.R;
import com.fawaid_elbenaa.databinding.SpinnerRowBinding;
import com.fawaid_elbenaa.models.GovernorateModel;

import java.util.List;

import io.paperdb.Paper;

public class SpinnerGovernorateAdapter extends BaseAdapter {
    private List<GovernorateModel> list;
    private Context context;
    private LayoutInflater inflater;
    private String lang;

    public SpinnerGovernorateAdapter(List<GovernorateModel> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        Paper.init(context);
        lang = Paper.book().read("lang","ar");
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint("ViewHolder") SpinnerRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.spinner_row,parent,false);
        String title ="";
        if (lang.equals("ar")){
            title = list.get(position).getGovernorate_name();
        }else {
            title = list.get(position).getGovernorate_name_en();

        }
        binding.setTitle(title);
        return binding.getRoot();
    }
}
