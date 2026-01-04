package org.deafop.digital_library.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.deafop.digital_library.R;
import org.deafop.digital_library.config.AppConfig;
import org.deafop.digital_library.models.Category;
import org.deafop.digital_library.utils.SharedPref;
import com.squareup.picasso.Picasso;

import org.deafop.digital_library.utils.Constant;

import java.util.List;

public class AdapterCategory extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Category> items;

    private final Context context;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Category obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterCategory(Context context, List<Category> items) {
        this.items = items;
        this.context = context;
    }

    public static class OriginalViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView category_name;
        public TextView video_count;
        public ImageView category_image;
        public LinearLayout lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            category_name = v.findViewById(R.id.category_name);
            video_count = v.findViewById(R.id.video_count);
            category_image = v.findViewById(R.id.category_image);
            lyt_parent = v.findViewById(R.id.lyt_parent);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SharedPref sharedPref = new SharedPref(context);
        if (sharedPref.getCategoryViewType() == Constant.CATEGORY_LIST) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_list, parent, false);
            return new OriginalViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_grid, parent, false);
            return new OriginalViewHolder(v);
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final Category c = items.get(position);
        final OriginalViewHolder vItem = (OriginalViewHolder) holder;
        SharedPref sharedPref = new SharedPref(context);

        vItem.category_name.setText(c.category_name);

        if (AppConfig.ENABLE_VIDEO_COUNT_ON_CATEGORY) {
            vItem.video_count.setVisibility(View.VISIBLE);
            vItem.video_count.setText(context.getString(R.string.video_count_text, Integer.parseInt(c.video_count)));
        } else {
            vItem.video_count.setVisibility(View.GONE);
        }

        Picasso.get()
                .load(sharedPref.getApiUrl() + "/upload/category/" + c.category_image)
                .placeholder(R.drawable.ic_thumbnail)
                .into(vItem.category_image);

        vItem.lyt_parent.setOnClickListener(view -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(view, c, position);
            }
        });
    }

    public void setListData(List<Category> items) {
        int startPosition = this.items.size();
        this.items.addAll(items);
        notifyItemRangeInserted(startPosition, items.size());
    }

    public void resetListData() {
        int size = this.items.size();
        this.items.clear();
        notifyItemRangeRemoved(0, size);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }

}