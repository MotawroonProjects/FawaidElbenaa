package com.fawaid_elbenaa.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.fawaid_elbenaa.R;
import com.fawaid_elbenaa.activities_fragments.activity_google.activity_shop_query.ShopsQueryActivity;
import com.fawaid_elbenaa.databinding.CouponRowBinding;
import com.fawaid_elbenaa.databinding.PlaceRowBinding;
import com.fawaid_elbenaa.models.PlaceModel;
import com.fawaid_elbenaa.tags.Tags;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<PlaceModel> list;
    private Context context;
    private LayoutInflater inflater;
    private ShopsQueryActivity activity;

    public PlaceAdapter(List<PlaceModel> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        activity = (ShopsQueryActivity) context;



    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PlaceRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.place_row, parent, false);
        return new MyHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyHolder) {
            MyHolder myHolder = (MyHolder) holder;
            myHolder.binding.setModel(list.get(position));
            if (list.get(position).getImages().size()>0){
                String url = Tags.IMAGE_URL + list.get(position).getImages().get(0).getImage();
                Picasso.get().load(Uri.parse(url)).placeholder(R.drawable.logo2).into(myHolder.binding.image);

            }else {
                Picasso.get().load(R.drawable.logo2).into(myHolder.binding.image);
            }

            myHolder.itemView.setOnClickListener(v -> {
                activity.setShopData(list.get(myHolder.getAdapterPosition()));
            });



        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        public PlaceRowBinding binding;

        public MyHolder(@NonNull PlaceRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}
