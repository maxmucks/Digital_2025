package org.deafop.digital_library.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.deafop.digital_library.R;
import org.deafop.digital_library.adapters.CardAdapter;
import org.deafop.digital_library.models.LearningCard;

import java.util.ArrayList;
import java.util.List;

public class ActivityCards extends AppCompatActivity implements CardAdapter.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);

        RecyclerView recycler = findViewById(R.id.cardRecycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        List<LearningCard> cards = new ArrayList<>();

        cards.add(new LearningCard(
                "Academic Learning",
                Color.parseColor("#3F6EC7"),
                R.drawable.acessinno
        ));

        cards.add(new LearningCard(
                "Life & Employability Skills",
                Color.parseColor("#6C8E2F"),
                R.drawable.laptop_windows_24px
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

        recycler.setAdapter(new CardAdapter(cards, this));


    }

    @Override
    public void onItemClick(LearningCard card) {
        Toast.makeText(this, "Clicked: " + card.title, Toast.LENGTH_SHORT).show();
    }
}