package com.fawaid_elbenaa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.fawaid_elbenaa.R;
import com.fawaid_elbenaa.activities_fragments.activity_home.fragments.Fragment_Department;
import com.fawaid_elbenaa.databinding.ChatRoomRowBinding;
import com.fawaid_elbenaa.models.RoomModel;

import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<RoomModel> list;
    private Context context;
    private LayoutInflater inflater;
    private Fragment fragment;

    public RoomAdapter(List<RoomModel> list, Context context, Fragment fragment) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ChatRoomRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.chat_room_row, parent, false);
        return new MyHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyHolder) {
            MyHolder myHolder = (MyHolder) holder;
            myHolder.binding.setModel(list.get(position));
            myHolder.itemView.setOnClickListener(view -> {
                if (fragment instanceof Fragment_Department) {

                    Fragment_Department fragment_department = (Fragment_Department) fragment;
                  //  fragment_news.setRoomDate(list.get(myHolder.getAdapterPosition()));
                }
            });



        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        public ChatRoomRowBinding binding;

        public MyHolder(@NonNull ChatRoomRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}
