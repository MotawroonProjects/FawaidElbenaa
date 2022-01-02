package com.fawaid_elbenaa.activities_fragments.activity_home.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;


import com.fawaid_elbenaa.R;
import com.fawaid_elbenaa.activities_fragments.activity_add_ads.AddAdsActivity;
import com.fawaid_elbenaa.activities_fragments.activity_chat_us.ChatUsActivity;
import com.fawaid_elbenaa.activities_fragments.activity_commission.CommissionActivity;
import com.fawaid_elbenaa.activities_fragments.activity_home.HomeActivity;
import com.fawaid_elbenaa.activities_fragments.activity_my_ads.MyAdsActivity;
import com.fawaid_elbenaa.activities_fragments.activity_my_favorite.MyFavoriteActivity;
import com.fawaid_elbenaa.activities_fragments.activity_setting.SettingActivity;
import com.fawaid_elbenaa.activities_fragments.activity_sign_up.SignUpActivity;
import com.fawaid_elbenaa.activities_fragments.activity_contact_us.ContactUsActivity;
import com.fawaid_elbenaa.databinding.FragmentProfileBinding;
import com.fawaid_elbenaa.interfaces.Listeners;
import com.fawaid_elbenaa.models.UserModel;
import com.fawaid_elbenaa.preferences.Preferences;

import io.paperdb.Paper;

public class Fragment_Profile extends Fragment {

    private HomeActivity activity;
    private FragmentProfileBinding binding;
    private Preferences preferences;
    private String lang;
    private UserModel userModel;

    public static Fragment_Profile newInstance() {

        return new Fragment_Profile();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();

    }

    private void initView() {
        activity = (HomeActivity) getActivity();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(activity);
        Paper.init(activity);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        binding.setModel(userModel);

        binding.cardViewMyAds.setOnClickListener(v -> {
            if (userModel != null) {
                Intent intent = new Intent(activity, MyAdsActivity.class);
                startActivityForResult(intent, 200);
            }
        });


        binding.cardViewFav.setOnClickListener(v -> {
            if (userModel != null) {
                Intent intent = new Intent(activity, MyFavoriteActivity.class);
                startActivity(intent);
            }
        });
        binding.cardViewEdit.setOnClickListener(v -> {
            if (userModel != null) {
                Intent intent = new Intent(activity, SignUpActivity.class);
                startActivityForResult(intent, 100);
            }
        });


        binding.llLogout.setOnClickListener(v -> {
            if (userModel != null) {
                activity.deleteFirebaseToken();
            }
        });


    }


    public void onAddAd() {
        if (userModel != null) {
            Intent intent = new Intent(activity, AddAdsActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(activity, getString(R.string.please_sign_in_or_sign_up), Toast.LENGTH_SHORT).show();
        }


    }

    public void onCommission() {
        Intent intent = new Intent(activity, CommissionActivity.class);
        startActivity(intent);
    }


    public void onSetting() {
        Intent intent = new Intent(activity, SettingActivity.class);
        startActivityForResult(intent, 300);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            userModel = preferences.getUserData(activity);
            binding.setModel(userModel);

        }
    }
}
