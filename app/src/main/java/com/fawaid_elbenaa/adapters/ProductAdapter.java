package com.fawaid_elbenaa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.fawaid_elbenaa.R;
import com.fawaid_elbenaa.activities_fragments.activity_department_details.DepartmentDetailsActivity;
import com.fawaid_elbenaa.activities_fragments.activity_filter.FilterActivity;
import com.fawaid_elbenaa.activities_fragments.activity_home.fragments.Fragment_Home;
import com.fawaid_elbenaa.databinding.ProductRowBinding;
import com.fawaid_elbenaa.models.ProductModel;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ProductModel> list;
    private Context context;
    private LayoutInflater inflater;
    private Fragment fragment;
    private AppCompatActivity activity;

    public ProductAdapter(List<ProductModel> list, Context context, Fragment fragment) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.fragment = fragment;
        activity = (AppCompatActivity) context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ProductRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.product_row, parent, false);
        return new MyHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyHolder) {
            MyHolder myHolder = (MyHolder) holder;
            myHolder.binding.setModel(list.get(position));

            myHolder.itemView.setOnClickListener(view -> {
                if (fragment!=null){
                    if (fragment instanceof Fragment_Home) {

                        Fragment_Home fragment_main = (Fragment_Home) fragment;
                        fragment_main.setProductItemData(list.get(myHolder.getAdapterPosition()));
                    }
                }else {
                    if (activity instanceof DepartmentDetailsActivity){
                        DepartmentDetailsActivity departmentDetailsActivity = (DepartmentDetailsActivity) activity;
                        departmentDetailsActivity.setProductItemData(list.get(myHolder.getAdapterPosition()));

                    }
                    else if(activity instanceof FilterActivity){
                        FilterActivity filterActivity = (FilterActivity) activity;
                        filterActivity.setProductItemData(list.get(myHolder.getAdapterPosition()));
                    }
                }

            });
myHolder.binding.checkbox.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if (fragment!=null){
            if (fragment instanceof Fragment_Home) {

                Fragment_Home fragment_main = (Fragment_Home) fragment;
                fragment_main.likeDislike(list.get(myHolder.getAdapterPosition()),myHolder.getAdapterPosition());
            }
        }else {
          if(activity instanceof FilterActivity){
                FilterActivity filterActivity = (FilterActivity) activity;
                filterActivity.likeDislike(list.get(myHolder.getAdapterPosition()),myHolder.getAdapterPosition());
            }
        }
    }
});

        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public ProductRowBinding binding;

        public MyHolder(@NonNull ProductRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}
