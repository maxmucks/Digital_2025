package org.deafop.digital_library.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.deafop.digital_library.R;
import org.deafop.digital_library.activities.FormActivity;
import org.deafop.digital_library.models.FormModel;

import java.util.List;

public class FormGridAdapter extends RecyclerView.Adapter<FormGridAdapter.ViewHolder> {

    Context context;
    List<FormModel> forms;

    public FormGridAdapter(Context context, List<FormModel> forms) {
        this.context = context;
        this.forms = forms;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_form_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FormModel form = forms.get(position);

        holder.name.setText(form.getName());

        int iconRes = context.getResources()
                .getIdentifier(form.getIcon(), "drawable", context.getPackageName());
        holder.icon.setImageResource(iconRes);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FormActivity.class);
            intent.putExtra("FORM_NAME", form.getName());
            intent.putExtra("FORM_ID", form.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return forms.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name;

        ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.form_icon);
            name = itemView.findViewById(R.id.form_name);
        }
    }
}
