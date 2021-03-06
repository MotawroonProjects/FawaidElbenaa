package com.fawaid_elbenaa.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;

import com.fawaid_elbenaa.R;
import com.fawaid_elbenaa.activities_fragments.activity_home.fragments.Fragment_Home;
import com.fawaid_elbenaa.databinding.SliderRowBinding;
import com.fawaid_elbenaa.models.SliderModel;
import com.fawaid_elbenaa.tags.Tags;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HomeSliderAdapter extends PagerAdapter {
    private List<SliderModel.Data> list;
    private Context context;
    private LayoutInflater inflater;
    private Fragment fragment;

    public HomeSliderAdapter(List<SliderModel.Data> list, Context context, Fragment fragment) {
        this.list = list;
        this.context = context;
        this.fragment = fragment;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        SliderRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.slider_row, container, false);
        Picasso.get().load(Uri.parse(Tags.IMAGE_URL + list.get(position).getImage())).into(binding.image);
        binding.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fragment instanceof Fragment_Home){
                    Fragment_Home fragment_home=(Fragment_Home)fragment;
                    fragment_home.openlink(list.get(position).getLink());

                }
            }
        });
        container.addView(binding.getRoot());
        return binding.getRoot();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
