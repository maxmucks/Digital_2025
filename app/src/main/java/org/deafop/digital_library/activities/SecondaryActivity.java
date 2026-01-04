package org.deafop.digital_library.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.deafop.digital_library.R;
import org.deafop.digital_library.adapters.FormGridAdapter;
import org.deafop.digital_library.models.FormModel;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SecondaryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<FormModel> formList = new ArrayList<>();

    String JSON_URL = "https://shule.deafopkenya.org/wp-content/uploads/2026/01/secondary.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondary);

        recyclerView = findViewById(R.id.recycler_forms);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        fetchForms();
    }

    private void fetchForms() {
        new Thread(() -> {
            try {
                URL url = new URL(JSON_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream())
                );

                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }

                JSONArray array = new JSONArray(json.toString());
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    formList.add(
                            new FormModel(
                                    obj.getInt("id"),
                                    obj.getString("name"),
                                    obj.getString("icon")
                            )
                    );
                }

                runOnUiThread(() ->
                        recyclerView.setAdapter(
                                new FormGridAdapter(this, formList)
                        )
                );

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
