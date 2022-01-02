package com.fawaid_elbenaa.adapters.google_adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.fawaid_elbenaa.R;
import com.fawaid_elbenaa.activities_fragments.activity_home.fragments.Fragment_Menu;
import com.fawaid_elbenaa.databinding.CategoryRowBinding;
import com.fawaid_elbenaa.models.google_models.CategoryModel;
import com.fawaid_elbenaa.tags.Tags;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.paperdb.Paper;

public class PlaceCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<CategoryModel> list;
    private Context context;
    private LayoutInflater inflater;
    private String lang;
    private Fragment_Menu fragment_menu;

    public PlaceCategoryAdapter(List<CategoryModel> list, Context context, Fragment_Menu fragment_menu) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        Paper.init(context);
        lang = Paper.book().read("lang","ar");
        this.fragment_menu = fragment_menu;


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        CategoryRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.category_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;
        CategoryModel categoryModel = list.get(position);
        myHolder.binding.setLang(lang);
        myHolder.binding.setModel(categoryModel);
        myHolder.binding.setLang(lang);
        Picasso.get().load(Uri.parse(Tags.IMAGE_URL+categoryModel.getImage())).fit().into(myHolder.binding.image);
        myHolder.itemView.setOnClickListener(v -> {
            CategoryModel categoryModel2 = list.get(holder.getAdapterPosition());
            //fragment_menu.setCategoryData(categoryModel2);

        });



    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        public CategoryRowBinding binding;

        public MyHolder(@NonNull CategoryRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }




}
