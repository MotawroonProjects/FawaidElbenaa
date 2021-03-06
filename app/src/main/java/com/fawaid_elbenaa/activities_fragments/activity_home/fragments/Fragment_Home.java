package com.fawaid_elbenaa.activities_fragments.activity_home.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fawaid_elbenaa.R;
import com.fawaid_elbenaa.activities_fragments.activity_filter.FilterActivity;
import com.fawaid_elbenaa.activities_fragments.activity_home.HomeActivity;
import com.fawaid_elbenaa.activities_fragments.activity_product_details.ProductDetailsActivity;
import com.fawaid_elbenaa.activities_fragments.activity_web_view.WebViewActivity;
import com.fawaid_elbenaa.adapters.Category_Adapter;
import com.fawaid_elbenaa.adapters.HomeSliderAdapter;
import com.fawaid_elbenaa.adapters.ProductAdapter;
import com.fawaid_elbenaa.databinding.FragmentHomeBinding;
import com.fawaid_elbenaa.models.AllCatogryModel;
import com.fawaid_elbenaa.models.ProductModel;
import com.fawaid_elbenaa.models.ProductsDataModel;
import com.fawaid_elbenaa.models.SingleCategoryModel;
import com.fawaid_elbenaa.models.SliderModel;
import com.fawaid_elbenaa.models.UserModel;
import com.fawaid_elbenaa.preferences.Preferences;
import com.fawaid_elbenaa.remote.Api;
import com.fawaid_elbenaa.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Home extends Fragment {

    private HomeActivity activity;
    private FragmentHomeBinding binding;
    private Preferences preferences;
    private String lang;
    private UserModel userModel;
    private ProductAdapter productAdapter;
    private List<ProductModel> productModelList;
    private LinearLayoutManager manager;
    private Call<ProductsDataModel> call;
    private HomeSliderAdapter sliderAdapter;
    private List<SliderModel.Data> sliderModelList;
    private List<SingleCategoryModel> categoryList;
    private Category_Adapter categoryAdapter;
    private String id;
    private String query = "";
    private int current_page = 0, NUM_PAGES;

    public static Fragment_Home newInstance() {
        return new Fragment_Home();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        initView();
        change_slide_image();
        return binding.getRoot();
    }

    private void change_slide_image() {
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (current_page == NUM_PAGES) {
                    current_page = 0;
                }
                binding.pager.setCurrentItem(current_page++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 10000, 10000);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void initView() {
        categoryList = new ArrayList<>();
        activity = (HomeActivity) getActivity();
        productModelList = new ArrayList<>();
        sliderModelList = new ArrayList<>();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(activity);
        Paper.init(activity);
        lang = Paper.book().read("lang", "ar");
        manager = new LinearLayoutManager(activity);
        productAdapter = new ProductAdapter(productModelList, activity, this);
        categoryAdapter = new Category_Adapter(categoryList, activity, this);

        binding.recView.setLayoutManager(manager);
        binding.recView.setAdapter(productAdapter);
        binding.recViewCategory.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
        binding.recViewCategory.setAdapter(categoryAdapter);

        binding.edtSearch.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                query = binding.edtSearch.getText().toString();
                if (!query.isEmpty()) {
                    productModelList.clear();
                    productAdapter.notifyDataSetChanged();
                    binding.progBar.setVisibility(View.VISIBLE);
                    getProducts(query);
                }
            }
            return false;
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
                if (editable.toString().isEmpty()) {
                    productModelList.clear();
                    productAdapter.notifyDataSetChanged();
                    binding.progBar.setVisibility(View.VISIBLE);
                    binding.tvNoData.setVisibility(View.GONE);
                    getProducts(null);
                }
            }
        });
        getProducts(null);
        binding.setUrl("https://faweeth.store");
        binding.cardStore.setOnClickListener(view -> {
            String url = "https://faweeth.store";
            navigateToWebViewActivity(url);
        });
      /*  binding.swipeRefresh.setOnRefreshListener(() -> {
            String query = binding.edtSearch.getText().toString();
            if (query.isEmpty()) {
                query = null;
            }
            getProducts(query);

        });*/

        sliderAdapter = new HomeSliderAdapter(sliderModelList, activity, this);
        binding.tab.setupWithViewPager(binding.pager);
        binding.pager.setAdapter(sliderAdapter);

        getSliderData();
        getCategoryData();
    }

    private void getSliderData() {
        Api.getService(Tags.base_url)
                .getSlider()
                .enqueue(new Callback<SliderModel>() {
                    @Override
                    public void onResponse(Call<SliderModel> call, Response<SliderModel> response) {
                        binding.progBar1.setVisibility(View.GONE);
                        if (response.isSuccessful()) {

                            if (response.body() != null && response.body().getData() != null) {
                                if (response.body().getStatus() == 200) {
                                    if (response.body().getData().size() > 0) {
                                        updateSliderUi(response.body().getData());

                                    } else {
                                        binding.flPager.setVisibility(View.GONE);

                                    }
                                } else {
                                    binding.flPager.setVisibility(View.GONE);
                                }
                             /*   if (response.body().getData().size() > 0) {
                                    updateSliderUi(response.body().getData());

                                } else {
                                    binding.flPager.setVisibility(View.GONE);

                                }*/
                            }


                        } else {
                            binding.progBar1.setVisibility(View.GONE);

                            switch (response.code()) {
                                case 500:
                                    // Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();
                                    break;

                                default:
                                    //Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                    break;
                            }
                            try {
                                Log.e("error_code", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                    }

                    @Override
                    public void onFailure(Call<SliderModel> call, Throwable t) {
                        try {
                            binding.progBar1.setVisibility(View.GONE);
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    //   Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else if (t.getMessage().toLowerCase().contains("socket") || t.getMessage().toLowerCase().contains("canceled")) {
                                } else {
                                    //  Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {

                        }
                    }
                });
    }

    private void updateSliderUi(List<SliderModel.Data> data) {
        NUM_PAGES = data.size();

        sliderModelList.addAll(data);
        sliderAdapter.notifyDataSetChanged();
    }

    private void getProducts(String query) {
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
                    .getProducts(id, query);
            call.enqueue(new Callback<ProductsDataModel>() {
                @Override
                public void onResponse(Call<ProductsDataModel> call, Response<ProductsDataModel> response) {
                    binding.progBar.setVisibility(View.GONE);
                    //binding.swipeRefresh.setRefreshing(false);
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().getStatus() == 200) {
                            if (response.body().getData().size() > 0) {
                                binding.llnew.setVisibility(View.VISIBLE);
                                productModelList.clear();
                                productModelList.addAll(response.body().getData());
                                productAdapter.notifyDataSetChanged();
                                binding.tvNoData.setVisibility(View.GONE);
                            } else {
                                binding.llnew.setVisibility(View.GONE);
                                binding.tvNoData.setVisibility(View.VISIBLE);

                            }
                        } else {
                            //    Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        binding.progBar.setVisibility(View.GONE);
                        //binding.swipeRefresh.setRefreshing(false);

                        if (response.code() == 500) {
                            // Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();


                        } else {
                            //Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();

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
                        //binding.swipeRefresh.setRefreshing(false);

                        if (t.getMessage() != null) {
                            Log.e("error", t.getMessage());
                            if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                //   Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                            } else if (t.getMessage().toLowerCase().contains("socket") || t.getMessage().toLowerCase().contains("canceled")) {

                            } else {
                                //   Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void getCategoryData() {
        // binding.cardView.setVisibility(View.VISIBLE);

        binding.progBarCategory.setVisibility(View.VISIBLE);

        Api.getService(Tags.base_url).getcategories().enqueue(new Callback<AllCatogryModel>() {
            @Override
            public void onResponse(Call<AllCatogryModel> call, Response<AllCatogryModel> response) {
                binding.progBarCategory.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {

                    if (response.body().getStatus() == 200) {
                        Log.e("dlldldlld", response.code() + "dldlldldld");

                        if (response.body().getData().size() > 0) {
                            //   binding.cardView.setVisibility(View.VISIBLE);

                            categoryList.clear();
                            categoryList.addAll(response.body().getData());
                            categoryAdapter.notifyDataSetChanged();
                        } else {
                            binding.cardView.setVisibility(View.GONE);
                        }


                    } else {
                        binding.cardView.setVisibility(View.GONE);
                    }


                } else {
                    try {

                        Log.e("errorssssss", response.code() + "_" + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (response.code() == 500) {
                        // Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();


                    } else {
                        // Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();


                    }
                }


            }

            @Override
            public void onFailure(Call<AllCatogryModel> call, Throwable t) {
                binding.progBarCategory.setVisibility(View.GONE);
                try {
                    binding.progBarCategory.setVisibility(View.GONE);
                    if (t.getMessage() != null) {
                        Log.e("error", t.getMessage());
                        if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                            //   Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                        } else {
                            // Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("error:", t.getMessage());
                        }
                    }

                } catch (Exception e) {
                }

            }
        });


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }


    public void setProductItemData(ProductModel productModel) {
        Intent intent = new Intent(activity, ProductDetailsActivity.class);
        intent.putExtra("product_id", productModel.getId());
        startActivity(intent);
    }

    public void search(String query) {
        productModelList.clear();
        productAdapter.notifyDataSetChanged();
        //binding.swipeRefresh.setRefreshing(false);
        binding.progBar.setVisibility(View.VISIBLE);
        binding.tvNoData.setVisibility(View.GONE);
        if (query.isEmpty()) {
            this.query = null;
            getProducts(null);
        } else {
            this.query = query;
            getProducts(this.query);
        }
    }

    public void showdata(SingleCategoryModel singleCategoryModel) {
        Intent intent = new Intent(activity, FilterActivity.class);
        intent.putExtra("data", singleCategoryModel);
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
                                productAdapter.notifyItemChanged(adapterPosition);

                            } else {
                                productModelList.set(adapterPosition, model);
                                productAdapter.notifyItemChanged(adapterPosition);


                                try {
                                    Log.e("error", response.code() + "_" + response.errorBody().string());

                                    if (response.code() == 500) {
                                        //       Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();
                                    } else {
                                        //     Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
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
                                productAdapter.notifyItemChanged(adapterPosition);
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage() + "__");

                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        //       Toast.makeText(activity, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                    } else {
                                        //     Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } catch (Exception e) {
                                Log.e("Error", e.getMessage() + "__");
                            }
                        }
                    });
        } else {
            productModelList.set(adapterPosition, model);
            productAdapter.notifyItemChanged(adapterPosition);
            activity.navigateToSignInActivity();
        }


    }

    private void navigateToWebViewActivity(String url) {
        Intent intent = new Intent(activity, WebViewActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    public void openlink(String link) {
        if (link != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            startActivity(intent);
        }
    }

    public void search() {
        search("");
    }
}
