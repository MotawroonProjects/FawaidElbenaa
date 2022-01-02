package com.fawaid_elbenaa.activities_fragments.activity_splash;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.fawaid_elbenaa.R;
import com.fawaid_elbenaa.activities_fragments.activity_home.HomeActivity;
import com.fawaid_elbenaa.activities_fragments.activity_login.LoginActivity;
import com.fawaid_elbenaa.databinding.ActivitySplashBinding;
import com.fawaid_elbenaa.language.Language;
import com.fawaid_elbenaa.models.UserModel;
import com.fawaid_elbenaa.preferences.Preferences;

import io.paperdb.Paper;

public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding binding;
    private Preferences preference;
    private UserModel userModel;

    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_splash);
        initView();
    }

    private void initView() {
        preference = Preferences.getInstance();
        userModel = preference.getUserData(this);
        new Handler().postDelayed(() -> {
            Intent intent;
            if (userModel==null){
                intent = new Intent(this, LoginActivity.class);

            }else {
                intent = new Intent(this, HomeActivity.class);

            }
            startActivity(intent);

            finish();

        },2000);
    }
}