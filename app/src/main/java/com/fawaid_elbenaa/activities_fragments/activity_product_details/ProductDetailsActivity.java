package com.fawaid_elbenaa.activities_fragments.activity_product_details;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
import com.fawaid_elbenaa.activities_fragments.activity_chat_admin.ChatAdminActivity;
import com.fawaid_elbenaa.activities_fragments.activity_comments.CommentsActivity;
import com.fawaid_elbenaa.adapters.Comment_Adapter;
import com.fawaid_elbenaa.adapters.ProductDetailsAdapter;
import com.fawaid_elbenaa.adapters.SingleCommentDataModel;
import com.fawaid_elbenaa.adapters.SliderAdapter;
import com.fawaid_elbenaa.databinding.ActivityProductDetailsBinding;
import com.fawaid_elbenaa.language.Language;
import com.fawaid_elbenaa.models.AdminMessageDataModel;
import com.fawaid_elbenaa.models.ChatUserModel;
import com.fawaid_elbenaa.models.CommentDataModel;
import com.fawaid_elbenaa.models.CommentModel;
import com.fawaid_elbenaa.models.MessageModel;
import com.fawaid_elbenaa.models.ProductImageModel;
import com.fawaid_elbenaa.models.ProductModel;
import com.fawaid_elbenaa.models.SingleProductDataModel;
import com.fawaid_elbenaa.models.StatusResponse;
import com.fawaid_elbenaa.models.UserModel;
import com.fawaid_elbenaa.preferences.Preferences;
import com.fawaid_elbenaa.remote.Api;
import com.fawaid_elbenaa.share.Common;
import com.fawaid_elbenaa.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ActivityProductDetailsBinding binding;
    private String lang;
    private Preferences preferences;
    private UserModel userModel;
    private int product_id;
    private ProductModel productModel;
    private ProductDetailsAdapter adapter;
    private List<ProductModel.ProductDetail> productDetailsModelList;
    private SliderAdapter sliderAdapter;
    private List<ProductImageModel> productImageModelList;
    private boolean isDataChanged = false;
    private GoogleMap mMap;
    private Comment_Adapter commentAdapter;
    private List<CommentModel> commentModelList;
    private int commentCount = 0;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_details);
        getDataFromIntent();

        initView();

    }


    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("product_id")) {
                product_id = intent.getIntExtra("product_id", 0);
                Log.e("product_id", product_id + "");

            } else {
                product_id = Integer.parseInt(intent.getData().getLastPathSegment());
            }

        }


    }


    private void initView() {

        commentModelList = new ArrayList<>();
        productImageModelList = new ArrayList<>();
        productDetailsModelList = new ArrayList<>();
        Paper.init(this);
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);
        binding.setUserModel(userModel);
        binding.tab.setupWithViewPager(binding.pager);


        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductDetailsAdapter(productDetailsModelList, this);
        binding.recView.setAdapter(adapter);


        sliderAdapter = new SliderAdapter(productImageModelList, this);
        binding.pager.setAdapter(sliderAdapter);


        commentAdapter = new Comment_Adapter(commentModelList, this);
        binding.recViewComment.setLayoutManager(new LinearLayoutManager(this));
        binding.recViewComment.setAdapter(commentAdapter);

        binding.checkFavorite.setOnClickListener(view -> {
            if (binding.checkFavorite.isChecked()) {
                like_dislike(true);
            } else {
                like_dislike(false);

            }
        });

        binding.iconCopy.setOnClickListener(view -> {

            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", productModel.getUser().getPhone_code() + productModel.getUser().getPhone());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, R.string.copied, Toast.LENGTH_SHORT).show();
        });

        binding.flCall.setOnClickListener(view -> {
            if (productModel.getIs_active().equals("yes")) {
                String phone = productModel.getUser().getPhone_code() + productModel.getUser().getPhone();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                startActivity(intent);
            } else {
                Toast.makeText(this, R.string.sold, Toast.LENGTH_SHORT).show();
            }

        });

        binding.iconWhatsApp.setOnClickListener(view -> {
            if (productModel.getIs_active().equals("yes")) {
                String phone = productModel.getUser().getPhone_code() + productModel.getUser().getPhone();
                String url = "https://api.whatsapp.com/send?phone=" + phone;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            } else {
                Toast.makeText(this, R.string.sold, Toast.LENGTH_SHORT).show();
            }


        });

        binding.flChatViaWhatsApp.setOnClickListener(v -> {
            if (productModel.getIs_active().equals("yes")) {
                String phone = productModel.getUser().getPhone_code() + productModel.getUser().getPhone();
                String url = "https://api.whatsapp.com/send?phone=" + phone;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            } else {
                Toast.makeText(this, R.string.sold, Toast.LENGTH_SHORT).show();
            }


        });

        binding.imageSend.setOnClickListener(v -> {
            String comment = binding.edtMessage.getText().toString().trim();
            if (!comment.isEmpty()) {
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
            intent.putExtra("product_id", product_id);
            startActivity(intent);
        });
        binding.llBack.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.llChat.setOnClickListener(view -> {
            createChat();
        });

        binding.imgShare.setOnClickListener(v -> {
            String data = getString(R.string.app_name) + "\n" + productModel.getTitle() + "\n" + Tags.base_url + "product_details/" + productModel.getId();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, data);
            startActivity(intent);
        });

        getProductById();
    }

    private void sendComment(String comment) {
        try {
            Api.getService(Tags.base_url)
                    .sendComments("Bearer " + userModel.getData().getToken(), product_id + "", null, comment)
                    .enqueue(new Callback<SingleCommentDataModel>() {
                        @Override
                        public void onResponse(Call<SingleCommentDataModel> call, Response<SingleCommentDataModel> response) {
                            if (response.isSuccessful()) {
                                if (response.body() != null && response.body().getStatus() == 200) {
                                    binding.llNoComment.setVisibility(View.GONE);
                                    commentCount++;
                                    String c = getString(R.string.comments) + "(" + commentCount + ")";
                                    binding.tvComments.setText(c);
                                    commentModelList.add(response.body().getData());
                                    adapter.notifyItemChanged(commentModelList.size() - 1);
                                    binding.recViewComment.postDelayed(() -> {
                                        binding.recViewComment.scrollToPosition(commentModelList.size() - 1);
                                    }, 500);

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

    private void createChat() {
        try {
            ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            Api.getService(Tags.base_url)
                    .getAdminChatMessage("Bearer " + userModel.getData().getToken(), productModel.getUser().getId() + "")
                    .enqueue(new Callback<AdminMessageDataModel>() {
                        @Override
                        public void onResponse(Call<AdminMessageDataModel> call, Response<AdminMessageDataModel> response) {
                            dialog.dismiss();
                            if (response.isSuccessful()) {

                                if (response.body() != null && response.body().getStatus() == 200) {
                                    navigateToChatActivity(response.body().getData().getRoom());
                                } else {
                                    //     Toast.makeText(ProductDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                }

                            } else {
                                dialog.dismiss();
                                if (response.code() == 500) {
                                    //   Toast.makeText(ProductDetailsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


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
                        public void onFailure(Call<AdminMessageDataModel> call, Throwable t) {
                            try {
                                dialog.dismiss();
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        //        Toast.makeText(ProductDetailsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        //      Toast.makeText(ProductDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }

    private void navigateToChatActivity(MessageModel.RoomModel data) {
        ChatUserModel chatUserModel = new ChatUserModel(data.getAdmin_id(), getString(R.string.admin), "", data.getId(), product_id + "");
        Intent intent = new Intent(this, ChatAdminActivity.class);
        intent.putExtra("data", chatUserModel);
        startActivity(intent);
    }

    private void getProductById() {

        try {
            binding.scrollView.setVisibility(View.GONE);
            binding.progBar.setVisibility(View.VISIBLE);
            String user_id = "";
            if (userModel != null) {
                user_id = String.valueOf(userModel.getData().getId());
            }

            Api.getService(Tags.base_url)
                    .getProductById(product_id, user_id)
                    .enqueue(new Callback<SingleProductDataModel>() {
                        @Override
                        public void onResponse(Call<SingleProductDataModel> call, Response<SingleProductDataModel> response) {
                            binding.progBar.setVisibility(View.GONE);
                            if (response.isSuccessful()) {

                                if (response.body().getStatus() == 200) {
                                    if (response.body().getData() != null) {
                                        productModel = response.body().getData();
                                        if (productModel != null) {
                                            updateUi();
                                        }
                                    }

                                } else {
                                    //   Toast.makeText(ProductDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }


                            } else {
                                binding.progBar.setVisibility(View.GONE);

                                if (response.code() == 500) {
                                    //          Toast.makeText(ProductDetailsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    //        Toast.makeText(ProductDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<SingleProductDataModel> call, Throwable t) {
                            try {
                                binding.progBar.setVisibility(View.GONE);

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        //           Toast.makeText(ProductDetailsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        //         Toast.makeText(ProductDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void getComments() {
        try {


            Api.getService(Tags.base_url)
                    .getComments(product_id + "", null, "limit")
                    .enqueue(new Callback<CommentDataModel>() {
                        @Override
                        public void onResponse(Call<CommentDataModel> call, Response<CommentDataModel> response) {
                            binding.progBar.setVisibility(View.GONE);
                            if (response.isSuccessful()) {

                                if (response.body() != null && response.body().getStatus() == 200) {
                                    commentCount = response.body().getData().getCount_of_all_comments();
                                    String c = getString(R.string.comments) + "(" + commentCount + ")";
                                    binding.tvComments.setText(c);
                                    if (response.body().getData().getComments().size() > 0) {
                                        binding.llNoComment.setVisibility(View.GONE);
                                        commentModelList.clear();
                                        commentModelList.addAll(response.body().getData().getComments());
                                        commentAdapter.notifyDataSetChanged();
                                        binding.recViewComment.postDelayed(() -> {
                                            binding.recViewComment.scrollToPosition(commentModelList.size() - 1);
                                        }, 500);
                                    } else {
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

        /*if (productModel.getIs_report().equals("yes")) {
            binding.imgWarning.setColorFilter(ContextCompat.getColor(ProductDetailsActivity.this, R.color.colorPrimary));
        } else {
            binding.imgWarning.setColorFilter(ContextCompat.getColor(ProductDetailsActivity.this, R.color.gray4));

        }
*/

        binding.flAction.setVisibility(View.VISIBLE);
        if (productModel.getProduct_details() != null && productModel.getProduct_details().size() > 0) {
            productDetailsModelList.addAll(productModel.getProduct_details());
            adapter.notifyDataSetChanged();
        }

        if (productModel.getIs_favorite().equals("yes")) {
            binding.checkFavorite.setChecked(true);
        } else {
            binding.checkFavorite.setChecked(false);

        }

       /* if (productModel.getIs_report().equals("yes")) {
            binding.imgWarning.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary));
        } else {
            binding.imgWarning.setColorFilter(ContextCompat.getColor(this, R.color.gray4));

        }*/

        if (productModel.getVideo() != null) {
            productImageModelList.add(new ProductImageModel(0, productModel.getVideo(), "video"));
        }
        if (productModel.getProduct_images() != null && productModel.getProduct_images().size() > 0) {
            binding.flNoSlider.setVisibility(View.GONE);
            binding.flSlider.setVisibility(View.VISIBLE);
            productImageModelList.addAll(productModel.getProduct_images());

            sliderAdapter.notifyDataSetChanged();
        } else {
            binding.flNoSlider.setVisibility(View.VISIBLE);
            binding.flSlider.setVisibility(View.GONE);
        }


        binding.setModel(productModel);

        //binding.flChatViaWhatsApp.setVisibility(View.VISIBLE);
        binding.scrollView.setVisibility(View.VISIBLE);

//        FragmentMapTouchListener fragment = (FragmentMapTouchListener) getSupportFragmentManager().findFragmentById(R.id.map);
//        if (fragment!=null){
//            fragment.getMapAsync(this);
//            fragment.setListener(() -> { binding.scrollView.requestDisallowInterceptTouchEvent(true); });
//        }
        getComments();

    }

    public void like_dislike(boolean isChecked) {
        if (userModel == null) {
            if (isChecked) {
                binding.checkFavorite.setChecked(false);
                productModel.setIs_favorite("no");

            } else {
                binding.checkFavorite.setChecked(true);
                productModel.setIs_favorite("yes");


            }
            Toast.makeText(this, getString(R.string.please_sign_in_or_sign_up), Toast.LENGTH_SHORT).show();

            return;
        }

        try {
            Api.getService(Tags.base_url)
                    .like_disliked("Bearer " + userModel.getData().getToken(), product_id)
                    .enqueue(new Callback<StatusResponse>() {
                        @Override
                        public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                            if (response.isSuccessful()) {
                                if (response.body().getStatus() == 200) {
                                    if (isChecked) {
                                        productModel.setIs_favorite("yes");
                                        binding.checkFavorite.setChecked(true);
                                        isDataChanged = true;

                                    } else {
                                        productModel.setIs_favorite("no");

                                        binding.checkFavorite.setChecked(false);

                                    }
                                } else {

                                    if (isChecked) {
                                        binding.checkFavorite.setChecked(false);
                                        productModel.setIs_favorite("no");


                                    } else {
                                        binding.checkFavorite.setChecked(true);
                                        productModel.setIs_favorite("yes");


                                    }

                                    //      Toast.makeText(ProductDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                }
                            } else {

                                if (isChecked) {
                                    binding.checkFavorite.setChecked(false);
                                    productModel.setIs_favorite("no");

                                } else {
                                    binding.checkFavorite.setChecked(true);
                                    productModel.setIs_favorite("yes");


                                }
                                if (response.code() == 500) {
                                    //              Toast.makeText(ProductDetailsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    //            Toast.makeText(ProductDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<StatusResponse> call, Throwable t) {
                            try {
                                if (isChecked) {
                                    binding.checkFavorite.setChecked(false);

                                } else {
                                    binding.checkFavorite.setChecked(true);

                                }
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
        } catch (Exception e) {
        }

    }

    public void addReport(boolean isReport) {

        if (userModel == null) {
            if (isReport) {
                productModel.setIs_report("yes");


            } else {
                productModel.setIs_report("no");

            }
            Toast.makeText(this, getString(R.string.please_sign_in_or_sign_up), Toast.LENGTH_SHORT).show();
            return;
        }
        /*try {
            Api.getService(Tags.base_url)
                    .report("Bearer " + userModel.getData().getToken(), product_id, "0")
                    .enqueue(new Callback<StatusResponse>() {
                        @Override
                        public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                            if (response.isSuccessful()) {
                                if (response.body().getStatus() == 200) {
                                    if (isReport) {
                                        productModel.setIs_report("no");
                                        binding.imgWarning.setColorFilter(ContextCompat.getColor(ProductDetailsActivity.this, R.color.gray4));


                                    } else {
                                        productModel.setIs_report("yes");
                                        binding.imgWarning.setColorFilter(ContextCompat.getColor(ProductDetailsActivity.this, R.color.colorPrimary));

                                    }
                                } else {

                                    if (isReport) {
                                        productModel.setIs_report("yes");


                                    } else {
                                        productModel.setIs_report("no");

                                    }

                                    Toast.makeText(ProductDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                }
                            } else {

                                if (isReport) {
                                    productModel.setIs_report("yes");
                                    binding.imgWarning.setColorFilter(ContextCompat.getColor(ProductDetailsActivity.this, R.color.colorPrimary));


                                } else {
                                    productModel.setIs_report("no");
                                    binding.imgWarning.setColorFilter(ContextCompat.getColor(ProductDetailsActivity.this, R.color.gray4));


                                }
                                if (response.code() == 500) {
                                    Toast.makeText(ProductDetailsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(ProductDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<StatusResponse> call, Throwable t) {
                            try {
                                if (isReport) {
                                    productModel.setIs_report("yes");
                                    binding.imgWarning.setColorFilter(ContextCompat.getColor(ProductDetailsActivity.this, R.color.colorPrimary));


                                } else {
                                    productModel.setIs_report("no");
                                    binding.imgWarning.setColorFilter(ContextCompat.getColor(ProductDetailsActivity.this, R.color.gray4));


                                }
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(ProductDetailsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ProductDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {
        }*/


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
//        if (googleMap!=null){
//            mMap = googleMap;
//            mMap.setBuildingsEnabled(false);
//            mMap.setTrafficEnabled(false);
//            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//            mMap.getUiSettings().setZoomControlsEnabled(false);
//            addMarker();
//        }
    }

    private void addMarker() {
        LatLng latLng = new LatLng(productModel.getLatitude(), productModel.getLongitude());
        mMap.addMarker(new MarkerOptions().title(productModel.getAddress()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).position(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.6f));
    }
}
