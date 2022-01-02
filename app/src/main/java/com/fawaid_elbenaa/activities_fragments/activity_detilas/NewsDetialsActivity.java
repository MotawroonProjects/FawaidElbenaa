package com.fawaid_elbenaa.activities_fragments.activity_detilas;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


import com.fawaid_elbenaa.R;
import com.fawaid_elbenaa.databinding.ActivityNewsDetialsBinding;
import com.fawaid_elbenaa.language.Language;
import com.fawaid_elbenaa.models.NewsModel;
import com.fawaid_elbenaa.preferences.Preferences;

import io.paperdb.Paper;


public class NewsDetialsActivity extends AppCompatActivity{
    private ActivityNewsDetialsBinding binding;
    private String lang;
    private Preferences preferences;
    private NewsModel.News news;


    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_news_detials);
        getDataFromIntent();
        initView();
    }

    private void initView() {
        preferences = Preferences.getInstance();

        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        binding.setModel(news);
        binding.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });


    }
    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
          news= (NewsModel.News) intent.getSerializableExtra("data");
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        back();
    }


    public void back() {
        finish();
    }
}