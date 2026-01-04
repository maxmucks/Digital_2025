package org.deafop.digital_library.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.deafop.digital_library.R;

public class FormFragment extends Fragment {

    public static FormFragment newInstance(int id, String name) {
        FormFragment fragment = new FormFragment();
        Bundle args = new Bundle();
        args.putInt("FORM_ID", id);
        args.putString("FORM_NAME", name);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_form, container, false);

        TextView title = view.findViewById(R.id.form_title);
        title.setText(getArguments().getString("FORM_NAME"));

        return view;
    }
}
