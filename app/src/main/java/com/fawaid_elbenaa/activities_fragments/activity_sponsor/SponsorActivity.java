package com.fawaid_elbenaa.activities_fragments.activity_sponsor;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.fawaid_elbenaa.R;
import com.fawaid_elbenaa.adapters.SponsorAdapter2;
import com.fawaid_elbenaa.databinding.ActivitySponsorBinding;
import com.fawaid_elbenaa.language.Language;
import com.fawaid_elbenaa.models.SponsorsModel;
import com.fawaid_elbenaa.models.UserModel;
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

public class SponsorActivity extends AppCompatActivity {
    private ActivitySponsorBinding binding;
    private Preferences preference;
    private UserModel userModel;
    private SponsorAdapter2 adapter;
    private List<SponsorsModel.Sponsors> sponsorsList;
    private String lang;
    private int selectedPos=-1;

    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sponsor);
        initView();
    }

    private void initView() {
        sponsorsList = new ArrayList<>();
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        preference = Preferences.getInstance();
        userModel = preference.getUserData(this);
        binding.recView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new SponsorAdapter2(sponsorsList, this);
        binding.recView.setAdapter(adapter);
        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        binding.swipeRefresh.setOnRefreshListener(this::getSponsor);
        binding.llBack.setOnClickListener(view -> onBackPressed());

        getSponsor();
    }

    private void getSponsor() {

        Api.getService(Tags.base_url).
                getSponsor().
                enqueue(new Callback<SponsorsModel>() {
                    @Override
                    public void onResponse(Call<SponsorsModel> call, Response<SponsorsModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        binding.swipeRefresh.setRefreshing(false);

                        if (response.isSuccessful() && response.body() != null) {

                            sponsorsList.clear();
                            sponsorsList.addAll(response.body().getSponsors());


                            if (sponsorsList.size()>0)
                            {
                                adapter.notifyDataSetChanged();
                                binding.tvNoData.setVisibility(View.GONE);

                            }else
                            {
                                binding.tvNoData.setVisibility(View.VISIBLE);

                            }

                        } else {
                            binding.swipeRefresh.setRefreshing(false);

                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                           //     Toast.makeText(SponsorActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                            } else {
                             //   Toast.makeText(SponsorActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();


                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SponsorsModel> call, Throwable t) {
                        binding.progBar.setVisibility(View.GONE);
                        binding.swipeRefresh.setRefreshing(false);

                        try {
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                            //        Toast.makeText(SponsorActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                              //      Toast.makeText(SponsorActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {
                        }



                    }
                });

    }

    @Override
    public void onBackPressed() {
        finish();
    }


}