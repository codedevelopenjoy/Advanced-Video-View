package com.video.player.advancedvideoview.cameraAPI;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;

import java.io.IOException;

import androidx.core.app.ActivityCompat;

public class CameraFrontFaceDetectorHelper {
    private CameraSource cameraSource;
    private Context activityContext;
    private OnFaceListener onFaceListener;

    public void init(Context activityContext) {
        this.activityContext = activityContext;
        createCameraSource();
        setLookMe();
    }

    private void setLookMe(){
        try {
            if (ActivityCompat.checkSelfPermission(activityContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) activityContext, new String[]{Manifest.permission.CAMERA}, 1);

                Toast.makeText(activityContext, "Grant Permission and restart app", Toast.LENGTH_SHORT).show();
            }
            cameraSource.start();
            Log.d("ReadThis", "camera-source started, outside");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setFaceListener(OnFaceListener onFaceListener) {
        this.onFaceListener = onFaceListener;
    }

    private class EyesTracker extends Tracker<Face> {

        private EyesTracker() {

        }

        @Override
        public void onUpdate(Detector.Detections<Face> detections, Face face) {
            onFaceListener.onface(detections,face);
        }

        @Override
        public void onMissing(Detector.Detections<Face> detections) {
            super.onMissing(detections);
            onFaceListener.noface(detections);
        }

        @Override
        public void onDone() {
            super.onDone();
        }
    }

    private void createCameraSource() {
        FaceDetector detector = new FaceDetector.Builder(activityContext).setTrackingEnabled(true).setClassificationType(FaceDetector.ALL_CLASSIFICATIONS).setMode(FaceDetector.FAST_MODE).build();

        detector.setProcessor(new LargestFaceFocusingProcessor(detector, new EyesTracker()));

        cameraSource = new CameraSource.Builder(activityContext, detector).setRequestedPreviewSize(1024, 768).setFacing(CameraSource.CAMERA_FACING_FRONT).setRequestedFps(30.0f).build();

        try {
            if (ActivityCompat.checkSelfPermission(activityContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) activityContext, new String[]{Manifest.permission.CAMERA}, 1);
                Toast.makeText(activityContext, "Grant Permission and restart app", Toast.LENGTH_SHORT).show();
            }
            else
                cameraSource.start();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void release(){
        if(cameraSource!=null){
            cameraSource.release();
            cameraSource = null;
        }
    }

}