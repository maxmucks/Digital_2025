package org.deafop.digital_library.adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.deafop.digital_library.R;
import org.deafop.digital_library.activities.MainList;
import org.deafop.digital_library.models.MainDataModel;

import java.util.ArrayList;

public class MainTitleAdapter extends RecyclerView.Adapter<MainTitleAdapter.MyViewHolder> {

    private final ArrayList<MainDataModel> dataSet;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_title);
            image = itemView.findViewById(R.id.item_image);
            itemView.setOnClickListener(MainList.myOnClickListener);
        }
    }

    public MainTitleAdapter(ArrayList<MainDataModel> data) {
        this.dataSet = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_title, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MainDataModel item = dataSet.get(position);
        holder.title.setText(item.getName());

        Glide.with(holder.image.getContext())
                .load(item.getImage())
                .placeholder(R.drawable.s21)
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
