package org.deafop.digital_library.activities;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;

import org.deafop.digital_library.R;
import org.deafop.digital_library.config.AppConfig;
import org.deafop.digital_library.utils.Constant;

public class ActivityYoutubePlayer extends AppCompatActivity {

    private WebView webView;
    private String videoId;
    private String videoTitle;
    private View loadingLayout;
    private View errorLayout;
    private TextView errorMessage;
    private MaterialButton retryButton;
    private MaterialButton closeButton;
    private MaterialButton openInBrowserButton;
    private Toolbar toolbar;
    private ProgressBar progressBar;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);

        initViews();
        getIntentData();

        if (videoId == null || videoId.isEmpty()) {
            showError("Invalid video ID");
            return;
        }

        setupToolbar();

        if (!isNetworkAvailable()) {
            showError("No internet connection");
            return;
        }

        setupWebView();
        setupClickListeners();

        loadYouTubeVideo();
    }

    private void initViews() {
        webView = findViewById(R.id.youtube_view);
        loadingLayout = findViewById(R.id.loading_layout);
        errorLayout = findViewById(R.id.error_layout);
        errorMessage = findViewById(R.id.error_message);
        retryButton = findViewById(R.id.retry_button);
        closeButton = findViewById(R.id.close_button);
        openInBrowserButton = findViewById(R.id.open_in_browser_button);
        toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            String videoString = intent.getStringExtra(Constant.KEY_VIDEO_ID);
            videoTitle = intent.getStringExtra(Constant.KEY_VIDEO_TITLE);

            videoId = extractVideoId(videoString);
            Log.d("YouTubePlayer", "Extracted video ID: " + videoId);

            if (videoTitle != null && toolbar != null) {
                toolbar.setTitle(videoTitle);
            }
        }
    }

    private String extractVideoId(String videoString) {
        if (videoString == null) return null;

        String cleaned = videoString.trim();

        // If it's already a clean ID (11 characters)
        if (cleaned.matches("[A-Za-z0-9_-]{11}")) {
            return cleaned;
        }

        // Extract from various YouTube URL formats
        if (cleaned.contains("youtube.com/watch?v=")) {
            int start = cleaned.indexOf("v=") + 2;
            int end = cleaned.indexOf('&', start);
            if (end == -1) end = cleaned.length();
            return cleaned.substring(start, end);
        } else if (cleaned.contains("youtu.be/")) {
            int start = cleaned.indexOf("youtu.be/") + 9;
            int end = cleaned.indexOf('?', start);
            if (end == -1) end = cleaned.length();
            return cleaned.substring(start, end);
        } else if (cleaned.contains("embed/")) {
            int start = cleaned.indexOf("embed/") + 6;
            int end = cleaned.indexOf('?', start);
            if (end == -1) end = cleaned.length();
            return cleaned.substring(start, end);
        }

        return cleaned;
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();

        // Basic settings
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        // Disable caching
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        // Allow autoplay
        webSettings.setMediaPlaybackRequiresUserGesture(false);

        // Set desktop user agent to get full YouTube page
        String desktopUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
        webSettings.setUserAgentString(desktopUserAgent);

        // Enable hardware acceleration
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d("YouTubePlayer", "Page finished loading: " + url);
                loadingLayout.setVisibility(View.GONE);

                // Inject JavaScript to hide everything except the video player
                hideYouTubeUI();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.e("YouTubePlayer", "Error loading: " + error.getDescription());
                runOnUiThread(() -> {
                    showError("Error loading video: " + error.getDescription());
                    openInBrowserButton.setVisibility(View.VISIBLE);
                });
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.e("YouTubePlayer", "Error " + errorCode + ": " + description);
                runOnUiThread(() -> {
                    showError("Error " + errorCode + ": " + description);
                    openInBrowserButton.setVisibility(View.VISIBLE);
                });
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (progressBar != null) {
                    progressBar.setProgress(newProgress);
                    if (newProgress == 100) {
                        progressBar.setVisibility(View.GONE);
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void hideYouTubeUI() {
        // JavaScript to hide all YouTube UI elements except the video player
        String js = "javascript:(function() {" +
                "// Hide header\n" +
                "var header = document.querySelector('ytd-app');\n" +
                "if(header) header.style.display = 'none';\n" +
                "\n" +
                "// Hide navigation\n" +
                "var nav = document.querySelector('#navigation');\n" +
                "if(nav) nav.style.display = 'none';\n" +
                "\n" +
                "// Hide related videos and comments\n" +
                "var secondary = document.querySelector('#secondary');\n" +
                "if(secondary) secondary.style.display = 'none';\n" +
                "\n" +
                "// Hide footer\n" +
                "var footer = document.querySelector('ytd-app > footer');\n" +
                "if(footer) footer.style.display = 'none';\n" +
                "\n" +
                "// Make video player full screen\n" +
                "var player = document.querySelector('#player');\n" +
                "if(player) {\n" +
                "  player.style.position = 'fixed';\n" +
                "  player.style.top = '0';\n" +
                "  player.style.left = '0';\n" +
                "  player.style.width = '100%';\n" +
                "  player.style.height = '100%';\n" +
                "  player.style.zIndex = '9999';\n" +
                "}\n" +
                "\n" +
                "// Remove margin and padding from body\n" +
                "document.body.style.margin = '0';\n" +
                "document.body.style.padding = '0';\n" +
                "document.body.style.overflow = 'hidden';\n" +
                "\n" +
                "// Hide any other elements that might appear\n" +
                "var elements = document.querySelectorAll('ytd-app:not(#player), #masthead, #container, ytd-browse, ytd-page-manager');\n" +
                "for(var i = 0; i < elements.length; i++) {\n" +
                "  elements[i].style.display = 'none';\n" +
                "}\n" +
                "})()";

        // Run the JavaScript after a delay to ensure page is loaded
        webView.postDelayed(() -> webView.loadUrl(js), 1000);
    }

    private void setupClickListeners() {
        retryButton.setOnClickListener(v -> {
            errorLayout.setVisibility(View.GONE);
            openInBrowserButton.setVisibility(View.GONE);
            loadingLayout.setVisibility(View.VISIBLE);
            loadYouTubeVideo();
        });

        closeButton.setOnClickListener(v -> finish());

        openInBrowserButton.setOnClickListener(v -> openInYouTubeApp());
    }

    private void loadYouTubeVideo() {
        if (videoId == null || videoId.isEmpty()) {
            showError("Invalid video ID");
            return;
        }

        Log.d("YouTubePlayer", "Loading video ID: " + videoId);

        // Load the watch page instead of embed
        String watchUrl = "https://www.youtube.com/watch?v=" + videoId + "&autoplay=1";
        webView.loadUrl(watchUrl);
    }

    private void openInYouTubeApp() {
        try {
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
            startActivity(appIntent);
        } catch (ActivityNotFoundException e) {
            try {
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.youtube.com/watch?v=" + videoId));
                startActivity(webIntent);
            } catch (Exception ex) {
                Toast.makeText(this, "Cannot open video", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showError(String message) {
        runOnUiThread(() -> {
            loadingLayout.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorMessage.setText(message);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) {
            webView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) {
            webView.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.loadUrl("about:blank");
            webView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}