package com.video.player.advancedvideoview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.manager.videoviewlibraryhelper.cameraAPI.CameraFrontFaceDetectorHelper;
import com.android.manager.videoviewlibraryhelper.cameraAPI.OnFaceListener;
import com.android.manager.videoviewlibraryhelper.services.VideoPlayerPIPServiceFaceDetection;
import com.google.android.gms.vision.Detector;

import androidx.annotation.Nullable;

public class BasicExoPlayerPIPFaceDetection extends BasicExoPlayerPIP
        implements OnFaceListener {

    private static final float THRESHOLD = 0.4f;
    private boolean[] arr = new boolean[15];
    private int index = 0;
    private int temp1, temp2, i;
    private boolean isFaceDetectionOn = true;
    private boolean isEyesDetectionOn = true;
    private ImageView exo_face,exo_eyes;
    private CameraFrontFaceDetectorHelper cameraFrontFaceDetectorHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cameraFrontFaceDetectorHelper = new CameraFrontFaceDetectorHelper();
        cameraFrontFaceDetectorHelper.init(this);
        cameraFrontFaceDetectorHelper.setFaceListener(this);

        ImageView pip = findViewById(R.id.exo_pip);
        pip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BasicExoPlayerPIPFaceDetection.this, VideoPlayerPIPServiceFaceDetection.class);
                intent.putExtra(VIDEO_PATH_URI, path);
                intent.putExtra(VIDEO_PLAYED, String.valueOf(simpleExoPlayer.getCurrentPosition()));
                startService(intent);
                finish();
                cameraFrontFaceDetectorHelper.release();
            }
        });

        exo_face = findViewById(R.id.exo_face);
        exo_eyes = findViewById(R.id.exo_eyes);

        exo_face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFaceDetectionOn){
                    isFaceDetectionOn = false;
                    exo_face.setImageResource(R.drawable.face_off);
                    cameraFrontFaceDetectorHelper.release();
                    Toast.makeText(BasicExoPlayerPIPFaceDetection.this, "Face Detection Service Stopped", Toast.LENGTH_SHORT).show();
                }
                else{
                    isFaceDetectionOn = true;
                    exo_face.setImageResource(R.drawable.face_on);
                    cameraFrontFaceDetectorHelper = new CameraFrontFaceDetectorHelper();
                    cameraFrontFaceDetectorHelper.init(BasicExoPlayerPIPFaceDetection.this);
                    cameraFrontFaceDetectorHelper.setFaceListener(BasicExoPlayerPIPFaceDetection.this);
                    Toast.makeText(BasicExoPlayerPIPFaceDetection.this, "Face Detection Service Started", Toast.LENGTH_SHORT).show();
                }
            }
        });

        exo_eyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFaceDetectionOn){
                    Toast.makeText(BasicExoPlayerPIPFaceDetection.this, "Cannot Start Without Face Detection Service", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(isEyesDetectionOn){
                    isEyesDetectionOn = false;
                    exo_eyes.setImageResource(R.drawable.eyes_off);
                    Toast.makeText(BasicExoPlayerPIPFaceDetection.this, "Eyes Detection Service Stopped", Toast.LENGTH_SHORT).show();
                }
                else{
                    isEyesDetectionOn = true;
                    exo_eyes.setImageResource(R.drawable.eyes_on);
                    Toast.makeText(BasicExoPlayerPIPFaceDetection.this, "Eyes Detection Service Started", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onface(Detector.Detections<com.google.android.gms.vision.face.Face> detections, final com.google.android.gms.vision.face.Face face) {

        if (index >= arr.length) index = 0;

        arr[index++] = true;

        temp1 = 0;
        temp2 = 0;
        for (i = 0; i < arr.length; i++) {
            if (arr[i]) temp1++;
            else temp2++;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (temp1 > temp2) {
                    if (isEyesDetectionOn) {
                        if (face.getIsLeftEyeOpenProbability() >= THRESHOLD ||
                                face.getIsRightEyeOpenProbability() >= THRESHOLD) {
                            resume();
                        } else pause();
                    } else resume();
                } else pause();
            }
        });
    }

    @Override
    public void noface(Detector.Detections<com.google.android.gms.vision.face.Face> detections) {
        if (index >= arr.length) index = 0;

        arr[index++] = false;

        temp1 = 0;
        temp2 = 0;
        for (i = 0; i < arr.length; i++) {
            if (arr[i]) temp1++;
            else temp2++;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (temp1 < temp2) pause();
                else resume();
            }
        });
    }
}
