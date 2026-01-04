package org.deafop.digital_library.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.deafop.digital_library.R;
import org.deafop.digital_library.config.AppConfig;
import org.deafop.digital_library.fragments.FragmentCategory;
import org.deafop.digital_library.fragments.FragmentRecent;
import org.deafop.digital_library.fragments.FragmentSettings;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private BottomNavigationView navigation;
    private MaterialToolbar toolbar;
    private MenuItem sortItem;
    private MainPagerAdapter adapter;

    private final int pagerCount = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.viewpager);
        navigation = findViewById(R.id.navigation);

        setupViewPager();
        setupBottomNavigation();
    }

    private void setupViewPager() {
        adapter = new MainPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(pagerCount);

        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        sortItem = menu.findItem(R.id.action_sort);
        sortItem.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

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

    public void selectCategory() {
        viewPager.setCurrentItem(0);
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
                case 0: return new FragmentCategory();
                case 1: return new FragmentRecent();
                case 2: return new FragmentSettings();
            }
            return new FragmentCategory();
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

}
