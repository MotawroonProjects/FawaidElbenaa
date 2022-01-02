package com.fawaid_elbenaa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.fawaid_elbenaa.R;
import com.fawaid_elbenaa.activities_fragments.activity_filter.FilterActivity;
import com.fawaid_elbenaa.databinding.TypeFilterRowBinding;
import com.fawaid_elbenaa.models.TypeModel;

import java.util.List;

public class TypeFilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<TypeModel> list;
    private Context context;
    private LayoutInflater inflater;
    private FilterActivity activity;
private int i=-1;
    public TypeFilterAdapter(List<TypeModel> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        activity = (FilterActivity) context;


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        TypeFilterRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.type_filter_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;
        myHolder.binding.setModel(list.get(position));

myHolder.itemView.setOnClickListener(new
                                             View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View view) {
                                                     i=position;
                                                     activity.settype(list.get(i).getId());

                                                     notifyDataSetChanged();
                                                 }
                                             });
if(i==position){
    myHolder.binding.imageDelete.setChecked(true);
}
else {
    myHolder.binding.imageDelete.setChecked(false);

}


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        public TypeFilterRowBinding binding;

        public MyHolder(@NonNull TypeFilterRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }




}
