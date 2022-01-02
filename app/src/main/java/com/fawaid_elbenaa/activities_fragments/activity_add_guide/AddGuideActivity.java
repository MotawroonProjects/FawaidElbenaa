package com.fawaid_elbenaa.activities_fragments.activity_add_guide;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fawaid_elbenaa.R;
import com.fawaid_elbenaa.activities_fragments.activity_map.MapActivity;
import com.fawaid_elbenaa.adapters.ImageAdsAdapter;
import com.fawaid_elbenaa.adapters.SpinnerGovernorateAdapter;
import com.fawaid_elbenaa.databinding.ActivityAddAdsBinding;
import com.fawaid_elbenaa.databinding.ActivityAddGuideBinding;
import com.fawaid_elbenaa.databinding.ItemAddAdsBinding;
import com.fawaid_elbenaa.language.Language;
import com.fawaid_elbenaa.models.AddguideModel;
import com.fawaid_elbenaa.models.GovernorateDataModel;
import com.fawaid_elbenaa.models.GovernorateModel;
import com.fawaid_elbenaa.models.SelectedLocation;
import com.fawaid_elbenaa.models.StatusResponse;
import com.fawaid_elbenaa.models.UserModel;
import com.fawaid_elbenaa.preferences.Preferences;
import com.fawaid_elbenaa.remote.Api;
import com.fawaid_elbenaa.share.Common;
import com.fawaid_elbenaa.tags.Tags;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddGuideActivity extends AppCompatActivity {
    private ActivityAddGuideBinding binding;
    private String lang;

    private List<String> imagesUriList;
    private final String READ_PERM = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final String write_permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final String camera_permission = Manifest.permission.CAMERA;
    private final int READ_REQ = 1, CAMERA_REQ = 2;
    private double lat = 0.0, lng = 0.0;
    private String address = "";


    private ImageAdsAdapter imageAdsAdapter;
    private boolean isVideoAvailable = false;
    private AddguideModel model;
    private Preferences preferences;
    private UserModel userModel;

    private List<GovernorateModel> governorateModelList;
    private SpinnerGovernorateAdapter spinnerGovernorateAdapter;
    private String cat_id;
    private SelectedLocation selectedLocation;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_guide);
       getDataFromIntent();
        initView();
    }
    private void getDataFromIntent() {
        Intent intent = getIntent();
        cat_id =intent.getStringExtra("cat_id");


    }

    private void initView() {
        governorateModelList = new ArrayList<>();
        model = new AddguideModel();
        model.setCategory_id(Integer.parseInt(cat_id));

        imagesUriList = new ArrayList<>();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setModel(model);
        binding.setLang(lang);



        binding.recView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageAdsAdapter = new ImageAdsAdapter(imagesUriList, this);
        binding.recView.setAdapter(imageAdsAdapter);




        GovernorateModel governorateModel = new GovernorateModel(0, "إختر المحافظة", "Choose governorate");
        governorateModelList.add(governorateModel);

        spinnerGovernorateAdapter = new SpinnerGovernorateAdapter(governorateModelList, this);
        binding.spinnerGovernate.setAdapter(spinnerGovernorateAdapter);



        binding.spinnerGovernate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == 0) {
                    model.setGovernate_id(0);
                } else {
                    model.setGovernate_id(governorateModelList.get(i).getId());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        binding.flUploadImage.setOnClickListener(view -> {
            openSheet();
        });

        binding.flGallery.setOnClickListener(view -> checkReadPermission());

        binding.flCamera.setOnClickListener(view -> checkCameraPermission());

        binding.btnCancel.setOnClickListener(view -> {
            closeSheet();
        });



        binding.lllocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddGuideActivity.this, MapActivity.class);
                startActivityForResult(intent, 100);
            }
        });

        binding.llBack.setOnClickListener(view -> back());

        binding.btnSend.setOnClickListener(view -> checkDataValid());
        getGovernorate();


    }





    private void getGovernorate() {
        try {

            Api.getService(Tags.base_url)
                    .getGovernorate()
                    .enqueue(new Callback<GovernorateDataModel>() {
                        @Override
                        public void onResponse(Call<GovernorateDataModel> call, Response<GovernorateDataModel> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    if (response.body().getData().size() > 0) {
                                        governorateModelList.clear();
                                        GovernorateModel governorateModel = new GovernorateModel(0, "إختر المحافظة", "Choose governorate");

                                        governorateModelList.add(governorateModel);

                                        governorateModelList.addAll(response.body().getData());
                                        runOnUiThread(() -> spinnerGovernorateAdapter.notifyDataSetChanged());

                                    }
                                } else {
                                 //   Toast.makeText(AddGuideActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            } else {

                                if (response.code() == 500) {
                                   // Toast.makeText(AddGuideActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                  //  Toast.makeText(AddGuideActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<GovernorateDataModel> call, Throwable t) {
                            try {

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                   //     Toast.makeText(AddGuideActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                     //   Toast.makeText(AddGuideActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }


    }







    public void back() {
        finish();
    }

    public void openSheet() {
        binding.expandLayout.setExpanded(true, true);
    }

    public void closeSheet() {
        binding.expandLayout.collapse(true);

    }


    public void checkDataValid() {
        model.setImagesList(imagesUriList);
        if (model.isDataValid(this)) {
           // getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                addAdsWithoutVideoWithoutList();


        }
    }



    private void addAdsWithoutVideoWithoutList() {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        RequestBody title_part = Common.getRequestBodyText(model.getName());

        RequestBody whats_part = Common.getRequestBodyText(model.getWhatsappnum());
        RequestBody category_id_part = Common.getRequestBodyText(String.valueOf(model.getCategory_id()));
        RequestBody governorate_id = Common.getRequestBodyText(String.valueOf(model.getGovernate_id()));
        RequestBody details_part = Common.getRequestBodyText(String.valueOf(model.getDetails()));

       // RequestBody price_part = Common.getRequestBodyText(model.getPrice());
        RequestBody address_part = Common.getRequestBodyText(model.getAddress());
        RequestBody lat_part = Common.getRequestBodyText(String.valueOf(model.getLat()));
        RequestBody lng_part = Common.getRequestBodyText(String.valueOf(model.getLng()));
        MultipartBody.Part main_image_part = Common.getMultiPartImage(this, Uri.parse(imagesUriList.get(0)), "main_image");

        Api.getService(Tags.base_url)
                .addGuide("Bearer " + userModel.getData().getToken(), category_id_part, governorate_id, whats_part, address_part, lat_part, lng_part, details_part,title_part, main_image_part, getMultipartImage())
                .enqueue(new Callback<StatusResponse>() {
                    @Override
                    public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null) {
                            finish();
                        } else {
                            try {
                                Log.e("error", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                            //    Toast.makeText(AddGuideActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            }
                            {
                                Log.e("mmmmmmmmmm", response.code() + "__" + response.errorBody());

                              //  Toast.makeText(AddGuideActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<StatusResponse> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            if (t.getMessage() != null) {
                                Log.e("mmmmmmmmmm", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                //    Toast.makeText(AddGuideActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.e("ccccc", t.getMessage());

                                  //  Toast.makeText(AddGuideActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
    }


    private List<MultipartBody.Part> getMultipartImage() {
        List<MultipartBody.Part> parts = new ArrayList<>();
        for (String path : imagesUriList) {
            Uri uri = Uri.parse(path);
            MultipartBody.Part part = Common.getMultiPartImage(this, uri, "images[]");
            parts.add(part);
        }
        return parts;
    }

    public void checkReadPermission() {
        closeSheet();
        if (ActivityCompat.checkSelfPermission(this, READ_PERM) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{READ_PERM}, READ_REQ);
        } else {
            SelectImage(READ_REQ);
        }
    }




    public void checkCameraPermission() {

        closeSheet();

        if (ContextCompat.checkSelfPermission(this, write_permission) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, camera_permission) == PackageManager.PERMISSION_GRANTED
        ) {
            SelectImage(CAMERA_REQ);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{camera_permission, write_permission}, CAMERA_REQ);
        }
    }

    private void SelectImage(int req) {

        Intent intent = new Intent();

        if (req == READ_REQ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            } else {
                intent.setAction(Intent.ACTION_GET_CONTENT);

            }

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("image/*");
            startActivityForResult(intent, req);

        } else if (req == CAMERA_REQ) {
            try {
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, req);
            } catch (SecurityException e) {
                Toast.makeText(this, R.string.perm_image_denied, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, R.string.perm_image_denied, Toast.LENGTH_SHORT).show();

            }


        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
       if (requestCode == READ_REQ) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SelectImage(requestCode);
            } else {
                Toast.makeText(this, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == CAMERA_REQ) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                SelectImage(requestCode);
            } else {
                Toast.makeText(this, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
   if (requestCode == READ_REQ && resultCode == Activity.RESULT_OK && data != null) {

            Uri uri = data.getData();
            if (imagesUriList.size() < 6) {
                imagesUriList.add(uri.toString());
                imageAdsAdapter.notifyItemInserted(imagesUriList.size() - 1);
            } else {
                Toast.makeText(this, R.string.max_ad_photo, Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CAMERA_REQ && resultCode == Activity.RESULT_OK && data != null) {

            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            Uri uri = getUriFromBitmap(bitmap);
            if (uri != null) {
                if (imagesUriList.size() < 6) {
                    imagesUriList.add(uri.toString());
                    imageAdsAdapter.notifyItemInserted(imagesUriList.size() - 1);

                } else {
                    Toast.makeText(this, R.string.max_ad_photo, Toast.LENGTH_SHORT).show();
                }
            }


        }
   else if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
       if (data.hasExtra("location")) {
           selectedLocation = (SelectedLocation) data.getSerializableExtra("location");
           binding.tvLocation.setText(selectedLocation.getAddress());
           model.setLat(selectedLocation.getLat());
           model.setLng(selectedLocation.getLng());
           model.setAddress(selectedLocation.getAddress());
           binding.setModel(model);

       }
   }

    }


    private Uri getUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        return Uri.parse(MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "", ""));
    }











    public void deleteImage(int adapterPosition) {
        if (imagesUriList.size() > 0) {
            imagesUriList.remove(adapterPosition);
            imageAdsAdapter.notifyItemRemoved(adapterPosition);

        }
    }



}