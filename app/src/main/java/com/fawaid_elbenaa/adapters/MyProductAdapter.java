package com.fawaid_elbenaa.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.fawaid_elbenaa.R;
import com.fawaid_elbenaa.activities_fragments.activity_my_ads.MyAdsActivity;
import com.fawaid_elbenaa.databinding.MyProductRowBinding;
import com.fawaid_elbenaa.databinding.ProductRow2Binding;
import com.fawaid_elbenaa.databinding.ProductRowBinding;
import com.fawaid_elbenaa.generated.callback.OnClickListener;
import com.fawaid_elbenaa.models.ProductModel;

import java.util.List;

public class MyProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ProductModel> list;
    private Context context;
    private LayoutInflater inflater;
    private AppCompatActivity activity;

    public MyProductAdapter(List<ProductModel> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        activity = (AppCompatActivity) context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MyProductRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.my_product_row, parent, false);
        return new MyHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyHolder) {
            MyHolder myHolder = (MyHolder) holder;
            myHolder.binding.setModel(list.get(position));
            myHolder.binding.tvOldPrice.setPaintFlags(myHolder.binding.tvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            myHolder.itemView.setOnClickListener(view -> {
                MyAdsActivity myAdsActivity = (MyAdsActivity) activity;
                myAdsActivity.setProductItemData(list.get(myHolder.getAdapterPosition()));

            });

            myHolder.binding.imageDelete.setOnClickListener(view -> {
                MyAdsActivity myAdsActivity = (MyAdsActivity) activity;
                myAdsActivity.deleteAd(list.get(myHolder.getAdapterPosition()), myHolder.getAdapterPosition());

            });

            myHolder.binding.tvAction.setOnClickListener(view -> {
                MyAdsActivity myAdsActivity = (MyAdsActivity) activity;
                myAdsActivity.changeStatus(list.get(myHolder.getAdapterPosition()),myHolder.getAdapterPosition());
            });
            myHolder.binding.tvEdit.setOnClickListener(view -> {
                MyAdsActivity myAdsActivity = (MyAdsActivity) activity;
                myAdsActivity.edit(list.get(myHolder.getAdapterPosition()),myHolder.getAdapterPosition());
            });

        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        public MyProductRowBinding binding;

        public MyHolder(@NonNull MyProductRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}
