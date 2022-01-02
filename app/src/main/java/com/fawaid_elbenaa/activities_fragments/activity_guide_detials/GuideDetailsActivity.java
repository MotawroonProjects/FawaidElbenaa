package com.fawaid_elbenaa.activities_fragments.activity_guide_detials;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.fawaid_elbenaa.R;
import com.fawaid_elbenaa.activities_fragments.FragmentMapTouchListener;
import com.fawaid_elbenaa.activities_fragments.activity_comments.CommentsActivity;
import com.fawaid_elbenaa.adapters.Comment_Adapter;
import com.fawaid_elbenaa.adapters.SingleCommentDataModel;
import com.fawaid_elbenaa.adapters.Slider2Adapter;
import com.fawaid_elbenaa.databinding.ActivityGuideDetailsBinding;
import com.fawaid_elbenaa.databinding.ActivityProductDetailsBinding;
import com.fawaid_elbenaa.language.Language;
import com.fawaid_elbenaa.models.CommentDataModel;
import com.fawaid_elbenaa.models.CommentModel;
import com.fawaid_elbenaa.models.PlaceModel;
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

public class GuideDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ActivityGuideDetailsBinding binding;
    private String lang;
    private Preferences preferences;
    private UserModel userModel;
    private PlaceModel productModel;
    private Slider2Adapter sliderAdapter;
    private List<PlaceModel.Image> productImageModelList;
    private boolean isDataChanged = false;
    private GoogleMap mMap;
    private Comment_Adapter commentAdapter;
    private List<CommentModel> commentModelList;
    private int commentCount = 0;
