package com.fawaid_elbenaa.activities_fragments.activity_departments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.fawaid_elbenaa.R;
import com.fawaid_elbenaa.activities_fragments.activity_add_ads.AddAdsActivity;
import com.fawaid_elbenaa.activities_fragments.activity_add_guide.AddGuideActivity;
import com.fawaid_elbenaa.adapters.Category2_Adapter;
import com.fawaid_elbenaa.adapters.google_adapters.Place2CategoryAdapter;
import com.fawaid_elbenaa.databinding.ActivityDepartmentDetailsBinding;
import com.fawaid_elbenaa.databinding.ActivityDepartmentsBinding;
import com.fawaid_elbenaa.language.Language;
import com.fawaid_elbenaa.models.AllCatogryModel;
import com.fawaid_elbenaa.models.SingleCategoryModel;
import com.fawaid_elbenaa.models.UserModel;
import com.fawaid_elbenaa.models.google_models.CategoryDataModel;
import com.fawaid_elbenaa.models.google_models.CategoryModel;
import com.fawaid_elbenaa.preferences.Preferences;
import com.fawaid_elbenaa.remote.Api;
import com.fawaid_elbenaa.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DepartmentActivity extends AppCompatActivity {
    private ActivityDepartmentsBinding binding;
    private String lang;
    private List<SingleCategoryModel> categoryList;
    private Category2_Adapter categoryAdapter;
    private Place2CategoryAdapter place2CategoryAdapter;
    private List<CategoryModel> categoryModelList;
    private Preferences preferences;
    private UserModel userModel;
    private int child_pos = 0;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_departments);
        initView();

    }


    private void initView() {
        categoryModelList = new ArrayList<>();
        categoryList = new ArrayList<>();


        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");


        binding.setLang(lang);


        binding.recViewSubDepartment.setLayoutManager(new GridLayoutManager(this, 3));
        categoryAdapter = new Category2_Adapter(categoryList, this);
        binding.recViewSubDepartment.setAdapter(categoryAdapter);

        //binding.recView.setLayoutManager(new LinearLayoutManager(this));
        binding.recView.setLayoutManager(new GridLayoutManager(this, 3));
        place2CategoryAdapter = new Place2CategoryAdapter(categoryModelList, this);

        binding.recView.setAdapter(place2CategoryAdapter);
        binding.llBack.setOnClickListener(view -> onBackPressed());
        getCategoryData();
        getCategory();
    }

    private void getCategory() {

binding.progBarType.setVisibility(View.VISIBLE);
        categoryModelList.clear();
        Api.getService(Tags.base_url)
                .getGoogleCategory()
                .enqueue(new Callback<CategoryDataModel>() {
                    @Override
                    public void onResponse(Call<CategoryDataModel> call, Response<CategoryDataModel> response) {
                        binding.progBarType.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null) {
                            categoryModelList.addAll(response.body().getGoogle_categories());
                            place2CategoryAdapter.notifyDataSetChanged();
                            Log.e("size", categoryModelList.size() + "");
                            if (categoryModelList.size() > 0) {
                                binding.tvNoData.setVisibility(View.GONE);
                            } else {
                                binding.tvNoData.setVisibility(View.VISIBLE);

                            }

                        } else {

                            try {
                                Log.e("error_code", response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                    }


                    @Override
                    public void onFailure(Call<CategoryDataModel> call, Throwable t) {
                        try {
                            binding.progBarType.setVisibility(View.GONE);

                            Log.e("3", "3");

                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    //Toast.makeText(activity,getString(R.string.something), Toast.LENGTH_LONG).show();
                                } else if (t.getMessage().toLowerCase().contains("socket") || t.getMessage().toLowerCase().contains("canceled")) {
                                } else {
                                    //  Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("error", e.toString());
                        }
                    }
                });


    }

    private void getCategoryData() {

        binding.progBar.setVisibility(View.VISIBLE);

        Api.getService(Tags.base_url).getcategories().enqueue(new Callback<AllCatogryModel>() {
            @Override
            public void onResponse(Call<AllCatogryModel> call, Response<AllCatogryModel> response) {
                binding.progBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {

                    if (response.body().getStatus() == 200) {
                        Log.e("dlldldlld", response.code() + "dldlldldld");

                        if (response.body().getData().size() > 0) {
                            binding.progBar.setVisibility(View.GONE);

                            categoryList.clear();
                            categoryList.addAll(response.body().getData());
                            categoryAdapter.notifyDataSetChanged();
                        } else {

                            binding.ll.setVisibility(View.GONE);
                        }


                    } else {
                        binding.ll.setVisibility(View.GONE);
                    }


                } else {
                    try {

                        Log.e("errorssssss", response.code() + "_" + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (response.code() == 500) {
                        //  Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();


                    } else {
                        //  Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();


                    }
                }


            }

            @Override
            public void onFailure(Call<AllCatogryModel> call, Throwable t) {
                binding.progBar.setVisibility(View.GONE);
                try {
                    binding.progBar.setVisibility(View.GONE);
                    if (t.getMessage() != null) {
                        Log.e("error", t.getMessage());
                        if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                            //    Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                        } else {
                            //    Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("error:", t.getMessage());
                        }
                    }

                } catch (Exception e) {
                }

            }
        });


    }


    public void addads(SingleCategoryModel singleCategoryModel) {
        Intent intent = new Intent(this, AddAdsActivity.class);
        intent.putExtra("cat_id", singleCategoryModel.getId()+"");
        startActivity(intent);
    }
    public void addguide(CategoryModel singleCategoryModel) {
        Intent intent = new Intent(this, AddGuideActivity.class);
        intent.putExtra("cat_id", singleCategoryModel.getId()+"");
        startActivity(intent);
    }
}