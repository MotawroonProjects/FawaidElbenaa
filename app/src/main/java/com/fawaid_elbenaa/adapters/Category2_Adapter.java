package com.fawaid_elbenaa.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.fawaid_elbenaa.R;
import com.fawaid_elbenaa.activities_fragments.activity_departments.DepartmentActivity;
import com.fawaid_elbenaa.databinding.Catogry2RowBinding;
import com.fawaid_elbenaa.databinding.CatogryRowBinding;
import com.fawaid_elbenaa.models.SingleCategoryModel;
import com.fawaid_elbenaa.tags.Tags;

import java.util.List;

import io.paperdb.Paper;

public class Category2_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SingleCategoryModel> singleCategoryModelList;
    private Context context;
    private LayoutInflater inflater;
    private String lang;


    public Category2_Adapter(List<SingleCategoryModel> singleCategoryModelList, Context context) {
        this.singleCategoryModelList = singleCategoryModelList;
        this.context = context;
        inflater = LayoutInflater.from(context);
        Paper.init(context);
        lang = Paper.book().read("lang", java.util.Locale.getDefault().getLanguage());
    }

    @androidx.annotation.NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {


        Catogry2RowBinding binding = DataBindingUtil.inflate(inflater, R.layout.catogry2_row, parent, false);
        return new EventHolder(binding);


    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull RecyclerView.ViewHolder holder, int position) {

        EventHolder eventHolder = (EventHolder) holder;
        eventHolder.binding.setCategorymodel(singleCategoryModelList.get(position));
        Log.e("dldkldk", Tags.IMAGE_URL+singleCategoryModelList.get(position).getImage());
        eventHolder.itemView.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                if (context instanceof DepartmentActivity) {
                    DepartmentActivity fragment_home = (DepartmentActivity) context;
                    fragment_home.addads(singleCategoryModelList.get(eventHolder.getLayoutPosition()));
                }
            }
        });
/*
if(i==position){
    if(i!=0) {
        if (((EventHolder) holder).binding.expandLayout.isExpanded()) {
            ((EventHolder) holder).binding.tvTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            ((EventHolder) holder).binding.recView.setLayoutParams(new FrameLayout.LayoutParams(0, 0));
            ((EventHolder) holder).binding.expandLayout.collapse(true);
            ((EventHolder) holder).binding.expandLayout.setVisibility(View.GONE);



        }
        else {

          //  ((EventHolder) holder).binding.tvTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            ((EventHolder) holder).binding.recView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
            ((EventHolder) holder).binding.expandLayout.setVisibility(View.VISIBLE);

           ((EventHolder) holder).binding.expandLayout.expand(true);
        }
    }
    else {
        eventHolder.binding.tvTitle.setBackground(activity.getResources().getDrawable(R.drawable.linear_bg_green));

        ((EventHolder) holder).binding.tvTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        ((EventHolder) holder).binding.recView.setLayoutParams(new FrameLayout.LayoutParams(0, 0));

    }
}
if(i!=position) {
    eventHolder.binding.tvTitle.setBackground(activity.getResources().getDrawable(R.drawable.linear_bg_white));
    ((EventHolder) holder).binding.tvTitle.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

    ((EventHolder) holder).binding.recView.setLayoutParams(new FrameLayout.LayoutParams(0, 0));
    ((EventHolder) holder).binding.expandLayout.collapse(true);


}*/

    }

    @Override
    public int getItemCount() {
        return singleCategoryModelList.size();
    }

    public class EventHolder extends RecyclerView.ViewHolder {
        public Catogry2RowBinding binding;

        public EventHolder(@androidx.annotation.NonNull Catogry2RowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}
