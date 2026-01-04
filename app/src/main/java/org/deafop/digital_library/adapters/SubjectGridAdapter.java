package org.deafop.digital_library.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.deafop.digital_library.R;
import org.deafop.digital_library.models.SubjectModel;

import java.util.List;

public class SubjectGridAdapter extends RecyclerView.Adapter<SubjectGridAdapter.ViewHolder> {

    public interface OnSubjectClickListener {
        void onSubjectClick(SubjectModel subject);
    }

    private Context context;
    private List<SubjectModel> subjects;
    private OnSubjectClickListener listener;

    public SubjectGridAdapter(Context context,
                              List<SubjectModel> subjects,
                              OnSubjectClickListener listener) {
        this.context = context;
        this.subjects = subjects;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_subject_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SubjectModel subject = subjects.get(position);

        holder.name.setText(subject.getName());

        int iconRes = context.getResources()
                .getIdentifier(subject.getIcon(), "drawable", context.getPackageName());
        if (iconRes != 0) {
            holder.icon.setImageResource(iconRes);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSubjectClick(subject);
            }
        });
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.subject_icon);
            name = itemView.findViewById(R.id.subject_name);
        }
    }
}
