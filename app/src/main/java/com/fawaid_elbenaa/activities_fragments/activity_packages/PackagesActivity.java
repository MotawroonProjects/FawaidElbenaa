package com.fawaid_elbenaa.activities_fragments.activity_packages;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;


import com.fawaid_elbenaa.R;
import com.fawaid_elbenaa.activities_fragments.activity_pay.PayActivity;
import com.fawaid_elbenaa.activities_fragments.activity_verification_code.VerificationCodeActivity;
import com.fawaid_elbenaa.adapters.PackageAdapter;
import com.fawaid_elbenaa.databinding.ActivityPackagesBinding;
import com.fawaid_elbenaa.databinding.ToolbarBinding;
import com.fawaid_elbenaa.language.Language;
import com.fawaid_elbenaa.models.InvoiceModel;
import com.fawaid_elbenaa.models.PackageDataModel;
import com.fawaid_elbenaa.models.PackageModel;
import com.fawaid_elbenaa.models.UserModel;
import com.fawaid_elbenaa.preferences.Preferences;
import com.fawaid_elbenaa.remote.Api;
import com.fawaid_elbenaa.share.Common;
import com.fawaid_elbenaa.tags.Tags;
import com.google.gson.Gson;

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
        list = new ArrayList<>();
        adapter = new PackageAdapter(list, this);
        binding.recView.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recView.setAdapter(adapter);
        binding.btnSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedPackage != null) {
                    payPackage();
                }
            }
        });
        getData();

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
        //   Log.e("ldldll",userModel.getData().getId()+"");
        String days = "0";
        Calendar calendar = Calendar.getInstance();
        if (userModel.getData().getPackage_date() != null) {
            Log.e("ddddd", userModel.getData().getPackage_date());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            try {
                Date expiredDate = format.parse(userModel.getData().getPackage_date());
                Date nowDate = calendar.getTime();
                if (expiredDate.getTime() > nowDate.getTime()) {
                    long daysMill = 1000 * 60 * 60 * 24;
                    long diff = expiredDate.getTime() - nowDate.getTime();
                    long d = diff / daysMill;
                    days = String.valueOf(d + 1);

                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
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

    private void payPackage() {
        Log.e("dldlld", "Bearer " + userModel.getData().getToken());
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .payPackge("Bearer " + userModel.getData().getToken(), userModel.getData().getId() + "", selectedPackage.getId() + "")
                .enqueue(new Callback<InvoiceModel>() {
                    @Override
                    public void onResponse(Call<InvoiceModel> call, Response<InvoiceModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null) {


                            if (response.body().isSuccess() && selectedPackage.getPrice() != 0) {
                                Intent intent = new Intent(PackagesActivity.this, PayActivity.class);
                                intent.putExtra("url", response.body().getData().getInvoiceURL());
                                startActivityForResult(intent, 1);
                            } else {
                                Toast.makeText(PackagesActivity.this, getResources().getString(R.string.suc), Toast.LENGTH_LONG).show();
                                getData();
                            }

                        } else {
                            Log.e("ttyyty", response.code() + "");
                            try {
                                Log.e("ttyyty", response.code() + "--" + response.errorBody().string());
                            } catch (IOException e) {
                                // e.printStackTrace();
                            }
                            if (response.code() == 500) {
                                //  Toast.makeText(VerificationCodeActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else {
                                //Toast.makeText(VerificationCodeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<InvoiceModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            if (t.getMessage() != null) {
                                Log.e("msg_category_error", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    //  Toast.makeText(VerificationCodeActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    //Toast.makeText(VerificationCodeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });

    }

    private void getProfile() {
        try {
            ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
            dialog.setCancelable(false);
            dialog.show();
            Api.getService(Tags.base_url)
                    .getProfile("Bearer " + userModel.getData().getToken(), userModel.getData().getId())
                    .enqueue(new Callback<UserModel>() {
                        @Override
                        public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                            dialog.dismiss();
                            if (response.isSuccessful()) {
                                Log.e("ssss", response.body().getStatus() + "");
                                if (response.body() != null && response.body().getStatus() == 200) {

                                    updateData(response.body());
                                } else {
                                    //        Toast.makeText(ProfileProductsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                }

                            } else {

                                if (response.code() == 500) {
                                    //      Toast.makeText(ProfileProductsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    //    Toast.makeText(ProfileProductsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<UserModel> call, Throwable t) {
                            try {
                                dialog.dismiss();
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        //      Toast.makeText(ProfileProductsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        //    Toast.makeText(ProfileProductsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e(";llllll", e.toString());
        }
    }

    private void updateData(UserModel body) {
        body.getData().setToken(userModel.getData().getToken());
        userModel = body;
        binding.setDays(getDays());

    }

    public void setItemPackage(PackageModel model) {
        selectedPackage = model;
        binding.btnSubscribe.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            getData();
        }
    }

    private void getData() {
        getPackages();
        getProfile();
    }
}