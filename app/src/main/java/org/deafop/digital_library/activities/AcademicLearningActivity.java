package org.deafop.digital_library.activities;

import static org.deafop.digital_library.utils.Constant.CATEGORY_GRID_2_COLUMN;
import static org.deafop.digital_library.utils.Constant.CATEGORY_GRID_3_COLUMN;
import static org.deafop.digital_library.utils.Constant.CATEGORY_LIST;
import static org.deafop.digital_library.utils.Constant.DELAY_TIME;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;

import org.deafop.digital_library.R;
import org.deafop.digital_library.adapters.CardAdapter;
import org.deafop.digital_library.callbacks.CallbackCategories;
import org.deafop.digital_library.config.AppConfig;
import org.deafop.digital_library.models.Category;
import org.deafop.digital_library.models.LearningCard;
import org.deafop.digital_library.rests.ApiInterface;
import org.deafop.digital_library.rests.RestAdapter;
import org.deafop.digital_library.utils.Constant;
import org.deafop.digital_library.utils.SharedPref;
import org.deafop.digital_library.utils.Tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AcademicLearningActivity extends AppCompatActivity implements CardAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Call<CallbackCategories> callbackCall = null;
    private ShimmerFrameLayout lytShimmer;
    private SharedPref sharedPref;
    private View rootView;
    private CardAdapter cardAdapter;
    private List<LearningCard> learningCards;

    // Store mapping of subject names to Category objects
    private Map<String, Category> subjectCategoryMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_base);

        rootView = findViewById(android.R.id.content);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Form 4 Academic Subjects");
        }

        // Set description
        TextView tvDescription = findViewById(R.id.tv_description);
        if (tvDescription != null) {
            tvDescription.setText("Access comprehensive Form 4 curriculum materials.");
        }

        // Initialize SharedPref
        sharedPref = new SharedPref(this);

        // Setup RecyclerView
        recyclerView = findViewById(R.id.rv_cards);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
       // recyclerView.setHasFixedSize(true);

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout_category);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        // Initialize Shimmer
        lytShimmer = findViewById(R.id.shimmer_view_container);

        // Initialize the exact 4 subjects from original file
        learningCards = new ArrayList<>();

        // Add Form 4 subject cards - EXACTLY THE 4 FROM ORIGINAL FILE
        learningCards.add(new LearningCard(
                "Form 4 Agriculture",
                Color.parseColor("#4CAF50"),
                R.drawable.ic_agriculture,
                "Crop production, livestock management, and agricultural economics"
        ));

        learningCards.add(new LearningCard(
                "Form 4 Biology",
                Color.parseColor("#2196F3"),
                R.drawable.ic_biology,
                "Genetics, ecology, human physiology, and cell biology"
        ));

        learningCards.add(new LearningCard(
                "Form 4 Computer Studies",
                Color.parseColor("#9C27B0"),
                R.drawable.ic_computer,
                "Programming, databases, networking, and computer applications"
        ));

        learningCards.add(new LearningCard(
                "Form 4 KSL",
                Color.parseColor("#FF9800"),
                R.drawable.ic_sign_language,
                "Kenyan Sign Language - Advanced communication and interpretation"
        ));

        // Create adapter with the 4 subjects
        cardAdapter = new CardAdapter(learningCards, this);
        recyclerView.setAdapter(cardAdapter);

        // Set up swipe refresh listener
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Refresh data
            requestAction();
        });

        // Initial data load - this will fetch categories from API
        requestAction();

        // Initialize shimmer layout
        initShimmerLayout();
    }

    private void requestAction() {
        showFailedView(false, "");
        swipeProgress(true);
        showNoItemView(false);

        // Show the 4 cards immediately (no delay for local data)
        new Handler(Looper.getMainLooper()).postDelayed(
                this::requestCategoriesApi,
                DELAY_TIME
        );
    }

    private void requestCategoriesApi() {
        ApiInterface apiInterface = RestAdapter.createAPI(sharedPref.getApiUrl());
        callbackCall = apiInterface.getAllCategories(AppConfig.REST_API_KEY);

        callbackCall.enqueue(new Callback<CallbackCategories>() {
            @Override
            public void onResponse(Call<CallbackCategories> call, Response<CallbackCategories> response) {
                CallbackCategories resp = response.body();
                if (resp != null && resp.status.equals("ok")) {
                    // Process API categories but keep our 4 subjects
                    processApiCategories(resp.categories);
                    swipeProgress(false);
                } else {
                    onFailRequest();
                }
            }

            @Override
            public void onFailure(Call<CallbackCategories> call, Throwable t) {
                if (!call.isCanceled()) {
                    // Even if API fails, we still show the 4 subjects
                    swipeProgress(false);
                    Toast.makeText(AcademicLearningActivity.this,
                            "Using offline content", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void processApiCategories(List<Category> categories) {
        // Clear existing mapping
        subjectCategoryMap.clear();

        // Try to find matching categories in API for our 4 subjects
        for (Category category : categories) {
            if (category.category_name != null) {
                String categoryName = category.category_name.toLowerCase();
                String categoryOriginalName = category.category_name;

                // Check for agriculture - look for Form 4 Agriculture specifically
                if (categoryName.contains("form 4") && categoryName.contains("agriculture")) {
                    subjectCategoryMap.put("Form 4 Agriculture", category);
                } else if (categoryName.contains("agriculture") && !categoryName.contains("form")) {
                    // If there's a general agriculture category, use it
                    subjectCategoryMap.put("Form 4 Agriculture", category);
                }

                // Check for biology
                if (categoryName.contains("form 4") && categoryName.contains("biology")) {
                    subjectCategoryMap.put("Form 4 Biology", category);
                } else if (categoryName.contains("biology") && !categoryName.contains("form")) {
                    subjectCategoryMap.put("Form 4 Biology", category);
                }

                // Check for computer studies
                if ((categoryName.contains("form 4") && categoryName.contains("computer")) ||
                        (categoryName.contains("form 4") && categoryName.contains("ict"))) {
                    subjectCategoryMap.put("Form 4 Computer Studies", category);
                } else if ((categoryName.contains("computer") || categoryName.contains("ict") ||
                        categoryName.contains("information technology")) && !categoryName.contains("form")) {
                    subjectCategoryMap.put("Form 4 Computer Studies", category);
                }

                // Check for KSL
                if (categoryName.contains("form 4") &&
                        (categoryName.contains("ksl") || categoryName.contains("sign language"))) {
                    subjectCategoryMap.put("Form 4 KSL", category);
                } else if ((categoryName.contains("ksl") || categoryName.contains("sign language")) &&
                        !categoryName.contains("form")) {
                    subjectCategoryMap.put("Form 4 KSL", category);
                }
            }
        }

        // Notify user if we found API categories
        if (!subjectCategoryMap.isEmpty()) {
            Toast.makeText(this, "Updated content available", Toast.LENGTH_SHORT).show();
        }
    }
    private void onFailRequest() {
        swipeProgress(false);
        if (Tools.isConnect(this)) {
            showFailedView(true, getString(R.string.failed_text));
        } else {
            showFailedView(true, getString(R.string.no_internet_text));
        }
    }

    private void showFailedView(boolean flag, String message) {
        View lytFailed = findViewById(R.id.lyt_failed_category);
        TextView failedMessage = findViewById(R.id.failed_message);

        if (failedMessage != null) {
            failedMessage.setText(message);
        }

        if (flag) {
            recyclerView.setVisibility(View.GONE);
            if (lytFailed != null) {
                lytFailed.setVisibility(View.VISIBLE);
            }
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            if (lytFailed != null) {
                lytFailed.setVisibility(View.GONE);
            }
        }

        View retryButton = findViewById(R.id.failed_retry);
        if (retryButton != null) {
            retryButton.setOnClickListener(view -> requestAction());
        }
    }

    private void showNoItemView(boolean show) {
        View lytNoItem = findViewById(R.id.lyt_no_item_category);
        TextView noItemMessage = findViewById(R.id.no_item_message);

        if (noItemMessage != null) {
            noItemMessage.setText(R.string.no_category_found);
        }

        if (show) {
            recyclerView.setVisibility(View.GONE);
            if (lytNoItem != null) {
                lytNoItem.setVisibility(View.VISIBLE);
            }
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            if (lytNoItem != null) {
                lytNoItem.setVisibility(View.GONE);
            }
        }
    }

    private void swipeProgress(final boolean show) {
        if (!show) {
            swipeRefreshLayout.setRefreshing(show);
            if (lytShimmer != null) {
                lytShimmer.setVisibility(View.GONE);
                lytShimmer.stopShimmer();
            }
            return;
        }

        swipeRefreshLayout.post(() -> {
            swipeRefreshLayout.setRefreshing(show);
            if (lytShimmer != null) {
                lytShimmer.setVisibility(View.VISIBLE);
                lytShimmer.startShimmer();
            }
        });
    }

    private void initShimmerLayout() {
        View lyt_shimmer_category_list = findViewById(R.id.lyt_shimmer_category_list);
        View lyt_shimmer_category_grid2 = findViewById(R.id.lyt_shimmer_category_grid2);
        View lyt_shimmer_category_grid3 = findViewById(R.id.lyt_shimmer_category_grid3);

        if (sharedPref.getCategoryViewType() == CATEGORY_LIST) {
            if (lyt_shimmer_category_list != null) lyt_shimmer_category_list.setVisibility(View.VISIBLE);
            if (lyt_shimmer_category_grid2 != null) lyt_shimmer_category_grid2.setVisibility(View.GONE);
            if (lyt_shimmer_category_grid3 != null) lyt_shimmer_category_grid3.setVisibility(View.GONE);
        } else if (sharedPref.getCategoryViewType() == CATEGORY_GRID_2_COLUMN) {
            if (lyt_shimmer_category_list != null) lyt_shimmer_category_list.setVisibility(View.GONE);
            if (lyt_shimmer_category_grid2 != null) lyt_shimmer_category_grid2.setVisibility(View.VISIBLE);
            if (lyt_shimmer_category_grid3 != null) lyt_shimmer_category_grid3.setVisibility(View.GONE);
        } else if (sharedPref.getCategoryViewType() == CATEGORY_GRID_3_COLUMN) {
            if (lyt_shimmer_category_list != null) lyt_shimmer_category_list.setVisibility(View.GONE);
            if (lyt_shimmer_category_grid2 != null) lyt_shimmer_category_grid2.setVisibility(View.GONE);
            if (lyt_shimmer_category_grid3 != null) lyt_shimmer_category_grid3.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(LearningCard card) {
        String subjectName = card.getTitle();
        Toast.makeText(this, "Opening: " + subjectName, Toast.LENGTH_SHORT).show();

        // Check if we have a category from API
        if (subjectCategoryMap.containsKey(subjectName)) {
            Category category = subjectCategoryMap.get(subjectName);

            // Open ActivityVideoByCategory with the Category object
            Intent intent = new Intent(this, ActivityVideoByCategory.class);
            intent.putExtra(Constant.EXTRA_OBJC, category);
            startActivity(intent);
        } else {
            // If no category found from API, show a message
            Toast.makeText(this,
                    "Content for " + subjectName + " is not available yet. Please try again later.",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        swipeProgress(false);
        if (callbackCall != null && callbackCall.isExecuted()) {
            callbackCall.cancel();
        }
        if (lytShimmer != null) {
            lytShimmer.stopShimmer();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}