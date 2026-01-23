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

public class CseLearningActivity extends AppCompatActivity implements CardAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Call<CallbackCategories> callbackCall = null;
    private ShimmerFrameLayout lytShimmer;
    private SharedPref sharedPref;
    private View rootView;
    private CardAdapter cardAdapter;
    private List<LearningCard> learningCards;

    // Store mapping of media titles to Category objects
    private Map<String, Category> mediaCategoryMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_base);

        rootView = findViewById(android.R.id.content);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("CSE Learning Media");
        }

        // Set description
        TextView tvDescription = findViewById(R.id.tv_description);
        if (tvDescription != null) {
            tvDescription.setText("Access comprehensive sexuality education resources. Learn about relationships, health, and personal development through various media formats.");
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

        // Initialize the media cards
        learningCards = new ArrayList<>();

        // Add media cards - using same colors as AcademicLearningActivity
        learningCards.add(new LearningCard(
                "City Girl - Season 1",
                Color.parseColor("#4CAF50"), // Green (same as Agriculture)
                R.drawable.ic_nav_video,
                "Deaf Series - Season 1"
        ));

        learningCards.add(new LearningCard(
                "City Girl - Season 2",
                Color.parseColor("#2196F3"), // Blue (same as Biology)
                R.drawable.ic_nav_video,
                "Deaf Series - Season 2"
        ));

        learningCards.add(new LearningCard(
                "Silent Cry",
                Color.parseColor("#9C27B0"), // Purple (same as Computer Studies)
                R.drawable.ic_nav_video,
                "Deaf Movie"
        ));

        // Added CSE Media card below Silent Cry
        learningCards.add(new LearningCard(
                "CSE Media",
                Color.parseColor("#FF9800"), // Orange (same as KSL)
                R.drawable.ic_nav_video,
                "Comprehensive Sexuality Education resources"
        ));

        // Create adapter with the media cards
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
                    // Process API categories
                    processApiCategories(resp.categories);
                    swipeProgress(false);
                } else {
                    onFailRequest();
                }
            }

            @Override
            public void onFailure(Call<CallbackCategories> call, Throwable t) {
                if (!call.isCanceled()) {
                    // Even if API fails, we still show the media cards
                    swipeProgress(false);
                    Toast.makeText(CseLearningActivity.this,
                            "Using offline content", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void processApiCategories(List<Category> categories) {
        // Clear existing mapping
        mediaCategoryMap.clear();

        // Try to find matching categories in API for our media
        for (Category category : categories) {
            if (category.category_name != null) {
                String categoryName = category.category_name.toLowerCase();
                String originalName = category.category_name;

                // Check for City Girl - Season 1
                if ((categoryName.contains("city girl") && categoryName.contains("season 1")) ||
                        (categoryName.contains("city girl") && !categoryName.contains("season 2"))) {
                    mediaCategoryMap.put("City Girl - Season 1", category);
                }

                // Check for City Girl - Season 2
                if (categoryName.contains("city girl") && categoryName.contains("season 2")) {
                    mediaCategoryMap.put("City Girl - Season 2", category);
                }

                // Check for Silent Cry
                if (categoryName.contains("silent cry")) {
                    mediaCategoryMap.put("Silent Cry", category);
                }

                // Check for CSE Media
                if (categoryName.contains("cse media") ||
                        (categoryName.contains("cse") && !categoryName.contains("city girl") && !categoryName.contains("silent cry"))) {
                    mediaCategoryMap.put("CSE Media", category);
                }

                // General CSE media categories
                if (categoryName.contains("cse") || categoryName.contains("sexuality") ||
                        categoryName.contains("sexual education") || categoryName.contains("deaf series") ||
                        categoryName.contains("deaf movie")) {
                    // Map to appropriate media based on name
                    if (categoryName.contains("season 1")) {
                        mediaCategoryMap.put("City Girl - Season 1", category);
                    } else if (categoryName.contains("season 2")) {
                        mediaCategoryMap.put("City Girl - Season 2", category);
                    } else if (categoryName.contains("silent cry")) {
                        mediaCategoryMap.put("Silent Cry", category);
                    } else if (categoryName.contains("cse media")) {
                        mediaCategoryMap.put("CSE Media", category);
                    }
                }
            }
        }

        // Notify user if we found API categories
        if (!mediaCategoryMap.isEmpty()) {
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
        String mediaTitle = card.getTitle();

        // Check if we have a category from API
        if (mediaCategoryMap.containsKey(mediaTitle)) {
            Category category = mediaCategoryMap.get(mediaTitle);

            Toast.makeText(this, "Opening: " + mediaTitle, Toast.LENGTH_SHORT).show();

            // Open ActivityVideoByCategory with the Category object
            Intent intent = new Intent(this, ActivityVideoByCategory.class);
            intent.putExtra(Constant.EXTRA_OBJC, category);
            startActivity(intent);
        } else {
            // No category found from API, show error message
            Toast.makeText(this,
                    "Content for \"" + mediaTitle + "\" is not available yet. Please try again later or check your internet connection.",
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