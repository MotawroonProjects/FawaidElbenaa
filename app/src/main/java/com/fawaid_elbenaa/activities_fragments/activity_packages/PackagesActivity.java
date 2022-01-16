package com.fawaid_elbenaa.activities_fragments.activity_packages;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;


import com.fawaid_elbenaa.R;
import com.fawaid_elbenaa.adapters.PackageAdapter;
import com.fawaid_elbenaa.databinding.ActivityPackagesBinding;
import com.fawaid_elbenaa.databinding.ToolbarBinding;
import com.fawaid_elbenaa.language.Language;
import com.fawaid_elbenaa.models.PackageDataModel;
import com.fawaid_elbenaa.models.PackageModel;
import com.fawaid_elbenaa.models.UserModel;
import com.fawaid_elbenaa.preferences.Preferences;
import com.fawaid_elbenaa.remote.Api;
import com.fawaid_elbenaa.tags.Tags;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PackagesActivity extends AppCompatActivity {
    private ActivityPackagesBinding binding;
    private List<PackageModel> list;
    private PackageAdapter adapter;
    private PackageModel selectedPackage;
    private UserModel userModel;
    private Preferences preferences;
    private String lang = "ar";
    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_packages);

        initView();

    }

    private void initView() {
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        lang = Paper.book().read("lang", "ar");
        setUpToolbar(binding.toolbar, getString(R.string.packages_and_subscriptions), R.color.white, R.color.black);
        binding.setDays(getDays());
        list = new ArrayList<>();
        adapter = new PackageAdapter(list, this);
        binding.recView.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recView.setAdapter(adapter);

        getPackages();

    }
    protected void setUpToolbar(ToolbarBinding binding, String title, int background, int arrowTitleColor) {
        binding.setLang(lang);
        binding.setTitle(title);
        binding.arrow.setColorFilter(ContextCompat.getColor(this, arrowTitleColor));
        binding.tvTitle.setTextColor(ContextCompat.getColor(this, arrowTitleColor));
        binding.toolbar.setBackgroundResource(background);
        binding.llBack.setOnClickListener(v -> finish());
    }
    private String getDays() {
        String days = "0";
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
//        try {
//            Date expiredDate = format.parse(userModel.getData().getPackage_expired_date());
//            Date nowDate = calendar.getTime();
//            Log.e("date", expiredDate.toString() + "__" + expiredDate.getTime() + "___" + nowDate.toString() + "" + nowDate.getTime());
//            if (expiredDate.getTime() > nowDate.getTime()) {
//                long daysMill = 1000 * 60 * 60 * 24;
//                long diff = expiredDate.getTime() - nowDate.getTime();
//                long d = diff / daysMill;
//                days = String.valueOf(d);
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        return days;
    }

    private void getPackages() {
        binding.progBar.setVisibility(View.VISIBLE);
        list.clear();
        adapter.notifyDataSetChanged();
        Call<PackageDataModel> call = null;

        call = Api.getService(Tags.base_url)
                    .getPackages();
        if (call == null) {
            return;
        }

        call.enqueue(new Callback<PackageDataModel>() {
            @Override
            public void onResponse(Call<PackageDataModel> call, Response<PackageDataModel> response) {

                binding.progBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                  //  Log.e("code", response.body().getStatus() + "__");

                    if (response.body() != null && response.body().getStatus() == 200 && response.body().getData() != null) {
                    //    Log.e("ss", response.body().getData().size() + "__");
                        list.addAll(response.body().getData());
                        adapter.notifyDataSetChanged();
                    }


                } else {
                    try {
                        Log.e("error", response.code() + "__" + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            }

            @Override
            public void onFailure(Call<PackageDataModel> call, Throwable t) {
                try {
                    binding.progBar.setVisibility(View.GONE);

                    if (t.getMessage() != null) {
                        Log.e("error", t.getMessage() + "__");


                    }
                } catch (Exception e) {
                    Log.e("Error", e.getMessage() + "__");
                }
            }
        });
    }

    public void setItemPackage(PackageModel model) {
        selectedPackage = model;
        binding.btnSubscribe.setVisibility(View.VISIBLE);
    }
}