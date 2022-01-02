package com.fawaid_elbenaa.activities_fragments.activity_filter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fawaid_elbenaa.R;
import com.fawaid_elbenaa.activities_fragments.activity_login.LoginActivity;
import com.fawaid_elbenaa.activities_fragments.activity_product_details.ProductDetailsActivity;
import com.fawaid_elbenaa.adapters.ProductAdapter;
import com.fawaid_elbenaa.adapters.SpinnerGovernorateAdapter;
import com.fawaid_elbenaa.adapters.TypeFilterAdapter;
import com.fawaid_elbenaa.databinding.ActivityFilterBinding;
import com.fawaid_elbenaa.databinding.ActivityMyAdsBinding;
import com.fawaid_elbenaa.language.Language;
import com.fawaid_elbenaa.models.GovernorateDataModel;
import com.fawaid_elbenaa.models.GovernorateModel;
import com.fawaid_elbenaa.models.ProductModel;
import com.fawaid_elbenaa.models.ProductsDataModel;
import com.fawaid_elbenaa.models.SingleCategoryModel;
import com.fawaid_elbenaa.models.TypeDataModel;
import com.fawaid_elbenaa.models.TypeModel;
import com.fawaid_elbenaa.models.UserModel;
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

public class FilterActivity extends AppCompatActivity {
    private ActivityFilterBinding binding;
    private Preferences preference;
    private UserModel userModel;
    private List<TypeModel> typeModelList;
    private TypeFilterAdapter typeFilterAdapter;
    private List<GovernorateModel> governorateModelList;
    private SpinnerGovernorateAdapter spinnerGovernorateAdapter;
    private ProductAdapter adapter;
    private List<ProductModel> productModelList;
    private Call<ProductsDataModel> call;

