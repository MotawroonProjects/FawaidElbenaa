package com.fawaid_elbenaa.activities_fragments.activity_home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.fawaid_elbenaa.activities_fragments.activity_add_ads.AddAdsActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.iid.FirebaseInstanceId;
import com.fawaid_elbenaa.R;
import com.fawaid_elbenaa.activities_fragments.activity_chat_admin.ChatAdminActivity;
import com.fawaid_elbenaa.activities_fragments.activity_contact_us.ContactUsActivity;
import com.fawaid_elbenaa.activities_fragments.activity_department_details.DepartmentDetailsActivity;
import com.fawaid_elbenaa.activities_fragments.activity_departments.DepartmentActivity;
import com.fawaid_elbenaa.activities_fragments.activity_home.fragments.Fragment_Department;
import com.fawaid_elbenaa.activities_fragments.activity_home.fragments.Fragment_Home;
import com.fawaid_elbenaa.activities_fragments.activity_home.fragments.Fragment_Menu;
import com.fawaid_elbenaa.activities_fragments.activity_home.fragments.Fragment_Profile;
import com.fawaid_elbenaa.activities_fragments.activity_login.LoginActivity;
import com.fawaid_elbenaa.activities_fragments.activity_notification.NotificationActivity;
import com.fawaid_elbenaa.activities_fragments.activity_sponsor.SponsorActivity;
import com.fawaid_elbenaa.activities_fragments.activity_web_view.WebViewActivity;
import com.fawaid_elbenaa.adapters.ExpandDepartmentAdapter;
import com.fawaid_elbenaa.databinding.ActivityHomeBinding;
import com.fawaid_elbenaa.language.Language;
import com.fawaid_elbenaa.models.AdminMessageDataModel;
import com.fawaid_elbenaa.models.ChatUserModel;
import com.fawaid_elbenaa.models.DepartmentModel;
import com.fawaid_elbenaa.models.MessageModel;
import com.fawaid_elbenaa.models.NotFireModel;
import com.fawaid_elbenaa.models.StatusResponse;
import com.fawaid_elbenaa.models.UserModel;
import com.fawaid_elbenaa.preferences.Preferences;
import com.fawaid_elbenaa.remote.Api;
import com.fawaid_elbenaa.share.Common;
import com.fawaid_elbenaa.tags.Tags;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {
    private ActivityHomeBinding binding;
    private Preferences preferences;
    private FragmentManager fragmentManager;
    private Fragment_Home fragment_home;
    private Fragment_Profile fragment_profile;
    private Fragment_Department fragment_department;
    private Fragment_Menu fragment_menu;
    private UserModel userModel;
    private String lang;
    private List<DepartmentModel> departmentModelList;
    private ExpandDepartmentAdapter expandDepartmentAdapter;
    private int parent_pos = -1, child_pos = -1;
    private float lastTranslate = 0.0f;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private final String gps_perm = Manifest.permission.ACCESS_FINE_LOCATION;
    private final int loc_req = 22;
    public double lat = 0.0, lng = 0.0;
    private Fragment displayedFragment;


    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        CheckPermission();

        initView();


    }

    private void initView() {
        departmentModelList = new ArrayList<>();
        fragmentManager = getSupportFragmentManager();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        binding.setModel(userModel);

        Paper.init(this);
        lang = Paper.book().read("lang", "ar");

        binding.setLang(lang);


        binding.flNotification.setOnClickListener(view -> {

            Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
            startActivity(intent);

        });
        binding.fab.setOnClickListener(v -> {
            if (userModel != null) {
                Intent intent = new Intent(this, AddAdsActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, getString(R.string.please_sign_in_or_sign_up), Toast.LENGTH_SHORT).show();
            }
        });
        binding.flHome.setOnClickListener(v -> {
            displayFragmentMain();
        });

        binding.flDepartment.setOnClickListener(v -> {
            displayFragmentDepartment();

        });

        binding.flProfile.setOnClickListener(v -> {
            displayFragmentProfile();

        });

        binding.flMenu.setOnClickListener(v -> {
            displayFragmentMenu();

        });


        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (fragment_home != null) {
                    fragment_home.search(editable.toString());
                }

            }
        });

        if (userModel != null) {
            EventBus.getDefault().register(this);
            updateTokenFireBase();

        }


