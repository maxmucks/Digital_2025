package org.deafop.digital_library.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.deafop.digital_library.R;

public class SubjectFragment extends Fragment {

    private static final String ARG_SUBJECT_NAME = "subject_name";

    public static SubjectFragment newInstance(String subjectName) {
        SubjectFragment fragment = new SubjectFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SUBJECT_NAME, subjectName);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subject, container, false);
        TextView subjectTitle = view.findViewById(R.id.subject_title);
        if (getArguments() != null) {
            subjectTitle.setText(getArguments().getString(ARG_SUBJECT_NAME));
        }
        return view;
    }
}