    private boolean isDataChanged = false;
    private String lang;
    private String type, goveid;
    private SingleCategoryModel singleCategoryModel;
    private String id;
    private String query;

    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_filter);
        getDatafromIntent();
        initView();
    }

    private void getDatafromIntent() {
        singleCategoryModel = (SingleCategoryModel) getIntent().getSerializableExtra("data");
    }

    private void initView() {
        productModelList = new ArrayList<>();
        governorateModelList = new ArrayList<>();
        typeModelList = new ArrayList<>();
        Paper.init(this);

        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        binding.setTitle(singleCategoryModel.getTitle());
        preference = Preferences.getInstance();
        userModel = preference.getUserData(this);
        binding.recView.setLayoutManager(new GridLayoutManager(this, 1));
        adapter = new ProductAdapter(productModelList, this, null);
        binding.recView.setAdapter(adapter);
        GovernorateModel governorateModel = new GovernorateModel(0, "إختر المحافظة", "Choose governorate");
        governorateModelList.add(governorateModel);

        spinnerGovernorateAdapter = new SpinnerGovernorateAdapter(governorateModelList, this);
        binding.spinnerGovernate.setAdapter(spinnerGovernorateAdapter);
        typeFilterAdapter = new TypeFilterAdapter(typeModelList, this);
        binding.recViewCity.setLayoutManager(new LinearLayoutManager(this));
        binding.recViewCity.setAdapter(typeFilterAdapter);
        binding.llBack.setOnClickListener(view -> onBackPressed());
        binding.llFilter.setOnClickListener(view -> openSheet());

        binding.imageCloseSpecialization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSheet();
            }
        });
        binding.spinnerGovernate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i == 0) {
                    goveid = null;
                } else {
                    goveid = governorateModelList.get(i).getId() + "";
                }
                getProducts();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.editQuery.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                query = binding.editQuery.getText().toString();
                if (!query.isEmpty()) {
                    productModelList.clear();
                    adapter.notifyDataSetChanged();
                    binding.progBar.setVisibility(View.VISIBLE);
                    getProducts();
                }
            }
            return false;
        });
        binding.editQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {
                    productModelList.clear();
                    adapter.notifyDataSetChanged();
                    binding.progBar.setVisibility(View.VISIBLE);
                    binding.tvNoData.setVisibility(View.GONE);
                    query = null;
                    getProducts();
                }
            }
        });
        getGovernorate();
        getProducts();
    }


    @Override
    public void onBackPressed() {

        finish();
    }

    private void openSheet() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        binding.flCitySheet.clearAnimation();
        binding.flCitySheet.startAnimation(animation);


        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                binding.flCitySheet.setVisibility(View.VISIBLE);
                getTypes(singleCategoryModel.getId());

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void closeSheet() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_down);

        binding.flCitySheet.clearAnimation();
        binding.flCitySheet.startAnimation(animation);


        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                binding.flCitySheet.setVisibility(View.GONE);
                getProducts();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    public void settype(int id) {
        type = id + "";
        closeSheet();
    }

    private void getTypes(int category_id) {

        try {
            binding.progBarCity.setVisibility(View.VISIBLE);
            binding.tvNoDataCity.setVisibility(View.GONE);
            Api.getService(Tags.base_url)
                    .getTypes(category_id)
                    .enqueue(new Callback<TypeDataModel>() {
                        @Override
                        public void onResponse(Call<TypeDataModel> call, Response<TypeDataModel> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                binding.progBarCity.setVisibility(View.GONE);
                                if (response.body().getStatus() == 200) {
                                    if (response.body().getData().size() > 0) {
                                        typeModelList.clear();

                                        typeModelList.addAll(response.body().getData());
                                        typeFilterAdapter.notifyDataSetChanged();
                                    } else {
                                        binding.tvNoDataCity.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                  //  Toast.makeText(FilterActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            } else {

                                if (response.code() == 500) {
                                    //Toast.makeText(FilterActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    //Toast.makeText(FilterActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<TypeDataModel> call, Throwable t) {
                            try {
                                binding.progBarCity.setVisibility(View.GONE);
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                      //  Toast.makeText(FilterActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        //Toast.makeText(FilterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }
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
                                  //  Toast.makeText(FilterActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            } else {

                                if (response.code() == 500) {
                                    //Toast.makeText(FilterActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    //Toast.makeText(FilterActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

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
                                      //  Toast.makeText(FilterActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        //Toast.makeText(FilterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }


    }

    private void getProducts() {
        binding.progBar.setVisibility(View.VISIBLE);
        productModelList.clear();
        adapter.notifyDataSetChanged();
        if (userModel != null) {
            id = userModel.getData().getId() + "";
        } else {
            id = null;
        }
        try {

            if (call != null) {
                call.cancel();
            }

            call = Api.getService(Tags.base_url)
                    .filterProducts(id, query, goveid, type, singleCategoryModel.getId() + "");
            call.enqueue(new Callback<ProductsDataModel>() {
                @Override
                public void onResponse(Call<ProductsDataModel> call, Response<ProductsDataModel> response) {
                    binding.progBar.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().getStatus() == 200) {
                            if (response.body().getData().size() > 0) {
                                productModelList.clear();
                                productModelList.addAll(response.body().getData());
                                adapter.notifyDataSetChanged();
                                binding.tvNoData.setVisibility(View.GONE);
                            } else {
                                binding.tvNoData.setVisibility(View.VISIBLE);

                            }
                        } else {
                          //  Toast.makeText(FilterActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        binding.progBar.setVisibility(View.GONE);

                        if (response.code() == 500) {
                            //Toast.makeText(FilterActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                        } else {
                            //Toast.makeText(FilterActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

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

                        if (t.getMessage() != null) {
                            Log.e("error", t.getMessage());
                            if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                              //  Toast.makeText(FilterActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                            } else if (t.getMessage().toLowerCase().contains("socket") || t.getMessage().toLowerCase().contains("canceled")) {

                            } else {
                                //Toast.makeText(FilterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (Exception e) {
                        Log.e("sllslls", e.toString());

                    }
                }
            });
        } catch (Exception e) {
            Log.e("sllslls", e.toString());
        }
    }

    public void setProductItemData(ProductModel productModel) {
        Intent intent = new Intent(this, ProductDetailsActivity.class);
        intent.putExtra("product_id", productModel.getId());
        startActivity(intent);
    }

    public void likeDislike(ProductModel model, int adapterPosition) {
        if (userModel != null) {
            Api.getService(Tags.base_url)
                    .likeDislike("Bearer " + userModel.getData().getToken(), model.getId())
                    .enqueue(new Callback<ProductsDataModel>() {
                        @Override
                        public void onResponse(Call<ProductsDataModel> call, Response<ProductsDataModel> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().getStatus() == 200) {
                                    if (model.getIs_favorite().equals("no")) {
                                        model.setIs_favorite("yes");
                                    } else {
                                        model.setIs_favorite("no");
                                    }


                                }

                                productModelList.set(adapterPosition, model);
                                adapter.notifyItemChanged(adapterPosition);

                            } else {
                                productModelList.set(adapterPosition, model);
                                adapter.notifyItemChanged(adapterPosition);


                                try {
                                    Log.e("error", response.code() + "_" + response.errorBody().string());

                                    if (response.code() == 500) {
                                  //      Toast.makeText(FilterActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                                    } else {
                                    //    Toast.makeText(FilterActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                            }
                        }

                        @Override
                        public void onFailure(Call<ProductsDataModel> call, Throwable t) {
                            try {
                                productModelList.set(adapterPosition, model);
                                adapter.notifyItemChanged(adapterPosition);
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage() + "__");

                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                      //  Toast.makeText(FilterActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                    } else {
                                        //Toast.makeText(FilterActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } catch (Exception e) {
                                Log.e("Error", e.getMessage() + "__");
                            }
                        }
                    });
        } else {
            productModelList.set(adapterPosition, model);
            adapter.notifyItemChanged(adapterPosition);
            navigateToSignInActivity();

        }


    }

    public void navigateToSignInActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finishAffinity();
    }
}