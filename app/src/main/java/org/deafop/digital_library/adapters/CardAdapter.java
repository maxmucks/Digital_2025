package org.deafop.digital_library.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.deafop.digital_library.R;
import org.deafop.digital_library.models.LearningCard;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private final List<LearningCard> cards;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(LearningCard card);
    }

    public CardAdapter(List<LearningCard> cards, OnItemClickListener listener) {
        this.cards = cards;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_learning_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        LearningCard card = cards.get(position);

        h.title.setText(card.title);
        h.icon.setImageResource(card.icon);
        h.header.setBackgroundColor(card.color);

        h.itemView.setOnClickListener(v -> listener.onItemClick(cards.get(h.getAdapterPosition())));
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView icon;
        LinearLayout header;

        ViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.title);
            icon = v.findViewById(R.id.icon);
            header = v.findViewById(R.id.header);
        }
    }
}
