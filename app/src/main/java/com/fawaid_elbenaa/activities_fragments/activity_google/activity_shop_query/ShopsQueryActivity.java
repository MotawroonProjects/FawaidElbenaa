package com.fawaid_elbenaa.activities_fragments.activity_google.activity_shop_query;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.fawaid_elbenaa.R;
import com.fawaid_elbenaa.activities_fragments.activity_guide_detials.GuideDetailsActivity;
import com.fawaid_elbenaa.adapters.PlaceAdapter;
import com.fawaid_elbenaa.databinding.ActivityShopsQueryBinding;
import com.fawaid_elbenaa.language.Language;
import com.fawaid_elbenaa.models.PlaceDataModel;
import com.fawaid_elbenaa.models.PlaceModel;
import com.fawaid_elbenaa.models.UserModel;
import com.fawaid_elbenaa.models.google_models.CategoryModel;
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

public class ShopsQueryActivity extends AppCompatActivity {
    private ActivityShopsQueryBinding binding;
    private double user_lat;
    private double user_lng;
    private SkeletonScreen skeletonScreen;
    private String lang;
    private CategoryModel categoryModel;
    private Preferences preferences;
    private UserModel userModel;
    private List<PlaceModel> list;
    private PlaceAdapter adapter;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_shops_query);
        getDataFromIntent();
        initView();
    }

    private void initView() {
        list = new ArrayList<>();

        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        if (lang.equals("ar")) {
            binding.setQuery(categoryModel.getAr_title());

        } else {
            binding.setQuery(categoryModel.getEn_title());

        }
        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PlaceAdapter(list,this);
        binding.recView.setAdapter(adapter);

        skeletonScreen = Skeleton.bind(binding.recView)
                .adapter(adapter)
                .count(5)
                .frozen(false)
                .shimmer(true)
                .show();




        binding.close.setOnClickListener(v -> super.onBackPressed());
        getShops();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        user_lat = intent.getDoubleExtra("lat", 0.0);
        user_lng = intent.getDoubleExtra("lng", 0.0);
        categoryModel = (CategoryModel) intent.getSerializableExtra("data");

    }


    private void getShops() {
        list.clear();
        adapter.notifyDataSetChanged();
        binding.tvNoData.setVisibility(View.GONE);
        skeletonScreen.show();


        Api.getService(Tags.base_url)
                .getPlaceByCategory("Bearer "+userModel.getData().getToken(), categoryModel.getId())
                .enqueue(new Callback<PlaceDataModel>() {
                    @Override
                    public void onResponse(Call<PlaceDataModel> call, Response<PlaceDataModel> response) {
                        if (response.isSuccessful() && response.body() != null) {

                            if (response.body().getData().size()>0){
                                updateUi(response.body().getData());
                                binding.tvNoData.setVisibility(View.GONE);
                            }else {
                                skeletonScreen.hide();
                                binding.tvNoData.setVisibility(View.VISIBLE);

                            }

                        } else {
                            skeletonScreen.hide();

                            try {
                                Log.e("error_code", response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                    }


                    @Override
                    public void onFailure(Call<PlaceDataModel> call, Throwable t) {
                        try {
                            Log.e("3", "3");

                            skeletonScreen.hide();
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                  //  Toast.makeText(ShopsQueryActivity.this,getString(R.string.something), Toast.LENGTH_LONG).show();
                                } else if (t.getMessage().toLowerCase().contains("socket") || t.getMessage().toLowerCase().contains("canceled")) {
                                } else {
                                    //Toast.makeText(ShopsQueryActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("error", e.toString());
                        }
                    }
                });

    }

    private void updateUi(List<PlaceModel> data) {
        for (PlaceModel placeModel :data){
            double distance = getDistance(new LatLng(user_lat,user_lng), new LatLng(placeModel.getLatitude(),placeModel.getLongitude()));

            placeModel.setDistance(Double.parseDouble(String.format(Locale.ENGLISH,"%.2f",distance)));
            list.add(placeModel);
        }
        skeletonScreen.hide();
        adapter.notifyDataSetChanged();
    }


    private double getDistance(LatLng latLng1, LatLng latLng2) {
        return SphericalUtil.computeDistanceBetween(latLng1, latLng2) / 1000;
    }

    public void setShopData(PlaceModel placeModel) {
        Intent intent = new Intent(this, GuideDetailsActivity.class);
        intent.putExtra("data", placeModel);
        startActivity(intent);

    }


}