//        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
//        binding.swipeRefresh.setOnRefreshListener(this::getDepartments);
        //  getDepartments();
      /*  binding.llabout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                binding.drawerLayout.closeDrawer(Gravity.RIGHT);
//                binding.tv1.setTextColor(getResources().getColor(R.color.gray6));
//                //  binding.tv2.setTextColor(getResources().getColor(R.color. gray6));
//                binding.tv3.setTextColor(getResources().getColor(R.color.gray6));
//                binding.tv4.setTextColor(getResources().getColor(R.color.colorAccent));
//                binding.tv5.setTextColor(getResources().getColor(R.color.gray6));
//                binding.image1.setColorFilter(R.color.gray6, android.graphics.PorterDuff.Mode.MULTIPLY);
//                //  binding.image2.setColorFilter(R.color. gray6, android.graphics.PorterDuff.Mode.MULTIPLY);
//                binding.image3.setColorFilter(R.color.gray6, android.graphics.PorterDuff.Mode.MULTIPLY);
//                binding.image4.setColorFilter(R.color.colorAccent, android.graphics.PorterDuff.Mode.MULTIPLY);
//                binding.image5.setColorFilter(R.color.gray6, android.graphics.PorterDuff.Mode.MULTIPLY);
                //  presenter.displayFragmentAboutus();
//        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
//        binding.swipeRefresh.setOnRefreshListener(this::getDepartments);
                //  getDepartments();
                String url = Tags.base_url+"app-setting#1";
                navigateToWebViewActivity(url);
            }
        });
        binding.llterms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  binding. drawerLayout.closeDrawer(Gravity.RIGHT);
//                binding.drawerLayout.closeDrawer(Gravity.RIGHT);
//                binding.tv1.setTextColor(getResources().getColor(R.color.gray6));
//                //binding.tv2.setTextColor(getResources().getColor(R.color. gray6));
//                binding.tv3.setTextColor(getResources().getColor(R.color.gray6));
//                binding.tv4.setTextColor(getResources().getColor(R.color.gray6));
//                binding.tv5.setTextColor(getResources().getColor(R.color.colorAccent));
//                binding.image1.setColorFilter(R.color.gray6, android.graphics.PorterDuff.Mode.MULTIPLY);
//                //binding.image2.setColorFilter(R.color. gray6, android.graphics.PorterDuff.Mode.MULTIPLY);
//                binding.image3.setColorFilter(R.color.gray6, android.graphics.PorterDuff.Mode.MULTIPLY);
//                binding.image4.setColorFilter(R.color.gray6, android.graphics.PorterDuff.Mode.MULTIPLY);
//                binding.image5.setColorFilter(R.color.colorAccent, android.graphics.PorterDuff.Mode.MULTIPLY);
                //  presenter.displayFragmentTerms();
                String url = Tags.base_url+"app-setting#2";
                navigateToWebViewActivity(url);
            }
        });
        binding.llcontactus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                binding.drawerLayout.closeDrawer(Gravity.RIGHT);
//                binding.tv1.setTextColor(getResources().getColor(R.color.gray6));
//                //binding.tv2.setTextColor(getResources().getColor(R.color. gray6));
//                binding.tv3.setTextColor(getResources().getColor(R.color.colorAccent));
//                binding.tv4.setTextColor(getResources().getColor(R.color.gray6));
//                binding.tv5.setTextColor(getResources().getColor(R.color.gray6));
//                binding.image1.setColorFilter(R.color.gray6, android.graphics.PorterDuff.Mode.MULTIPLY);
//                // binding.image2.setColorFilter(R.color. gray6, android.graphics.PorterDuff.Mode.MULTIPLY);
//                binding.image3.setColorFilter(R.color.colorAccent, android.graphics.PorterDuff.Mode.MULTIPLY);
//                binding.image4.setColorFilter(R.color.gray6, android.graphics.PorterDuff.Mode.MULTIPLY);
//                binding.image5.setColorFilter(R.color.gray6, android.graphics.PorterDuff.Mode.MULTIPLY);
                navigateToContactusActivity();
                // presenter.displayFragmentContactus();

            }
        });
        binding.llchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userModel!=null){
                    createChat();
                }
                else {
                    navigateToSignInActivity();
                }
//                binding.drawerLayout.closeDrawer(Gravity.RIGHT);
//                binding.tv1.setTextColor(getResources().getColor(R.color.colorAccent));
//                binding.tv3.setTextColor(getResources().getColor(R.color.gray6));
//                binding.tv4.setTextColor(getResources().getColor(R.color.gray6));
//                binding.tv5.setTextColor(getResources().getColor(R.color.gray6));
//                binding.image1.setColorFilter(R.color.colorAccent, android.graphics.PorterDuff.Mode.MULTIPLY);
//                binding.image3.setColorFilter(R.color.gray6, android.graphics.PorterDuff.Mode.MULTIPLY);
//                binding.image4.setColorFilter(R.color.gray6, android.graphics.PorterDuff.Mode.MULTIPLY);
//                binding.image5.setColorFilter(R.color.gray6, android.graphics.PorterDuff.Mode.MULTIPLY);

            }
        });
        binding.llsponsors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToSponsorsActivity();
            }
        });
        binding.lllogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userModel != null) {
                    deleteFirebaseToken();
                } else {
                    navigateToSignInActivity();
                    // Toast.makeText(HomeActivity.this, getResources().getString(R.string.please_sign_in_or_sign_up), Toast.LENGTH_LONG).show();

                }

            }

        });
        binding.flar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lang.equals("en")) {
                  refreshActivity("ar");
                }
            }
        });
        binding.flen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lang.equals("ar")) {
                    refreshActivity("en");
                }
            }
        });*/

    }

    private void navigateToChatActivity(MessageModel.RoomModel data) {
        ChatUserModel chatUserModel = new ChatUserModel(data.getAdmin_id(), getString(R.string.admin), "", data.getId(), "");
        Intent intent = new Intent(this, ChatAdminActivity.class);
        intent.putExtra("data", chatUserModel);
        startActivity(intent);
    }

    private void navigateToSponsorsActivity() {
        Intent intent = new Intent(this, SponsorActivity.class);
        startActivity(intent);
    }


    private void readNotificationCount() {
        binding.setNotCount(0);
    }


    public void displayFragmentMain() {
        try {
            updateHomUi();
            if (displayedFragment != null) {
                fragmentManager.beginTransaction().hide(displayedFragment).commit();
            }
            if (fragment_home == null) {
                fragment_home = Fragment_Home.newInstance();
            }
            displayedFragment = fragment_home;

            if (fragment_home.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_home).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_home, "fragment_home").commit();

            }

        } catch (Exception e) {
        }

    }

    public void displayFragmentMenu() {

        try {

            updateMenuUi();
            if (displayedFragment != null) {
                fragmentManager.beginTransaction().hide(displayedFragment).commit();
            }

            if (fragment_menu == null) {
                fragment_menu = Fragment_Menu.newInstance();
            }
            displayedFragment = fragment_menu;

            if (fragment_menu.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_menu).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_menu, "fragment_offer").commit();

            }
        } catch (Exception e) {
        }
    }

    public void displayFragmentProfile() {
        if (userModel == null) {
            Toast.makeText(this, getString(R.string.please_sign_in_or_sign_up), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            updateProfileUi();
            if (displayedFragment != null) {
                fragmentManager.beginTransaction().hide(displayedFragment).commit();
            }
            if (fragment_profile == null) {
                fragment_profile = Fragment_Profile.newInstance();
            }

            displayedFragment = fragment_profile;


            if (fragment_profile.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_profile).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_profile, "fragment_profile").commit();

            }
        } catch (Exception e) {
        }
    }

    public void displayFragmentDepartment() {

        try {
            updateDepartmentUi();

            if (displayedFragment != null) {
                fragmentManager.beginTransaction().hide(displayedFragment).commit();
            }
            if (fragment_department == null) {
                fragment_department = Fragment_Department.newInstance();
            }
            displayedFragment = fragment_department;

            if (fragment_department.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_department).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_app_container, fragment_department, "fragment_profile").addToBackStack("fragment_profile").commit();

            }
        } catch (Exception e) {
        }
    }


    private void updateHomUi() {
        binding.flsearch.setVisibility(View.GONE);

        binding.flHome.setBackgroundResource(0);
        binding.iconHome.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary));

        binding.flProfile.setBackgroundResource(0);
        binding.iconProfile.setColorFilter(ContextCompat.getColor(this, R.color.black));

        binding.flDepartment.setBackgroundResource(0);
        binding.iconDepartment.setColorFilter(ContextCompat.getColor(this, R.color.black));

        binding.flMenu.setBackgroundResource(0);
        binding.iconMenu.setColorFilter(ContextCompat.getColor(this, R.color.black));


    }

    private void updateDepartmentUi() {
        binding.flsearch.setVisibility(View.GONE);
        binding.flHome.setBackgroundResource(0);
        binding.iconHome.setColorFilter(ContextCompat.getColor(this, R.color.black));

        binding.flProfile.setBackgroundResource(0);
        binding.iconProfile.setColorFilter(ContextCompat.getColor(this, R.color.black));

        binding.flDepartment.setBackgroundResource(0);
        binding.iconDepartment.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary));

        binding.flMenu.setBackgroundResource(0);
        binding.iconMenu.setColorFilter(ContextCompat.getColor(this, R.color.black));

    }

    private void updateProfileUi() {
        binding.flsearch.setVisibility(View.GONE);
        binding.flHome.setBackgroundResource(0);
        binding.iconHome.setColorFilter(ContextCompat.getColor(this, R.color.black));

        binding.flProfile.setBackgroundResource(0);
        binding.iconProfile.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary));

        binding.flDepartment.setBackgroundResource(0);
        binding.iconDepartment.setColorFilter(ContextCompat.getColor(this, R.color.black));

        binding.flMenu.setBackgroundResource(0);
        binding.iconMenu.setColorFilter(ContextCompat.getColor(this, R.color.black));

    }

    private void updateMenuUi() {
        binding.flsearch.setVisibility(View.GONE);
        binding.flHome.setBackgroundResource(0);
        binding.iconHome.setColorFilter(ContextCompat.getColor(this, R.color.black));

        binding.flProfile.setBackgroundResource(0);
        binding.iconProfile.setColorFilter(ContextCompat.getColor(this, R.color.black));

        binding.flDepartment.setBackgroundResource(0);
        binding.iconDepartment.setColorFilter(ContextCompat.getColor(this, R.color.black));

        binding.flMenu.setBackgroundResource(0);
        binding.iconMenu.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary));

    }

    private void updateTokenFireBase() {

        FirebaseInstanceId.getInstance()
                .getInstanceId().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult().getToken();

                try {
                    Api.getService(Tags.base_url)
                            .updateFirebaseToken(token, userModel.getData().getId(), "android")
                            .enqueue(new Callback<StatusResponse>() {
                                @Override
                                public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                                    if (response.isSuccessful() && response.body() != null) {

                                        if (response.body().getStatus() == 200) {
                                            userModel.getData().setFirebaseToken(token);
                                            preferences.create_update_userdata(HomeActivity.this, userModel);
                                            Log.e("token", "updated successfully");

                                        }
                                    } else {
                                        try {

                                            Log.e("errorToken", response.code() + "_" + response.errorBody().string());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<StatusResponse> call, Throwable t) {
                                    try {

                                        if (t.getMessage() != null) {
                                            Log.e("errorToken2", t.getMessage());
                                            if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                                //         Toast.makeText(HomeActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                            } else {
                                                //       Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                    } catch (Exception e) {
                                    }
                                }
                            });
                } catch (Exception e) {


                }

            }
        });
    }

    public void deleteFirebaseToken() {
        if (userModel == null) {
            navigateToSignInActivity();

        } else {
            ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(true);
            dialog.show();

            Api.getService(Tags.base_url)
                    .deleteFirebaseToken(userModel.getData().getFirebaseToken(), userModel.getData().getId())
                    .enqueue(new Callback<StatusResponse>() {
                        @Override
                        public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {

                                if (response.body().getStatus() == 200) {
                                    logout(dialog);
                                } else {
                                    //         Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                dialog.dismiss();
                                try {
                                    Log.e("error", response.code() + "__" + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                if (response.code() == 500) {
                                    //    Toast.makeText(HomeActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                                } else {
                                    //  Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<StatusResponse> call, Throwable t) {
                            try {
                                dialog.dismiss();
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage() + "__");

                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        //   Toast.makeText(HomeActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } catch (Exception e) {
                                Log.e("Error", e.getMessage() + "__");
                            }
                        }
                    });
        }

    }

    private void logout(ProgressDialog dialog) {
        Api.getService(Tags.base_url)
                .logout("Bearer " + userModel.getData().getToken())
                .enqueue(new Callback<StatusResponse>() {
                    @Override
                    public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            if (response.body() != null && response.body().getStatus() == 200) {
                                preferences.clear(HomeActivity.this);
                                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                if (manager != null) {
                                    manager.cancel(Tags.not_tag, Tags.not_id);
                                }
                                navigateToSignInActivity();
                            } else {
                                //       Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                            }


                        } else {
                            dialog.dismiss();
                            try {
                                Log.e("error", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                                //  Toast.makeText(HomeActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else {
                                //  Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<StatusResponse> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    //    Toast.makeText(HomeActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    //  Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
    }

    public void refreshActivity(String lang) {
        Paper.book().write("lang", lang);
        Language.setNewLocale(this, lang);
        new Handler()
                .postDelayed(() -> {

                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }, 500);


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void listenToNotifications(NotFireModel notFireModel) {
        if (userModel != null) {
            getNotificationCount();


        }
    }

    private void getNotificationCount() {

    }

    @Override
    public void onBackPressed() {

        if (fragment_home != null && fragment_home.isAdded() && fragment_home.isVisible()) {
            if (userModel != null) {
                finish();
            } else {
                navigateToSignInActivity();
            }
        } else {
            displayFragmentMain();
        }
    }


    public void navigateToSignInActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    private void initGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    private void CheckPermission() {
        if (ActivityCompat.checkSelfPermission(this, gps_perm) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{gps_perm}, loc_req);
        } else {

            initGoogleApiClient();

        }
    }


    private void initLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setFastestInterval(1000);
        locationRequest.setInterval(60000);
        LocationSettingsRequest.Builder request = new LocationSettingsRequest.Builder();
        request.addLocationRequest(locationRequest);
        request.setAlwaysShow(false);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, request.build());

        result.setResultCallback(result1 -> {

            Status status = result1.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    startLocationUpdate();
                    break;
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        status.startResolutionForResult(HomeActivity.this, 1255);
                    } catch (Exception e) {
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    Log.e("not available", "not available");
                    break;
            }
        });

    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdate() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                onLocationChanged(locationResult.getLastLocation());
            }
        };
        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        initLocationRequest();
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();
        displayFragmentMain();
        binding.tvLocation.setVisibility(View.GONE);
        binding.coordData.setVisibility(View.VISIBLE);
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
        if (locationCallback != null) {
            LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        if (googleApiClient != null) {
            googleApiClient.disconnect();
            googleApiClient = null;
        }

        if (locationCallback != null) {
            LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (Fragment fragment : fragmentList) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == 1255 && resultCode == RESULT_OK) {
            startLocationUpdate();

        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (Fragment fragment : fragmentList) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        if (requestCode == loc_req) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initGoogleApiClient();

            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void setItemSubDepartmentData(DepartmentModel.SubCategory subCategory, int parent_pos, int adapterPosition) {
        DepartmentModel departmentModel = departmentModelList.get(parent_pos);
        Intent intent = new Intent(this, DepartmentDetailsActivity.class);
        intent.putExtra("data", departmentModel);
        intent.putExtra("child_pos", adapterPosition);
        startActivity(intent);

        this.parent_pos = parent_pos;
        this.child_pos = adapterPosition;
    }

    public void refreshFragmentOffers() {
        if (fragment_menu != null && fragment_menu.isAdded()) {
        }
    }

//    public void slide(float slideOffset) {
//        float moveFactor = (float) ((binding.cardview.getWidth() * slideOffset) / 1.5);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            binding. cardview.setTranslationX(-moveFactor);
//            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) binding. cardview.getLayoutParams();
//            if (binding.drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
//                params.setMargins(0, 0, 0, 0);
//            } else {
//                params.setMargins(0, 200, 0, 200);
//            }
//            binding. cardview.setLayoutParams(params);
//
//        } else {
//            TranslateAnimation anim = new TranslateAnimation(lastTranslate, -moveFactor, 0.0f, 0.0f);
//            anim.setDuration(0);
//            anim.setFillAfter(true);
//
//            lastTranslate = -moveFactor;
//            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) binding. cardview.getLayoutParams();
//            if (binding. drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
//                params.setMargins(0, 0, 0, 0);
//            } else {
//                params.setMargins(0, 200, 0, 200);
//            }
//            binding. cardview.setLayoutParams(params);
//            binding. cardview.startAnimation(anim);
//
//        }
//
//    }


    private void createChat() {
        try {
            ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            Api.getService(Tags.base_url)
                    .getAdminChatMessage("Bearer " + userModel.getData().getToken(), null)
                    .enqueue(new Callback<AdminMessageDataModel>() {
                        @Override
                        public void onResponse(Call<AdminMessageDataModel> call, Response<AdminMessageDataModel> response) {
                            dialog.dismiss();
                            if (response.isSuccessful()) {

                                if (response.body() != null && response.body().getStatus() == 200) {
                                    navigateToChatActivity(response.body().getData().getRoom());
                                } else {
                                    //     Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                }

                            } else {
                                dialog.dismiss();
                                if (response.code() == 500) {
                                    //   Toast.makeText(HomeActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    //    Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<AdminMessageDataModel> call, Throwable t) {
                            try {
                                dialog.dismiss();
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        //       Toast.makeText(HomeActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        //     Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }

}