;
    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_guide_details);
        getDataFromIntent();

        initView();

    }


    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getSerializableExtra("data")!=null){
                productModel = (PlaceModel) intent.getSerializableExtra("data");

            }

        }




    }


    private void initView() {

        commentModelList = new ArrayList<>();
        productImageModelList = new ArrayList<>();
        Paper.init(this);
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);
        binding.setUserModel(userModel);
      updateUi();
        binding.tab.setupWithViewPager(binding.pager);






        sliderAdapter = new Slider2Adapter(productImageModelList, this);
        binding.pager.setAdapter(sliderAdapter);


        commentAdapter = new Comment_Adapter(commentModelList,this);
        binding.recViewComment.setLayoutManager(new LinearLayoutManager(this));
        binding.recViewComment.setAdapter(commentAdapter);






        binding.flChatViaWhatsApp.setOnClickListener(view -> {
            String phone = productModel.getUser().getPhone_code() + productModel.getUser().getWhatsapp_number();
            String url = "https://api.whatsapp.com/send?phone=" + phone;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });



        binding.imageSend.setOnClickListener(v -> {
            String comment = binding.edtMessage.getText().toString().trim();
            if (!comment.isEmpty()){
                binding.edtMessage.setText(null);
                sendComment(comment);
            }
        });

        /*binding.image.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileProductsActivity.class);
            intent.putExtra("user_id", productModel.getUser().getId());
            startActivity(intent);
        });*/

        binding.tvShowAll.setOnClickListener(v -> {
            Intent intent = new Intent(this, CommentsActivity.class);
            intent.putExtra("guide_id", productModel.getId());
            startActivity(intent);
        });
        binding.llBack.setOnClickListener(view -> {
            onBackPressed();
        });




    }

    private void sendComment(String comment) {
        try {
            Api.getService(Tags.base_url)
                    .sendComments("Bearer " + userModel.getData().getToken(),null, productModel.getId()+"", comment)
                    .enqueue(new Callback<SingleCommentDataModel>() {
                        @Override
                        public void onResponse(Call<SingleCommentDataModel> call, Response<SingleCommentDataModel> response) {
                            if (response.isSuccessful()) {
                                if (response.body()!=null&&response.body().getStatus() == 200) {
                                    binding.llNoComment.setVisibility(View.GONE);
                                    commentCount++;
                                    String c = getString(R.string.comments)+"("+commentCount+")";
                                    binding.tvComments.setText(c);
                                    commentModelList.add(response.body().getData());
                                    commentAdapter.notifyItemChanged(commentModelList.size()-1);
                                    binding.recViewComment.postDelayed(()->{binding.recViewComment.scrollToPosition(commentModelList.size()-1);}, 500);

                                } else {

                                 //   Toast.makeText(ProductDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                }
                            } else {

                                if (response.code() == 500) {
                                //    Toast.makeText(ProductDetailsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                  //  Toast.makeText(ProductDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<SingleCommentDataModel> call, Throwable t) {
                            try {

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                              //          Toast.makeText(ProductDetailsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                //        Toast.makeText(ProductDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {
        }


    }




    private void getComments(){
        try {


            Api.getService(Tags.base_url)
                    .getComments(null,productModel.getId()+"", "limit")
                    .enqueue(new Callback<CommentDataModel>() {
                        @Override
                        public void onResponse(Call<CommentDataModel> call, Response<CommentDataModel> response) {
                            binding.progBar.setVisibility(View.GONE);
                            if (response.isSuccessful()) {

                                if (response.body()!=null&&response.body().getStatus() == 200) {
                                    commentCount = response.body().getData().getCount_of_all_comments();
                                    String c = getString(R.string.comments)+"("+commentCount+")";
                                    binding.tvComments.setText(c);
                                    if (response.body().getData().getComments().size()>0){
                                        binding.llNoComment.setVisibility(View.GONE);
                                        commentModelList.clear();
                                        commentModelList.addAll(response.body().getData().getComments());
                                        commentAdapter.notifyDataSetChanged();
                                        binding.recViewComment.postDelayed(()->{binding.recViewComment.scrollToPosition(commentModelList.size()-1);}, 500);
                                    }else {
                                        binding.llNoComment.setVisibility(View.VISIBLE);

                                    }

                                } else {
                              //      Toast.makeText(ProductDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }


                            } else {
                                binding.progBar.setVisibility(View.GONE);

                                if (response.code() == 500) {
                                //    Toast.makeText(ProductDetailsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                  //  Toast.makeText(ProductDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

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
                                binding.progBar.setVisibility(View.GONE);

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                 //       Toast.makeText(ProductDetailsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                   //     Toast.makeText(ProductDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void updateUi() {
productImageModelList.addAll(productModel.getImages());
        /*if (productModel.getIs_report().equals("yes")) {
            binding.imgWarning.setColorFilter(ContextCompat.getColor(ProductDetailsActivity.this, R.color.colorPrimary));
        } else {
            binding.imgWarning.setColorFilter(ContextCompat.getColor(ProductDetailsActivity.this, R.color.gray4));

        }
*/


       /* if (productModel.getIs_report().equals("yes")) {
            binding.imgWarning.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary));
        } else {
            binding.imgWarning.setColorFilter(ContextCompat.getColor(this, R.color.gray4));

        }*/




        binding.setModel(productModel);
        binding.flChatViaWhatsApp.setVisibility(View.VISIBLE);
        binding.scrollView.setVisibility(View.VISIBLE);

        FragmentMapTouchListener fragment = (FragmentMapTouchListener) getSupportFragmentManager().findFragmentById(R.id.map);
        if (fragment!=null){
            fragment.getMapAsync(this);
            fragment.setListener(() -> { binding.scrollView.requestDisallowInterceptTouchEvent(true); });
        }
        getComments();

    }


    @Override
    public void onBackPressed() {
        if (isDataChanged) {
            setResult(RESULT_OK);
        }
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap!=null){
            mMap = googleMap;
            mMap.setBuildingsEnabled(false);
            mMap.setTrafficEnabled(false);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.getUiSettings().setZoomControlsEnabled(false);
            addMarker();
        }
    }

    private void addMarker() {
        LatLng latLng = new LatLng(productModel.getLatitude(), productModel.getLongitude());
        mMap.addMarker(new MarkerOptions().title(productModel.getAddress()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).position(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15.6f));
    }
}
