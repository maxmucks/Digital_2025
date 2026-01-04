package org.deafop.digital_library.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.deafop.digital_library.R;

public class SubjectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);

        String subjectName = getIntent().getStringExtra("SUBJECT_NAME");
        setTitle(subjectName);

        TextView title = findViewById(R.id.subject_title);
        title.setText(subjectName);
    }
}
