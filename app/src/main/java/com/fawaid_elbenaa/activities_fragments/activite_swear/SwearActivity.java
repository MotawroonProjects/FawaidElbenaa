package com.fawaid_elbenaa.activities_fragments.activite_swear;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;

import com.fawaid_elbenaa.R;
import com.fawaid_elbenaa.databinding.ActivitySplashBinding;
import com.fawaid_elbenaa.databinding.ActivitySwearBinding;
import com.fawaid_elbenaa.generated.callback.OnClickListener;
import com.fawaid_elbenaa.language.Language;
import com.fawaid_elbenaa.models.UserModel;
import com.fawaid_elbenaa.preferences.Preferences;

import io.paperdb.Paper;

public class SwearActivity extends AppCompatActivity {

    private ActivitySwearBinding binding;
    private Preferences preference;
    private UserModel userModel;
    private String lang;
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_swear);
        initView();
    }

    private void initView() {
        Paper.init(this);
        lang = Paper.book().read("lang","ar");
        binding.setLang(lang);
        preference = Preferences.getInstance();
        userModel = preference.getUserData(this);

        binding.btnDone.setOnClickListener(view -> {
            finish();
        });
        binding.llBack.setOnClickListener(view -> finish());
    }
}
