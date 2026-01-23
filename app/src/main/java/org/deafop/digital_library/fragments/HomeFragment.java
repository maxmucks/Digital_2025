package org.deafop.digital_library.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.deafop.digital_library.R;
import org.deafop.digital_library.activities.AcademicLearningActivity;
import org.deafop.digital_library.activities.LifeEmployabilityActivity;
import org.deafop.digital_library.activities.LanguageCommunicationActivity;
import org.deafop.digital_library.activities.CseLearningActivity;
import org.deafop.digital_library.adapters.CardAdapter;
import org.deafop.digital_library.models.LearningCard;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements CardAdapter.OnItemClickListener {

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView recycler = view.findViewById(R.id.cardRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setHasFixedSize(true);

        List<LearningCard> cards = new ArrayList<>();

        cards.add(new LearningCard(
                "Academic Learning",
                Color.parseColor("#3F6EC7"),
                R.drawable.academic_24px
        ));

        cards.add(new LearningCard(
                "Life & Employability Skills",
                Color.parseColor("#6C8E2F"),
                R.drawable.pastry_24px
        ));

        cards.add(new LearningCard(
                "Language & Communication",
                Color.parseColor("#2E8BC0"),
                R.drawable.hand_gesture_24px
        ));

        cards.add(new LearningCard(
                "CSE Learning Media",
                Color.parseColor("#F28C28"),
                R.drawable.films_24px
        ));

        CardAdapter adapter = new CardAdapter(cards, this);
        recycler.setAdapter(adapter);

        return view;
    }

    @Override
    public void onItemClick(LearningCard card) {
        Intent intent;

        // Use the getter method
        switch (card.getTitle()) {
            case "Academic Learning":
                intent = new Intent(getActivity(), AcademicLearningActivity.class);
                break;

            case "Life & Employability Skills":
                intent = new Intent(getActivity(), LifeEmployabilityActivity.class);
                break;

            case "Language & Communication":
                intent = new Intent(getActivity(), LanguageCommunicationActivity.class);
                break;

            case "CSE Learning Media":
                intent = new Intent(getActivity(), CseLearningActivity.class);
                break;

            default:
                intent = new Intent(getActivity(), AcademicLearningActivity.class);
                break;
        }

        startActivity(intent);
    }
}