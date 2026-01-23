package org.deafop.digital_library.fragments;

import static org.deafop.digital_library.utils.Constant.CATEGORY_GRID_2_COLUMN;
import static org.deafop.digital_library.utils.Constant.CATEGORY_GRID_3_COLUMN;
import static org.deafop.digital_library.utils.Constant.CATEGORY_LIST;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;

import org.deafop.digital_library.R;
import org.deafop.digital_library.activities.ActivityVideoByCategory;
import org.deafop.digital_library.adapters.AdapterCategory;
import org.deafop.digital_library.callbacks.CallbackCategories;
import org.deafop.digital_library.config.AppConfig;
import org.deafop.digital_library.models.Category;
import org.deafop.digital_library.rests.ApiInterface;
import org.deafop.digital_library.rests.RestAdapter;
import org.deafop.digital_library.utils.Constant;
import org.deafop.digital_library.utils.ItemOffsetDecoration;
import org.deafop.digital_library.utils.SharedPref;
import org.deafop.digital_library.utils.Tools;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubjectFragment extends Fragment {

    private static final String ARG_SUBJECT_NAME = "subject_name";

    private View root_view;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AdapterCategory adapterCategory;
    public static final String EXTRA_OBJC = "key.EXTRA_OBJC";
    private Call<CallbackCategories> callbackCall = null;
    private ShimmerFrameLayout lyt_shimmer;
    private SharedPref sharedPref;
    private String subjectName;

    // Factory method to create new instance with subject name
    public static SubjectFragment newInstance(String subjectName) {
        SubjectFragment fragment = new SubjectFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SUBJECT_NAME, subjectName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            subjectName = getArguments().getString(ARG_SUBJECT_NAME);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.fragment_category, container, false); // Use fragment_category layout

        sharedPref = new SharedPref(requireActivity());

        lyt_shimmer = root_view.findViewById(R.id.shimmer_view_container);
        swipeRefreshLayout = root_view.findViewById(R.id.swipe_refresh_layout_category);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        recyclerView = root_view.findViewById(R.id.recyclerViewCategory);
       // recyclerView.setHasFixedSize(true);

        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(requireActivity(), R.dimen.item_offset);

        // Grid layout with 2 columns
        recyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), 2));
        recyclerView.addItemDecoration(itemDecoration);
       // recyclerView.setHasFixedSize(true);
        // Set up adapter
        adapterCategory = new AdapterCategory(requireActivity(), new ArrayList<>());
        recyclerView.setAdapter(adapterCategory);

        // On item click listener
        adapterCategory.setOnItemClickListener((v, obj, position) -> {
            Intent intent = new Intent(requireActivity(), ActivityVideoByCategory.class);
            intent.putExtra(EXTRA_OBJC, obj);
            startActivity(intent);
        });

        // Swipe to refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            adapterCategory.resetListData();
            requestAction();
        });

        requestAction();
        initShimmerLayout();

        return root_view;
    }

    private List<Category> filterCategoriesBySubject(List<Category> categories) {
        List<Category> filtered = new ArrayList<>();

        if (subjectName == null || subjectName.isEmpty()) {
            // If no subject specified, show all form-related categories
            for (Category c : categories) {
                if (c.category_name != null &&
                        c.category_name.toLowerCase().contains("form")) {
                    filtered.add(c);
                }
            }
        } else {
            // Filter by specific subject name
            String searchTerm = subjectName.toLowerCase();

            // Extract the subject from "Form 4 Biology" -> "biology"
            String[] parts = subjectName.split(" ");
            String subjectKeyword = "";
            if (parts.length >= 3) {
                subjectKeyword = parts[2].toLowerCase(); // "biology", "agriculture", etc.
            }

            for (Category c : categories) {
                if (c.category_name != null) {
                    String categoryLower = c.category_name.toLowerCase();

                    // Check if category contains the full subject name or just the subject keyword
                    if (categoryLower.contains(searchTerm) ||
                            (subjectKeyword.length() > 3 && categoryLower.contains(subjectKeyword))) {
                        filtered.add(c);
                    }
                }
            }
        }
        return filtered;
    }

    private void displayApiResult(final List<Category> categories) {
        List<Category> filteredCategories = filterCategoriesBySubject(categories);

        adapterCategory.setListData(filteredCategories);
        swipeProgress(false);

        if (filteredCategories.size() == 0) {
            showNoItemView(true);
        }
    }

    private void requestCategoriesApi() {
        ApiInterface apiInterface = RestAdapter.createAPI(sharedPref.getApiUrl());
        callbackCall = apiInterface.getAllCategories(AppConfig.REST_API_KEY);

        callbackCall.enqueue(new Callback<CallbackCategories>() {
            @Override
            public void onResponse(Call<CallbackCategories> call, Response<CallbackCategories> response) {
                CallbackCategories resp = response.body();
                if (resp != null && resp.status.equals("ok")) {
                    displayApiResult(resp.categories);
                } else {
                    onFailRequest();
                }
            }

            @Override
            public void onFailure(Call<CallbackCategories> call, Throwable t) {
                if (!call.isCanceled()) onFailRequest();
            }
        });
    }

    private void onFailRequest() {
        swipeProgress(false);
        if (Tools.isConnect(requireActivity())) {
            showFailedView(true, getString(R.string.failed_text));
        } else {
            showFailedView(true, getString(R.string.no_internet_text));
        }
    }

    private void requestAction() {
        showFailedView(false, "");
        swipeProgress(true);
        showNoItemView(false);

        new Handler(Looper.getMainLooper()).postDelayed(
                this::requestCategoriesApi,
                Constant.DELAY_TIME
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        swipeProgress(false);
        if (callbackCall != null && callbackCall.isExecuted()) {
            callbackCall.cancel();
        }
        lyt_shimmer.stopShimmer();
    }

    private void showFailedView(boolean flag, String message) {
        View lyt_failed = root_view.findViewById(R.id.lyt_failed_category);
        ((TextView) root_view.findViewById(R.id.failed_message)).setText(message);

        if (flag) {
            recyclerView.setVisibility(View.GONE);
            lyt_failed.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_failed.setVisibility(View.GONE);
        }

        root_view.findViewById(R.id.failed_retry).setOnClickListener(view -> requestAction());
    }

    private void showNoItemView(boolean show) {
        View lyt_no_item = root_view.findViewById(R.id.lyt_no_item_category);
        ((TextView) root_view.findViewById(R.id.no_item_message)).setText(R.string.no_category_found);

        if (show) {
            recyclerView.setVisibility(View.GONE);
            lyt_no_item.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            lyt_no_item.setVisibility(View.GONE);
        }
    }

    private void swipeProgress(final boolean show) {
        if (!show) {
            swipeRefreshLayout.setRefreshing(show);
            lyt_shimmer.setVisibility(View.GONE);
            lyt_shimmer.stopShimmer();
            return;
        }

        swipeRefreshLayout.post(() -> {
            swipeRefreshLayout.setRefreshing(show);
            lyt_shimmer.setVisibility(View.VISIBLE);
            lyt_shimmer.startShimmer();
        });
    }

    private void initShimmerLayout() {
        View lyt_shimmer_category_list = root_view.findViewById(R.id.lyt_shimmer_category_list);
        View lyt_shimmer_category_grid2 = root_view.findViewById(R.id.lyt_shimmer_category_grid2);
        View lyt_shimmer_category_grid3 = root_view.findViewById(R.id.lyt_shimmer_category_grid3);

        if (sharedPref.getCategoryViewType() == CATEGORY_LIST) {
            lyt_shimmer_category_list.setVisibility(View.VISIBLE);
            lyt_shimmer_category_grid2.setVisibility(View.GONE);
            lyt_shimmer_category_grid3.setVisibility(View.GONE);
        } else if (sharedPref.getCategoryViewType() == CATEGORY_GRID_2_COLUMN) {
            lyt_shimmer_category_list.setVisibility(View.GONE);
            lyt_shimmer_category_grid2.setVisibility(View.VISIBLE);
            lyt_shimmer_category_grid3.setVisibility(View.GONE);
        } else if (sharedPref.getCategoryViewType() == CATEGORY_GRID_3_COLUMN) {
            lyt_shimmer_category_list.setVisibility(View.GONE);
            lyt_shimmer_category_grid2.setVisibility(View.GONE);
            lyt_shimmer_category_grid3.setVisibility(View.VISIBLE);
        }
    }
}