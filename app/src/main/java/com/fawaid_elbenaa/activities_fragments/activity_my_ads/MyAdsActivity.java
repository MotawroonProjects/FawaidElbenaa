package com.fawaid_elbenaa.activities_fragments.activity_my_ads;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.fawaid_elbenaa.R;
import com.fawaid_elbenaa.activities_fragments.activity_add_ads.AddAdsActivity;
import com.fawaid_elbenaa.activities_fragments.activity_product_details.ProductDetailsActivity;
import com.fawaid_elbenaa.adapters.MyProductAdapter;
import com.fawaid_elbenaa.databinding.ActivityMyAdsBinding;
import com.fawaid_elbenaa.language.Language;
import com.fawaid_elbenaa.models.ProductModel;
import com.fawaid_elbenaa.models.ProductsDataModel;
import com.fawaid_elbenaa.models.StatusResponse;
import com.fawaid_elbenaa.models.UserModel;
import com.fawaid_elbenaa.preferences.Preferences;
import com.fawaid_elbenaa.remote.Api;
import com.fawaid_elbenaa.share.Common;
import com.fawaid_elbenaa.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyAdsActivity extends AppCompatActivity {
    private ActivityMyAdsBinding binding;
    private Preferences preference;
    private UserModel userModel;
    private MyProductAdapter adapter;
    private List<ProductModel> productModelList;
    private boolean isDataChanged = false;
    private String lang;

    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_my_ads);
        initView();
    }

    private void initView() {
        productModelList = new ArrayList<>();
        Paper.init(this);
        lang = Paper.book().read("lang","ar");
        binding.setLang(lang);
        preference = Preferences.getInstance();
        userModel = preference.getUserData(this);
        binding.recView.setLayoutManager(new GridLayoutManager(this,2));
        adapter = new MyProductAdapter(productModelList,this);
        binding.recView.setAdapter(adapter);
        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        binding.swipeRefresh.setOnRefreshListener(this::getMyProducts);
        binding.llBack.setOnClickListener(view -> onBackPressed());

        getMyProducts();
    }

    private void getMyProducts()
    {

        try {

            Api.getService(Tags.base_url)
                    .getMyProducts("Bearer "+userModel.getData().getToken())
                    .enqueue(new Callback<ProductsDataModel>() {
                        @Override
                        public void onResponse(Call<ProductsDataModel> call, Response<ProductsDataModel> response) {
                            binding.progBar.setVisibility(View.GONE);
                            binding.swipeRefresh.setRefreshing(false);
                            if (response.isSuccessful() && response.body() != null ) {
                                if (response.body().getStatus()==200){
                                    if (response.body().getData().size()>0){
                                        productModelList.clear();
                                        productModelList.addAll(response.body().getData());
                                        adapter.notifyDataSetChanged();
                                        binding.tvNoData.setVisibility(View.GONE);
                                    }else {
                                        binding.tvNoData.setVisibility(View.VISIBLE);

                                    }
                                }else {
                                  //  Toast.makeText(MyAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                binding.progBar.setVisibility(View.GONE);
                                binding.swipeRefresh.setRefreshing(false);
                                if (response.code() == 500) {
                                   // Toast.makeText(MyAdsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    //Toast.makeText(MyAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ProductsDataModel> call, Throwable t) {
                            try {
                                binding.progBar.setVisibility(View.GONE);
                                binding.swipeRefresh.setRefreshing(false);

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                 //       Toast.makeText(MyAdsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                   //     Toast.makeText(MyAdsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }

    }


    public void setProductItemData(ProductModel productModel) {
        Intent intent = new Intent(this, ProductDetailsActivity.class);
        intent.putExtra("product_id",productModel.getId());
        startActivity(intent);
    }

    public void deleteAd(ProductModel productModel,int pos){

        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.show();

        Api.getService(Tags.base_url)
                .deleteProduct("Bearer "+userModel.getData().getToken(),productModel.getId())
                .enqueue(new Callback<StatusResponse>() {
                    @Override
                    public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            if (response.body()!=null&&response.body().getStatus()==200){
                                isDataChanged = true;
                                productModelList.remove(pos);
                                adapter.notifyItemRemoved(pos);
                                if (productModelList.size()>0){
                                    binding.tvNoData.setVisibility(View.GONE);
                                }else {
                                    binding.tvNoData.setVisibility(View.VISIBLE);

                                }
                            }else {
                            //    Toast.makeText(MyAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                            }
                        } else {
                            dialog.dismiss();
                            try {
                                Log.e("error", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                              //  Toast.makeText(MyAdsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else {
                                //Toast.makeText(MyAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
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
                         //           Toast.makeText(MyAdsActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                           //         Toast.makeText(MyAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (isDataChanged){
            setResult(RESULT_OK);
        }
        finish();
    }

    public void changeStatus(ProductModel productModel, int adapterPosition) {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.show();

        Api.getService(Tags.base_url)
                .changeAdStatus("Bearer "+userModel.getData().getToken(),productModel.getId()+"")
                .enqueue(new Callback<StatusResponse>() {
                    @Override
                    public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            if (response.body()!=null&&response.body().getStatus()==200){
                                if (productModel.getIs_active().equals("1")){
                                    productModel.setIs_active("0");
                                }else {
                                    productModel.setIs_active("1");

                                }
                                productModelList.set(adapterPosition,productModel);
                                adapter.notifyItemChanged(adapterPosition);
                            }else {

                            }
                        } else {
                            dialog.dismiss();
                            try {
                                Log.e("error", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                                //  Toast.makeText(MyAdsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else {
                                //Toast.makeText(MyAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
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
                                    //           Toast.makeText(MyAdsActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    //         Toast.makeText(MyAdsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
    }

    public void edit(ProductModel productModel, int adapterPosition) {
        Intent intent=new Intent(this, AddAdsActivity.class);
        intent.putExtra("data",productModel);
        startActivity(intent);

    }
}