package org.deafop.digital_library.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.deafop.digital_library.R;
import org.deafop.digital_library.config.AppConfig;
import org.deafop.digital_library.models.Video;
import org.deafop.digital_library.utils.Constant;
import org.deafop.digital_library.utils.SharedPref;
import org.deafop.digital_library.utils.Tools;
import com.balysv.materialripple.MaterialRippleLayout;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.List;

public class AdapterFavorite extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;

    private List<Video> items;

    private Context context;
    private OnItemClickListener mOnItemClickListener;
    private OnItemClickListener mOnItemOverflowClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Video obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public void setOnItemOverflowClickListener(final OnItemClickListener mItemOverflowClickListener) {
        this.mOnItemOverflowClickListener = mItemOverflowClickListener;
    }

    public AdapterFavorite(Context context, RecyclerView view, List<Video> items) {
        this.items = items;
        this.context = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public TextView category_name;
        public TextView video_title;
        public TextView video_duration;
        public TextView total_views;
        public TextView date_time;
        public LinearLayout lyt_view;
        public LinearLayout lyt_date;
        public ImageView video_thumbnail;
        public MaterialRippleLayout lyt_parent;
        public ImageButton overflow;

        public OriginalViewHolder(View v) {
            super(v);
            category_name = v.findViewById(R.id.category_name);
            video_title = v.findViewById(R.id.video_title);
            video_duration = v.findViewById(R.id.video_duration);
            date_time = v.findViewById(R.id.date_time);
       //     total_views = v.findViewById(R.id.total_views);
            lyt_view = v.findViewById(R.id.lyt_view_count);
            lyt_date = v.findViewById(R.id.lyt_date);
            video_thumbnail = v.findViewById(R.id.video_thumbnail);
            lyt_parent = v.findViewById(R.id.lyt_parent);
            overflow = v.findViewById(R.id.overflow);
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        SharedPref sharedPref = new SharedPref(context);
        if (sharedPref.getVideoViewType() == Constant.VIDEO_LIST_COMPACT) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_compact, parent, false);
            vh = new OriginalViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_default, parent, false);
            vh = new OriginalViewHolder(v);
        }
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        final Video p = items.get(position);
        final OriginalViewHolder vItem = (OriginalViewHolder) holder;
        SharedPref sharedPref = new SharedPref(context);

        vItem.category_name.setText(p.category_name);
        vItem.video_title.setText(p.video_title);
        vItem.video_duration.setText(p.video_duration);

        if (AppConfig.ENABLE_VIEW_COUNT) {
           // vItem.total_views.setText(Tools.withSuffix(p.total_views) + " " + context.getResources().getString(R.string.views_count));
        } else {
            vItem.lyt_view.setVisibility(View.GONE);
        }

        if (AppConfig.ENABLE_DATE_DISPLAY && AppConfig.DISPLAY_DATE_AS_TIME_AGO) {
            PrettyTime prettyTime = new PrettyTime();
            long timeAgo = Tools.timeStringtoMilis(p.date_time);
            vItem.date_time.setText(prettyTime.format(new Date(timeAgo)));
        } else if (AppConfig.ENABLE_DATE_DISPLAY && !AppConfig.DISPLAY_DATE_AS_TIME_AGO) {
            vItem.date_time.setText(Tools.getFormatedDateSimple(p.date_time));
        } else {
            vItem.lyt_date.setVisibility(View.GONE);
        }

        if (p.video_type != null && p.video_type.equals("youtube")) {
            if (sharedPref.getVideoViewType() == Constant.VIDEO_LIST_COMPACT) {
                Picasso.get()
                        .load(Constant.YOUTUBE_IMAGE_FRONT + p.video_id + Constant.YOUTUBE_IMAGE_BACK_MQ)
                        .placeholder(R.drawable.ic_thumbnail)
                        .into(vItem.video_thumbnail);
            } else {
                Picasso.get()
                        .load(Constant.YOUTUBE_IMAGE_FRONT + p.video_id + Constant.YOUTUBE_IMAGE_BACK_HQ)
                        .placeholder(R.drawable.ic_thumbnail)
                        .into(vItem.video_thumbnail);
            }
        } else {
            Picasso.get()
                    .load(sharedPref.getApiUrl() + "/upload/" + p.video_thumbnail)
                    .placeholder(R.drawable.ic_thumbnail)
                    .into(vItem.video_thumbnail);
        }

        vItem.lyt_parent.setOnClickListener(view -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(view, p, position);
            }
        });

        vItem.overflow.setOnClickListener(view -> {
            if (mOnItemOverflowClickListener != null) {
                mOnItemOverflowClickListener.onItemClick(view, p, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_ITEM;
    }

}