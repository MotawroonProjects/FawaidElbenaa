package com.fawaid_elbenaa.activities_fragments.activity_home.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fawaid_elbenaa.R;
import com.fawaid_elbenaa.activities_fragments.activity_filter.FilterActivity;
import com.fawaid_elbenaa.activities_fragments.activity_home.HomeActivity;
import com.fawaid_elbenaa.adapters.Category_Adapter;
import com.fawaid_elbenaa.adapters.Category_Adapter3;
import com.fawaid_elbenaa.databinding.FragmentDepartmentBinding;
import com.fawaid_elbenaa.models.AllCatogryModel;
import com.fawaid_elbenaa.models.SingleCategoryModel;
import com.fawaid_elbenaa.models.UserModel;
import com.fawaid_elbenaa.preferences.Preferences;
import com.fawaid_elbenaa.remote.Api;
import com.fawaid_elbenaa.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Department extends Fragment {

    private HomeActivity activity;
    private FragmentDepartmentBinding binding;
    private Preferences preferences;
    private UserModel userModel;
    private String lang;
    private Category_Adapter3 categoryAdapter;
    private List<SingleCategoryModel> categoryList;


    public static Fragment_Department newInstance() {

        return new Fragment_Department();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_department, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();

    }

    private void initView() {
        categoryList = new ArrayList<>();
        activity = (HomeActivity) getActivity();
        preferences = Preferences.getInstance();
        Paper.init(activity);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);
        binding.recView.setLayoutManager(new LinearLayoutManager(activity));
        binding.recView.setLayoutManager(new GridLayoutManager(activity, 2));
        categoryAdapter = new Category_Adapter3(categoryList, activity, this);
        binding.recView.setAdapter(categoryAdapter);
        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        getDepartments();
        binding.swipeRefresh.setOnRefreshListener(() -> getDepartments());

    }

    public void getDepartments() {

        binding.progBar.setVisibility(View.VISIBLE);

        Api.getService(Tags.base_url).getcategories().enqueue(new Callback<AllCatogryModel>() {
            @Override
            public void onResponse(Call<AllCatogryModel> call, Response<AllCatogryModel> response) {
                binding.progBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {

                    if (response.body().getStatus() == 200) {

                        if (response.body().getData().size() > 0) {
                            binding.tvNoData.setVisibility(View.GONE);
                            categoryList.clear();
                            categoryList.addAll(response.body().getData());
                            categoryAdapter.notifyDataSetChanged();
                        } else {
                            binding.tvNoData.setVisibility(View.VISIBLE);
                        }


                    }


                } else {
                    try {

                        Log.e("errorssssss", response.code() + "_" + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (response.code() == 500) {
                        // Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();


                    } else {
                        // Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();


                    }
                }


            }

            @Override
            public void onFailure(Call<AllCatogryModel> call, Throwable t) {
                binding.progBar.setVisibility(View.GONE);
                try {
                    if (t.getMessage() != null) {
                        Log.e("error", t.getMessage());
                        if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                            //   Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                        } else {
                            // Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("error:", t.getMessage());
                        }
                    }

                } catch (Exception e) {
                }

            }
        });


    }


    public void setItemData(SingleCategoryModel singleCategoryModel) {
        Intent intent = new Intent(activity, FilterActivity.class);
        intent.putExtra("data", singleCategoryModel);
        startActivity(intent);
    }
}