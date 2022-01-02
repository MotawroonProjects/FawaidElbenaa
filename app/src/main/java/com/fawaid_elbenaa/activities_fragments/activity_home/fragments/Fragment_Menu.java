package com.fawaid_elbenaa.activities_fragments.activity_home.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;


import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.fawaid_elbenaa.R;
import com.fawaid_elbenaa.activities_fragments.activity_chat_us.ChatUsActivity;
import com.fawaid_elbenaa.activities_fragments.activity_contact_us.ContactUsActivity;
import com.fawaid_elbenaa.activities_fragments.activity_google.activity_shop_query.ShopsQueryActivity;
import com.fawaid_elbenaa.activities_fragments.activity_home.HomeActivity;
import com.fawaid_elbenaa.activities_fragments.activity_language.LanguageActivity;
import com.fawaid_elbenaa.activities_fragments.activity_web_view.WebViewActivity;
import com.fawaid_elbenaa.adapters.google_adapters.PlaceCategoryAdapter;
import com.fawaid_elbenaa.databinding.FragmentMenuBinding;
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

public class Fragment_Menu extends Fragment {

    private HomeActivity activity;
    private FragmentMenuBinding binding;
    private String lang;
    private Preferences preferences;
    private UserModel userModel;


    public static Fragment_Menu newInstance() {
        return new Fragment_Menu();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_menu, container, false);
        initView();
        return binding.getRoot();
    }


    private void initView() {

        activity = (HomeActivity) getActivity();
        preferences = Preferences.getInstance();
        Paper.init(activity);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);

        binding.llLanguage.setOnClickListener(v -> {
            navigateToLanguageActivity();
        });

        binding.llAboutApp.setOnClickListener(v -> {
            String url = Tags.base_url + "app-setting#1";
            navigateToWebViewActivity(url);
        });

        binding.llContact.setOnClickListener(v -> {
            navigateToContactusActivity();
        });

        binding.llChatUs.setOnClickListener(v -> {
            navigateToChatActivity();
        });


        binding.llRate.setOnClickListener(v -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + activity.getPackageName())));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + activity.getPackageName())));
            }
        });

    }

    private void navigateToChatActivity() {
        userModel = preferences.getUserData(activity);
        if (userModel != null) {
            Intent intent = new Intent(activity, ChatUsActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(activity, getString(R.string.please_sign_in_or_sign_up), Toast.LENGTH_SHORT).show();
        }

    }


    private void navigateToWebViewActivity(String url) {
        Intent intent = new Intent(activity, WebViewActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    private void navigateToContactusActivity() {
        Intent intent = new Intent(activity, ContactUsActivity.class);
        startActivity(intent);
    }

    private void navigateToLanguageActivity() {
        Intent intent = new Intent(activity, LanguageActivity.class);
        startActivityForResult(intent, 1);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            lang = data.getStringExtra("lang");
            activity.refreshActivity(lang);
        }
    }
}
