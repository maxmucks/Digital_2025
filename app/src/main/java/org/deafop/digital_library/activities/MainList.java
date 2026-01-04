package org.deafop.digital_library.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.deafop.digital_library.R;
import org.deafop.digital_library.adapters.MainTitleAdapter;
import org.deafop.digital_library.models.MainDataModel;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainList extends AppCompatActivity {

    public static RecyclerView recyclerView;
    public static ArrayList<MainDataModel> data;
    public static View.OnClickListener myOnClickListener;
    private MainTitleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<>();
        adapter = new MainTitleAdapter(data);
        recyclerView.setAdapter(adapter);

        myOnClickListener = new MyOnClickListener(this);

        fetchJsonData();
    }

    private void fetchJsonData() {

        String url = "https://shule.deafopkenya.org/wp-content/uploads/2026/01/data.json";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        data.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);

                            data.add(new MainDataModel(
                                    obj.getString("title"),
                                    obj.getInt("id"),
                                    obj.getString("image"),
                                    obj.getString("type")
                            ));
                        }
                        adapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }

    private static class MyOnClickListener implements View.OnClickListener {

        private final Context context;

        MyOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {

            int position = recyclerView.getChildAdapterPosition(v);
            String type = data.get(position).getType();
            Intent intent = null;

            switch (type) {
                case "ksl":
                 //   intent = new Intent(context, KSL1Activity.class);
                    break;
                case "primary":
                  //  intent = new Intent(context, PrimaryActivity.class);
                    break;
                case "secondary":
                    intent = new Intent(context, SecondaryActivity.class);
                    break;
                case "vocational":
                  //  intent = new Intent(context, VocationalActivity.class);
                    break;
                case "open":
                  //  intent = new Intent(context, Open.class);
                    break;
            }
            if (intent != null) context.startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", (d, i) -> finish())
                .setNegativeButton("No", (d, i) -> d.dismiss())
                .show();
    }
}
