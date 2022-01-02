package com.fawaid_elbenaa.activities_fragments.activity_comments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.fawaid_elbenaa.R;
import com.fawaid_elbenaa.adapters.Comment_Adapter;
import com.fawaid_elbenaa.databinding.ActivityCommentsBinding;
import com.fawaid_elbenaa.databinding.ActivityNotificationBinding;
import com.fawaid_elbenaa.language.Language;
import com.fawaid_elbenaa.models.CommentDataModel;
import com.fawaid_elbenaa.models.CommentModel;
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

public class CommentsActivity extends AppCompatActivity {
    private ActivityCommentsBinding binding;
    private String lang;
    private List<CommentModel> list;
    private Comment_Adapter adapter;
    private Preferences preferences;
    private UserModel userModel;
    private String product_id;
    private String country_guide_id;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_comments);
        getDataFromIntent();
        initView();
    }
    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("product_id")){
                product_id = intent.getIntExtra("product_id", 0)+"";

            }
            else {
                country_guide_id = intent.getIntExtra("guide_id", 0)+"";

            }

        }




    }



    private void initView() {
        list = new ArrayList<>();
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(   this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);
        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Comment_Adapter(list, this);
        binding.recView.setAdapter(adapter);
        binding.llBack.setOnClickListener(view -> finish());
        binding.swipeRefresh.setOnRefreshListener(this::getComments);
        getComments();

    }

    private void getComments(){
        try {


            Api.getService(Tags.base_url)
                    .getComments(product_id,country_guide_id, "all")
                    .enqueue(new Callback<CommentDataModel>() {
                        @Override
                        public void onResponse(Call<CommentDataModel> call, Response<CommentDataModel> response) {
                            binding.progBar.setVisibility(View.GONE);
                            binding.swipeRefresh.setRefreshing(false);
                            if (response.isSuccessful()) {

                                if (response.body()!=null&&response.body().getStatus() == 200) {
                                    if (response.body().getData().getComments().size()>0){
                                        binding.tvNoData.setVisibility(View.GONE);
                                        list.clear();
                                        list.addAll(response.body().getData().getComments());
                                        adapter.notifyDataSetChanged();
                                        binding.recView.postDelayed(()->{binding.recView.scrollToPosition(list.size()-1);}, 500);
                                    }else {
                                        binding.tvNoData.setVisibility(View.VISIBLE);

                                    }

                                } else {
                                  //  Toast.makeText(CommentsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }


                            } else {
                                binding.swipeRefresh.setRefreshing(false);

                                binding.progBar.setVisibility(View.GONE);

                                if (response.code() == 500) {
                                //    Toast.makeText(CommentsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                  //  Toast.makeText(CommentsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<CommentDataModel> call, Throwable t) {
                            try {
                                binding.swipeRefresh.setRefreshing(false);

                                binding.progBar.setVisibility(View.GONE);

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    //    Toast.makeText(CommentsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                      //  Toast.makeText(CommentsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (
                Exception e) {

        }
    }

}