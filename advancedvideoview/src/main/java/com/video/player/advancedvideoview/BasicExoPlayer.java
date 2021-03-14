package com.video.player.advancedvideoview;

import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BasicExoPlayer extends AppCompatActivity{

    //Minimum Video you want to buffer while Playing
    public int MIN_BUFFER_DURATION = 3000;
    //Max Video you want to buffer during PlayBack
    public int MAX_BUFFER_DURATION = 5000;
    //Min Video you want to buffer before start Playing it
    public int MIN_PLAYBACK_START_BUFFER = 1500;
    //Min video You want to buffer when user resumes video
    public int MIN_PLAYBACK_RESUME_BUFFER = 5000;

    public static final String VIDEO_PATH_URI = "path";
    public static final String VIDEO_PLAYED = "played";
    private long played = 0;
    protected String path = "";
    protected PlayerView playerView;
    protected SimpleExoPlayer simpleExoPlayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //FULL SCREEN
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //HIDE ACTION BAR
        if (getSupportActionBar() != null && getSupportActionBar().isShowing()) {
            getSupportActionBar().hide();
        }

        //SET CONTENT
        setContentView(R.layout.basic_exo_player);

        //INIT PLAYER
        initializePlayer();

        //GET INTENT VIDEO PATH
        path = getIntent().getStringExtra(VIDEO_PATH_URI);
        if(getIntent()!=null && getIntent().hasExtra(VIDEO_PLAYED)){
            played = Long.parseLong(getIntent().getStringExtra(VIDEO_PLAYED));
        }

        if(path == null || path.isEmpty()){
            Toast.makeText(this, "No Video Path", Toast.LENGTH_SHORT).show();
            finish();
        }

        //SET PATH AND PLAY
        setPath(path);

    }

    public void setPath(String path) {
        if (path == null || path.isEmpty()) {
            Toast.makeText(this, "No Video Path", Toast.LENGTH_SHORT).show();
            finish();
        }else
            buildMediaSource(Uri.parse(path));
    }

    private void initializePlayer() {
        playerView = findViewById(R.id.exoPlayer);
        if (simpleExoPlayer == null) {

            // 1. Create a default TrackSelector
            LoadControl loadControl = new DefaultLoadControl(new DefaultAllocator(true, 16),
                    MIN_BUFFER_DURATION,
                    MAX_BUFFER_DURATION,
                    MIN_PLAYBACK_START_BUFFER,
                    MIN_PLAYBACK_RESUME_BUFFER,
                    -1, true);
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);

            // 2. Create the player
            simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl, null);
            playerView.setPlayer(simpleExoPlayer);
        }
    }

    private void buildMediaSource(Uri mUri) {
        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "Video View"), bandwidthMeter);
        // This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mUri);
        // Prepare the player with the source.
        simpleExoPlayer.prepare(videoSource);
        simpleExoPlayer.setPlayWhenReady(true);
        simpleExoPlayer.seekTo(played);
    }

    protected void release() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
            simpleExoPlayer = null;
        }
    }

    protected void pause() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(false);
            simpleExoPlayer.getPlaybackState();
        }
    }

    protected void resume() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(true);
            simpleExoPlayer.getPlaybackState();
        }
    }

    protected boolean isPlaying(){
        return simpleExoPlayer != null
                && simpleExoPlayer.getPlaybackState() != Player.STATE_ENDED
                && simpleExoPlayer.getPlaybackState() != Player.STATE_IDLE
                && simpleExoPlayer.getPlayWhenReady();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        release();
    }
}
