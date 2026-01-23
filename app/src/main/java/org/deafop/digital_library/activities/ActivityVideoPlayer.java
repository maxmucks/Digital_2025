package org.deafop.digital_library.activities;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionParameters;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;

import org.deafop.digital_library.R;
import org.deafop.digital_library.config.AppConfig;

public class ActivityVideoPlayer extends AppCompatActivity {

    private static final String TAG = "ActivityVideoPlayer";
    private static final int RETRY_DELAY_MS = 2000;
    private static final int MAX_RETRY_COUNT = 3;

    private String videoUrl;
    private ExoPlayer player;
    private PlayerView playerView;
    private ProgressBar progressBar;
    private TextView bufferingText;
    private View errorLayout;
    private TextView errorText;
    private ImageView retryButton;
    private ImageView closeButton;
    private ImageView fullscreenButton;

    private boolean isFullscreen = false;
    private int retryCount = 0;
    private Handler handler = new Handler(Looper.getMainLooper());
    private boolean controlsVisible = false;

    // Track player state
    private long playbackPosition = 0;
    private boolean playWhenReady = true;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate called");

        try {
            // Set fullscreen before setContentView
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

            // Keep screen on
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            setContentView(R.layout.activity_video_player);
            Log.d(TAG, "ContentView set successfully");

            if (AppConfig.FORCE_PLAYER_TO_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }

            initializeViews();
            getIntentData();

            if (videoUrl == null || videoUrl.isEmpty()) {
                showError("No video URL provided");
                return;
            }

            initializePlayer();
            setupPlayerListeners();
            setupClickListeners();

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Failed to initialize video player", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initializeViews() {
        try {
            playerView = findViewById(R.id.exoPlayerView);
            progressBar = findViewById(R.id.progressBar);
            bufferingText = findViewById(R.id.bufferingText);
            errorLayout = findViewById(R.id.errorLayout);
            errorText = findViewById(R.id.errorText);
            retryButton = findViewById(R.id.retryButton);
            closeButton = findViewById(R.id.closeButton);
            fullscreenButton = findViewById(R.id.fullscreenButton);

            // Hide loading initially
            progressBar.setVisibility(View.GONE);
            bufferingText.setVisibility(View.GONE);
            errorLayout.setVisibility(View.GONE);

            Log.d(TAG, "Views initialized");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage(), e);
        }
    }

    private void getIntentData() {
        try {
            videoUrl = getIntent().getStringExtra("url");
            if (getIntent().hasExtra("position")) {
                playbackPosition = getIntent().getLongExtra("position", 0);
            }
            if (getIntent().hasExtra("playWhenReady")) {
                playWhenReady = getIntent().getBooleanExtra("playWhenReady", true);
            }
            Log.d(TAG, "Got intent data - URL: " + (videoUrl != null ? videoUrl.substring(0, Math.min(videoUrl.length(), 50)) + "..." : "null"));
        } catch (Exception e) {
            Log.e(TAG, "Error getting intent data: " + e.getMessage(), e);
        }
    }

    private void initializePlayer() {
        try {
            Log.d(TAG, "Initializing player");

            // Configure track selector
            DefaultTrackSelector trackSelector = new DefaultTrackSelector(this);

            // Set preferred video quality
            TrackSelectionParameters parameters = trackSelector.getParameters()
                    .buildUpon()
                    .setMaxVideoSize(1920, 1080) // Max 1080p
                    .setPreferredAudioLanguage("en")
                    .build();
            trackSelector.setParameters(parameters);

            // Create player with optimized settings
            player = new ExoPlayer.Builder(this)
                    .setTrackSelector(trackSelector)
                    .setLoadControl(new DefaultLoadControl.Builder()
                            .setBufferDurationsMs(
                                    15000,  // Min buffer
                                    30000,  // Max buffer
                                    1500,   // Buffer for playback
                                    2000    // Buffer for playback after re-buffering
                            )
                            .build())
                    .build();

            playerView.setPlayer(player);
            playerView.setKeepScreenOn(true);

            // Configure controller
            playerView.setControllerAutoShow(true);
            playerView.setControllerShowTimeoutMs(8000);
            playerView.setControllerHideOnTouch(false);

            // Show controller immediately when video starts
            playerView.showController();

            Log.d(TAG, "Player initialized successfully");

            // Load and play the video
            loadVideo();

        } catch (Exception e) {
            Log.e(TAG, "Error initializing player: " + e.getMessage(), e);
            showError("Failed to initialize video player: " + e.getMessage());
        }
    }

    private void loadVideo() {
        if (videoUrl == null || videoUrl.isEmpty()) {
            showError("Invalid video URL");
            return;
        }

        try {
            Log.d(TAG, "Loading video from URL: " + videoUrl);

            Uri uri = Uri.parse(videoUrl);
            MediaSource mediaSource = buildMediaSource(uri);

            // Set media source
            player.setMediaSource(mediaSource);
            player.prepare();

            // Restore playback position if available
            if (playbackPosition > 0) {
                player.seekTo(playbackPosition);
            }

            player.setPlayWhenReady(playWhenReady);

            // Show controls after a short delay
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (playerView != null) {
                        playerView.showController();
                        controlsVisible = true;
                    }
                }
            }, 500);

            Log.d(TAG, "Video loading started");

        } catch (Exception e) {
            Log.e(TAG, "Failed to load video: " + e.getMessage(), e);
            showError("Failed to load video: " + e.getMessage());
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        try {
            String userAgent = Util.getUserAgent(this, getString(R.string.app_name));

            // Create data source factory based on URL scheme
            DataSource.Factory dataSourceFactory;
            if (uri.getScheme() != null && uri.getScheme().startsWith("http")) {
                // For HTTP/HTTPS URLs
                HttpDataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory()
                        .setUserAgent(userAgent)
                        .setConnectTimeoutMs(DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS)
                        .setReadTimeoutMs(DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS)
                        .setAllowCrossProtocolRedirects(true);
                dataSourceFactory = httpDataSourceFactory;
            } else {
                // For local files or content URIs
                dataSourceFactory = new DefaultDataSource.Factory(this);
            }

            // Determine content type
            @C.ContentType int type = Util.inferContentType(uri);
            Log.d(TAG, "Content type: " + type);

            // Create appropriate media source
            switch (type) {
                case C.TYPE_DASH:
                    return new DashMediaSource.Factory(dataSourceFactory)
                            .createMediaSource(MediaItem.fromUri(uri));
                case C.TYPE_HLS:
                    return new HlsMediaSource.Factory(dataSourceFactory)
                            .createMediaSource(MediaItem.fromUri(uri));
                case C.TYPE_SS:
                    // SmoothStreaming
                    return new DefaultMediaSourceFactory(dataSourceFactory)
                            .createMediaSource(MediaItem.fromUri(uri));
                case C.TYPE_RTSP:
                    // RTSP - might need special handling
                    return new DefaultMediaSourceFactory(dataSourceFactory)
                            .createMediaSource(MediaItem.fromUri(uri));
                default:
                    // For regular video files (MP4, MKV, etc.)
                    return new ProgressiveMediaSource.Factory(dataSourceFactory)
                            .createMediaSource(MediaItem.fromUri(uri));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error building media source: " + e.getMessage(), e);
            throw e;
        }
    }

    private void setupPlayerListeners() {
        try {
            player.addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(@Player.State int state) {
                    Log.d(TAG, "Playback state changed: " + state);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                switch (state) {
                                    case Player.STATE_IDLE:
                                        progressBar.setVisibility(View.GONE);
                                        bufferingText.setVisibility(View.GONE);
                                        break;
                                    case Player.STATE_BUFFERING:
                                        progressBar.setVisibility(View.VISIBLE);
                                        bufferingText.setVisibility(View.VISIBLE);
                                        // Show controls when buffering
                                        playerView.showController();
                                        break;
                                    case Player.STATE_READY:
                                        progressBar.setVisibility(View.GONE);
                                        bufferingText.setVisibility(View.GONE);
                                        errorLayout.setVisibility(View.GONE);
                                        retryCount = 0; // Reset retry count on successful play
                                        // Show controls when ready
                                        playerView.showController();
                                        break;
                                    case Player.STATE_ENDED:
                                        // Show controls when video ends
                                        playerView.showController();
                                        break;
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error in playback state change: " + e.getMessage());
                            }
                        }
                    });
                }

                @Override
                public void onPlayerError(PlaybackException error) {
                    Log.e(TAG, "Player error: " + error.getMessage(), error);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                progressBar.setVisibility(View.GONE);
                                bufferingText.setVisibility(View.GONE);

                                String errorMessage = "Playback error";
                                if (error.errorCode == PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW) {
                                    errorMessage = "Live stream is behind live window";
                                    // Special handling for live streams
                                    if (player != null) {
                                        player.seekToDefaultPosition();
                                        player.prepare();
                                    }
                                } else if (error.errorCode == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED) {
                                    errorMessage = "Network error. Please check your connection.";
                                } else if (error.errorCode == PlaybackException.ERROR_CODE_PARSING_CONTAINER_UNSUPPORTED) {
                                    errorMessage = "Unsupported video format";
                                }

                                showError(errorMessage);

                                // Auto-retry for network errors
                                if (retryCount < MAX_RETRY_COUNT &&
                                        (error.errorCode == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED ||
                                                error.errorCode == PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS)) {
                                    retryCount++;
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            retryLoad();
                                        }
                                    }, RETRY_DELAY_MS);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error handling player error: " + e.getMessage());
                            }
                        }
                    });
                }
            });

            Log.d(TAG, "Player listeners setup completed");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up player listeners: " + e.getMessage(), e);
        }
    }

    private void setupClickListeners() {
        try {
            retryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        errorLayout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                        retryLoad();
                    } catch (Exception e) {
                        Log.e(TAG, "Error in retry button click: " + e.getMessage());
                    }
                }
            });

            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            // Fullscreen button click listener
            fullscreenButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleFullscreen();
                }
            });

            // Set up a simple touch listener for the player view
            playerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toggle controls visibility
                    if (controlsVisible) {
                        playerView.hideController();
                        controlsVisible = false;
                    } else {
                        playerView.showController();
                        controlsVisible = true;
                    }
                }
            });

            Log.d(TAG, "Click listeners setup completed");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up click listeners: " + e.getMessage(), e);
        }
    }

    private void toggleFullscreen() {
        if (isFullscreen) {
            exitFullscreen();
        } else {
            enterFullscreen();
        }
        isFullscreen = !isFullscreen;

        // Update button icon
        updateFullscreenButtonIcon();
    }

    private void updateFullscreenButtonIcon() {
        try {
            if (isFullscreen) {
                fullscreenButton.setImageResource(R.drawable.ic_fullscreen_exit);
            } else {
                fullscreenButton.setImageResource(R.drawable.ic_fullscreen);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating fullscreen button icon: " + e.getMessage());
        }
    }

    private void showError(String message) {
        try {
            Log.e(TAG, "Showing error: " + message);
            if (errorLayout != null && errorText != null) {
                errorLayout.setVisibility(View.VISIBLE);
                errorText.setText(message);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing error message: " + e.getMessage());
        }
    }

    private void retryLoad() {
        try {
            if (player != null) {
                player.stop();
                player.clearMediaItems();
                loadVideo();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error retrying load: " + e.getMessage(), e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        try {
            if (player != null) {
                player.setPlayWhenReady(true);
            }
            // Show controls when resuming
            if (playerView != null) {
                playerView.showController();
                controlsVisible = true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onResume: " + e.getMessage());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        try {
            if (player != null) {
                playWhenReady = player.getPlayWhenReady();
                playbackPosition = player.getCurrentPosition();
                player.setPlayWhenReady(false);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onPause: " + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        try {
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
            }
            releasePlayer();
        } catch (Exception e) {
            Log.e(TAG, "Error in onDestroy: " + e.getMessage());
        }
    }

    private void releasePlayer() {
        try {
            if (player != null) {
                playbackPosition = player.getCurrentPosition();
                playWhenReady = player.getPlayWhenReady();
                player.release();
                player = null;
                Log.d(TAG, "Player released");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error releasing player: " + e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (isFullscreen) {
                exitFullscreen();
                isFullscreen = false;
                updateFullscreenButtonIcon();
            } else {
                // Save playback state before finishing
                if (player != null) {
                    playbackPosition = player.getCurrentPosition();
                    playWhenReady = player.getPlayWhenReady();
                }
                super.onBackPressed();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onBackPressed: " + e.getMessage());
            super.onBackPressed();
        }
    }

    private void exitFullscreen() {
        // Exit fullscreen mode
        if (AppConfig.FORCE_PLAYER_TO_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        // Show system UI
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    private void enterFullscreen() {
        // Enter fullscreen mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Hide system UI
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    // Save instance state for configuration changes
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            if (player != null) {
                outState.putLong("playbackPosition", player.getCurrentPosition());
                outState.putBoolean("playWhenReady", player.getPlayWhenReady());
                outState.putBoolean("isFullscreen", isFullscreen);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error saving instance state: " + e.getMessage());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        try {
            if (savedInstanceState != null) {
                playbackPosition = savedInstanceState.getLong("playbackPosition", 0);
                playWhenReady = savedInstanceState.getBoolean("playWhenReady", true);
                isFullscreen = savedInstanceState.getBoolean("isFullscreen", false);

                if (isFullscreen) {
                    enterFullscreen();
                }
                updateFullscreenButtonIcon();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error restoring instance state: " + e.getMessage());
        }
    }
}