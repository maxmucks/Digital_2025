package org.deafop.digital_library.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.deafop.digital_library.R;
import org.deafop.digital_library.activities.MainActivity;
import org.deafop.digital_library.adapters.SubjectAdapter;
import org.deafop.digital_library.models.Subject;

import java.util.ArrayList;
import java.util.List;

public class AcademicLearningFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_academic_learning, container, false);

        RecyclerView recycler = view.findViewById(R.id.subjectRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Subject> subjects = new ArrayList<>();
        subjects.add(new Subject("Form 4 Agriculture", R.drawable.ic_drawer_favorite));
        subjects.add(new Subject("Form 4 Biology", R.drawable.ic_drawer_favorite));
        subjects.add(new Subject("Form 4 Computer Studies", R.drawable.ic_drawer_favorite));
        subjects.add(new Subject("Form 4 KSL", R.drawable.ic_drawer_favorite));

        SubjectAdapter adapter = new SubjectAdapter(subjects, subject -> {
            // Handle click - Navigate to SubjectFragment with subject name
            navigateToSubjectFragment(subject.name);
        });

        recycler.setAdapter(adapter);
        return view;
    }

    private void navigateToSubjectFragment(String subjectName) {
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            Fragment subjectFragment = SubjectFragment.newInstance(subjectName);
            mainActivity.navigateToSubjectFragment(subjectFragment, subjectName);
        }
    }
}