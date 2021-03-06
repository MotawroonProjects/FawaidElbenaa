package com.fawaid_elbenaa.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.fawaid_elbenaa.R;
import com.fawaid_elbenaa.databinding.LatestOfferRowBinding;
import com.fawaid_elbenaa.databinding.OfferRowBinding;
import com.fawaid_elbenaa.models.ProductModel;

import java.util.List;

public class OfferAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ProductModel> list;
    private Context context;
    private LayoutInflater inflater;
    private Fragment fragment;

    public OfferAdapter(List<ProductModel> list, Context context, Fragment fragment) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        OfferRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.offer_row, parent, false);
        return new MyHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyHolder) {
            MyHolder myHolder = (MyHolder) holder;
            myHolder.binding.setModel(list.get(position));
            myHolder.binding.tvOldPrice.setPaintFlags(myHolder.binding.tvOldPrice.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
            myHolder.itemView.setOnClickListener(view -> {

            });

            /*if (list.get(myHolder.getAdapterPosition()).getProduct_images().size() > 0) {
                Picasso.get().load(Uri.parse(Tags.IMAGE_URL + list.get(myHolder.getAdapterPosition()).getProduct_images().get(0).getImage())).fit().into(myHolder.binding.imageAds);

            }*/

        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        public OfferRowBinding binding;

        public MyHolder(@NonNull OfferRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}
