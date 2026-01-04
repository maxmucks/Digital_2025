package org.deafop.digital_library.utils;

import static org.deafop.digital_library.utils.Constant.CATEGORY_LIST;
import static org.deafop.digital_library.utils.Constant.VIDEO_LIST_DEFAULT;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPref(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public Boolean getIsDarkTheme() {
        return sharedPreferences.getBoolean("theme", false);
    }

    public void setIsDarkTheme(Boolean isDarkTheme) {
        editor.putBoolean("theme", isDarkTheme);
        editor.apply();
    }



    public void saveConfig(String api_url, String application_id) {
        editor.putString("api_url", api_url);
        editor.putString("application_id", application_id);
        editor.apply();
    }

    public String getApiUrl() {
        return sharedPreferences.getString("api_url", "http://example.com");
    }

    public String getApplicationId() {
        return sharedPreferences.getString("application_id", "");
    }

    //0 for Most popular
    //1 for Date added (oldest)
    //2 for Date added (newest)
    public void setDefaultSortHome() {
        editor.putInt("sort", 2);
        editor.apply();
    }

    public Integer getCurrentSortHome() {
        return sharedPreferences.getInt("sort", 0);
    }

    public void updateSortHome(int position) {
        editor.putInt("sort", position);
        editor.apply();
    }

    //0 for Most popular
    //1 for Date added (oldest)
    //2 for Date added (newest)
    public void setDefaultSortVideos() {
        editor.putInt("sort_act", 2);
        editor.apply();
    }

    public Integer getCurrentSortVideos() {
        return sharedPreferences.getInt("sort_act", 0);
    }

    public void updateSortVideos(int position) {
        editor.putInt("sort_act", position);
        editor.apply();
    }

    //VIDEO_LIST_DEFAULT for default video list
    //VIDEO_LIST_COMPACT for compact video list
    public Integer getVideoViewType() {
        return sharedPreferences.getInt("video_list", VIDEO_LIST_DEFAULT);
    }

    public void updateVideoViewType(int position) {
        editor.putInt("video_list", position);
        editor.apply();
    }

    //CATEGORY_LIST for category list
    //CATEGORY_GRID_2_COLUMN for category grid (2 column)
    //CATEGORY_GRID_3_COLUMN for category grid (3 column)
    public Integer getCategoryViewType() {
        return sharedPreferences.getInt("category_list", CATEGORY_LIST);
    }

    public void updateCategoryViewType(int position) {
        editor.putInt("category_list", position);
        editor.apply();
    }

}
