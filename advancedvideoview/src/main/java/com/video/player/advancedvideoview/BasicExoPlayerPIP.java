package com.video.player.advancedvideoview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.video.player.advancedvideoview.services.VideoPlayerPIPService;

import androidx.annotation.Nullable;

public class BasicExoPlayerPIP extends BasicExoPlayer {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImageView pipMode = findViewById(R.id.exo_pip);

        pipMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BasicExoPlayerPIP.this, VideoPlayerPIPService.class);
                intent.putExtra(VIDEO_PATH_URI,path);
                intent.putExtra(VIDEO_PLAYED,String.valueOf(simpleExoPlayer.getCurrentPosition()));
                startService(intent);
                finish();
            }
        });
    }
}
