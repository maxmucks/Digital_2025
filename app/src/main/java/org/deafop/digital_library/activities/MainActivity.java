package org.deafop.digital_library.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.deafop.digital_library.R;
import org.deafop.digital_library.fragments.AcademicLearningFragment;
import org.deafop.digital_library.fragments.FragmentCategory;
import org.deafop.digital_library.fragments.FragmentRecent;
import org.deafop.digital_library.fragments.FragmentSettings;
import org.deafop.digital_library.fragments.HomeFragment;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private BottomNavigationView navigation;
    private MaterialToolbar toolbar;
    private MenuItem sortItem;
    private MainPagerAdapter adapter;
    private FrameLayout fragmentContainer;
    private FrameLayout contentFrame;

    private final int pagerCount = 3;
    private boolean isInNestedNavigation = false;
    private String currentSubjectTitle = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragmentContainer = findViewById(R.id.fragment_container);
        contentFrame = findViewById(R.id.content_frame);

        viewPager = findViewById(R.id.viewpager);
        navigation = findViewById(R.id.navigation);

        setupViewPager();
        setupBottomNavigation();

        // Initially hide back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private void setupViewPager() {
        adapter = new MainPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(pagerCount);

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (isInNestedNavigation) {
                    exitNestedNavigation();
                }

                navigation.getMenu().getItem(position).setChecked(true);

                switch (position) {
                    case 0:
                        getSupportActionBar().setTitle(R.string.title_nav_category);
                        showSortMenu(false);
                        break;

                    case 1:
                        getSupportActionBar().setTitle(R.string.title_nav_home);
                        showSortMenu(true);
                        break;

                    case 2:
                        getSupportActionBar().setTitle(R.string.title_nav_settings);
                        showSortMenu(false);
                        break;
                }
            }
        });
    }

    private void setupBottomNavigation() {
        navigation.setOnNavigationItemSelectedListener(item -> {
            if (isInNestedNavigation) {
                exitNestedNavigation();
            }

            if (item.getItemId() == R.id.navigation_category) {
                viewPager.setCurrentItem(0);
                return true;
            } else if (item.getItemId() == R.id.navigation_home) {
                viewPager.setCurrentItem(1);
                return true;
            } else if (item.getItemId() == R.id.navigation_settings) {
                viewPager.setCurrentItem(2);
                return true;
            }
            return false;
        });
    }

    // Method to navigate to a nested fragment
    public void navigateToSubjectFragment(Fragment fragment, String subjectName) {
        isInNestedNavigation = true;
        currentSubjectTitle = subjectName;

        // Hide ViewPager and show fragment container
        viewPager.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.VISIBLE);

        // Hide bottom navigation
        navigation.setVisibility(View.GONE);

        // Update toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(subjectName);

        // Set custom back navigation icon
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Clear any existing fragments
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
            fm.popBackStack();
        }

        // Add fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment, "SubjectFragment")
                .addToBackStack(null)
                .commit();
    }

    // Method to exit nested navigation
    public void exitNestedNavigation() {
        isInNestedNavigation = false;
        currentSubjectTitle = "";

        // Show ViewPager and hide fragment container
        viewPager.setVisibility(View.VISIBLE);
        fragmentContainer.setVisibility(View.GONE);

        // Show bottom navigation
        navigation.setVisibility(View.VISIBLE);

        // Update toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(R.string.title_nav_category);

        // Reset navigation icon
        toolbar.setNavigationIcon(R.drawable.ic_dots);
        toolbar.setNavigationOnClickListener(null);

        // Clear fragment container
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag("SubjectFragment");
        if (fragment != null) {
            fm.beginTransaction().remove(fragment).commit();
        }
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        sortItem = menu.findItem(R.id.action_sort);
        sortItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle toolbar back button
        if (item.getItemId() == android.R.id.home) {
            if (isInNestedNavigation) {
                exitNestedNavigation();
                return true;
            }
        }

        if (item.getItemId() == R.id.action_search) {
            startActivity(new Intent(this, ActivitySearch.class));
            return true;
        }

        if (item.getItemId() == R.id.action_sort) {
            Fragment fragment = adapter.getItem(viewPager.getCurrentItem());
            if (fragment instanceof FragmentRecent) {
                ((FragmentRecent) fragment).showSortDialog();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showSortMenu(boolean show) {
        if (sortItem != null) {
            sortItem.setVisible(show);
        }
    }

    // ADD BACK THE selectCategory METHOD
    public void selectCategory() {
        if (isInNestedNavigation) {
            exitNestedNavigation();
        }
        viewPager.setCurrentItem(0);
    }

    @Override
    public void onBackPressed() {
        if (isInNestedNavigation) {
            exitNestedNavigation();
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    // ---------------- ADAPTER ----------------
    class MainPagerAdapter extends FragmentPagerAdapter {

        MainPagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: return new HomeFragment();
                //case 0: return new AcademicLearningFragment();
                case 1: return new FragmentCategory();
                case 2: return new FragmentSettings();
            }
            return new AcademicLearningFragment();
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
    // In MainActivity.java, add this method:
    public void navigateToAcademicLearning() {
        if (isInNestedNavigation) {
            exitNestedNavigation();
        }
        viewPager.setCurrentItem(0); // AcademicLearningFragment position
    }
}