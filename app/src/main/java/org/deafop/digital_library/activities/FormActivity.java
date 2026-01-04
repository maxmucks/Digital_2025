package org.deafop.digital_library.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.deafop.digital_library.R;
import org.deafop.digital_library.adapters.SubjectGridAdapter;
import org.deafop.digital_library.fragments.SubjectFragment;
import org.deafop.digital_library.models.SubjectModel;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FormActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<SubjectModel> subjectList = new ArrayList<>();
    int formId;
    String formName;

    String JSON_URL = "https://shule.deafopkenya.org/wp-content/uploads/2026/01/secondary.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        recyclerView = findViewById(R.id.recycler_subjects);

        // Get Form info from intent
        formId = getIntent().getIntExtra("FORM_ID", 0);
        formName = getIntent().getStringExtra("FORM_NAME");
        setTitle(formName);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        loadSubjects();
    }

    private void loadSubjects() {
        new Thread(() -> {
            try {
                URL url = new URL(JSON_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }

                JSONArray forms = new JSONArray(json.toString());

                for (int i = 0; i < forms.length(); i++) {
                    JSONObject form = forms.getJSONObject(i);
                    if (form.getInt("id") == formId) {
                        JSONArray subjects = form.getJSONArray("subjects");
                        for (int j = 0; j < subjects.length(); j++) {
                            JSONObject s = subjects.getJSONObject(j);
                            subjectList.add(
                                    new SubjectModel(
                                            s.getInt("id"),
                                            s.getString("name"),
                                            s.getString("icon")
                                    )
                            );
                        }
                        break;
                    }
                }

                runOnUiThread(() -> {
                    SubjectGridAdapter adapter = new SubjectGridAdapter(this, subjectList, subject -> {
                        // Subject click -> open fragment
                        openSubjectFragment(subject.getName());
                    });
                    recyclerView.setAdapter(adapter);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void openSubjectFragment(String subjectName) {
        // Hide RecyclerView
        recyclerView.setVisibility(RecyclerView.GONE);

        // Open SubjectFragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, SubjectFragment.newInstance(subjectName))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        // If fragment open, pop it first
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            recyclerView.setVisibility(RecyclerView.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }
}
