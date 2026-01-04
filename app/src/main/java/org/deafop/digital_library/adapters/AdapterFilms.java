package org.deafop.digital_library.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.RecyclerView;

import org.deafop.digital_library.R;
import org.deafop.digital_library.activities.ActivityYoutubePlayer;
import org.deafop.digital_library.models.Films;
import org.deafop.digital_library.utils.Constant;

import java.util.ArrayList;
import java.util.List;

public class AdapterFilms extends RecyclerView.Adapter<AdapterFilms.MyViewHolder> {
    private Context context;
    private List<Films> items;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Films films, int i);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public AdapterFilms(Context context2, List<Films> list) {
        this.items = list;
        this.context = context2;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewIcon;
        TextView textViewName;

        public MyViewHolder(View view) {
            super(view);
            this.textViewName = (TextView) view.findViewById(R.id.textViewName);
            this.imageViewIcon = (ImageView) view.findViewById(R.id.imageView);
        }
    }

    public AdapterFilms(ArrayList<Films> arrayList) {
        this.items = arrayList;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.films_layout, viewGroup, false));
    }

    public void onBindViewHolder(final MyViewHolder myViewHolder, int i) {
        TextView textView = myViewHolder.textViewName;
        ImageView imageView = myViewHolder.imageViewIcon;
        textView.setText(this.items.get(i).getName());
        imageView.setImageResource(this.items.get(i).getImage());
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                myViewHolder.getAdapterPosition();
                if (myViewHolder.getAdapterPosition() == 0) {
                    Toast.makeText(view.getContext(), "Episode 1", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(view.getContext(), ActivityYoutubePlayer.class);
                    intent.putExtra(Constant.KEY_VIDEO_ID, "Y24kAnzyJRM");
                    view.getContext().startActivity(intent);
                } else if (myViewHolder.getAdapterPosition() == 1) {
                    Toast.makeText(view.getContext(), "Episode 2", Toast.LENGTH_LONG).show();
                    Intent intent2 = new Intent(view.getContext(), ActivityYoutubePlayer.class);
                    intent2.putExtra(Constant.KEY_VIDEO_ID, "-E--4zxg_3o");
                    view.getContext().startActivity(intent2);
                } else if (myViewHolder.getAdapterPosition() == 2) {
                    Toast.makeText(view.getContext(), "Episode 3", Toast.LENGTH_LONG).show();
                    Intent intent3 = new Intent(view.getContext(), ActivityYoutubePlayer.class);
                    intent3.putExtra(Constant.KEY_VIDEO_ID, "24mO8TpauH8");
                    view.getContext().startActivity(intent3);
                } else if (myViewHolder.getAdapterPosition() == 3) {
                    Toast.makeText(view.getContext(), "Episode 4", Toast.LENGTH_LONG).show();
                    Intent intent4 = new Intent(view.getContext(), ActivityYoutubePlayer.class);
                    intent4.putExtra(Constant.KEY_VIDEO_ID, "zxvwYxq8DdU");
                    view.getContext().startActivity(intent4);
                } else if (myViewHolder.getAdapterPosition() == 4) {
                    Toast.makeText(view.getContext(), "Episode 5", Toast.LENGTH_LONG).show();
                    Intent intent5 = new Intent(view.getContext(), ActivityYoutubePlayer.class);
                    intent5.putExtra(Constant.KEY_VIDEO_ID, "_3QXRVB2Jnc");
                    view.getContext().startActivity(intent5);
                } else if (myViewHolder.getAdapterPosition() == 5) {
                    Toast.makeText(view.getContext(), "Episode 6", Toast.LENGTH_LONG).show();
                    Intent intent6 = new Intent(view.getContext(), ActivityYoutubePlayer.class);
                    intent6.putExtra(Constant.KEY_VIDEO_ID, "v1SA_PmPCgw");
                    view.getContext().startActivity(intent6);
                } else if (myViewHolder.getAdapterPosition() == 6) {
                    Toast.makeText(view.getContext(), "Episode 7", Toast.LENGTH_LONG).show();
                    Intent intent7 = new Intent(view.getContext(), ActivityYoutubePlayer.class);
                    intent7.putExtra(Constant.KEY_VIDEO_ID, "ZLDMtGRx6wQ");
                    view.getContext().startActivity(intent7);
                } else if (myViewHolder.getAdapterPosition() == 7) {
                    Toast.makeText(view.getContext(), "Episode 8", Toast.LENGTH_LONG).show();
                    Intent intent8 = new Intent(view.getContext(), ActivityYoutubePlayer.class);
                    intent8.putExtra(Constant.KEY_VIDEO_ID, "5ZRgaRbeUIk");
                    view.getContext().startActivity(intent8);
                } else if (myViewHolder.getAdapterPosition() == 8) {
                    Intent intent9 = new Intent(view.getContext(), ActivityYoutubePlayer.class);
                    intent9.putExtra(Constant.KEY_VIDEO_ID, "kbMU2SONw28");
                    view.getContext().startActivity(intent9);
                } else if (myViewHolder.getAdapterPosition() == 9) {
                    Intent intent10 = new Intent(view.getContext(), ActivityYoutubePlayer.class);
                    intent10.putExtra(Constant.KEY_VIDEO_ID, "XhMZ6v_IaeU");
                    view.getContext().startActivity(intent10);
                } else if (myViewHolder.getAdapterPosition() == 10) {
                    Intent intent11 = new Intent(view.getContext(), ActivityYoutubePlayer.class);
                    intent11.putExtra(Constant.KEY_VIDEO_ID, "BN0-8Ly99oo");
                    view.getContext().startActivity(intent11);
                } else if (myViewHolder.getAdapterPosition() == 11) {
                    Intent intent12 = new Intent(view.getContext(), ActivityYoutubePlayer.class);
                    intent12.putExtra(Constant.KEY_VIDEO_ID, "H123S-5q2cQ");
                    view.getContext().startActivity(intent12);
                } else if (myViewHolder.getAdapterPosition() == 12) {
                    Intent intent13 = new Intent(view.getContext(), ActivityYoutubePlayer.class);
                    intent13.putExtra(Constant.KEY_VIDEO_ID, "5iaDQ1s-85Y");
                    view.getContext().startActivity(intent13);
                } else if (myViewHolder.getAdapterPosition() == 13) {
                    Intent intent14 = new Intent(view.getContext(), ActivityYoutubePlayer.class);
                    intent14.putExtra(Constant.KEY_VIDEO_ID, "yxgv1lA4Mf0");
                    view.getContext().startActivity(intent14);
                } else if (myViewHolder.getAdapterPosition() == 14) {
                    Toast.makeText(view.getContext(), "Silent Cry", Toast.LENGTH_LONG).show();
                    Intent intent15 = new Intent(view.getContext(), ActivityYoutubePlayer.class);
                    intent15.putExtra(Constant.KEY_VIDEO_ID, "jnOoblL8O1M");
                    view.getContext().startActivity(intent15);
                } else if (myViewHolder.getAdapterPosition() == 15) {
                    Toast.makeText(view.getContext(), "Poem", Toast.LENGTH_LONG).show();
                    Intent intent16 = new Intent(view.getContext(), ActivityYoutubePlayer.class);
                    intent16.putExtra(Constant.KEY_VIDEO_ID, "c9nDWr8lMLw");
                    view.getContext().startActivity(intent16);
                }
            }
        });
    }

    public int getItemCount() {
        return this.items.size();
    }
}
