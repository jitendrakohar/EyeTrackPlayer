package com.example.thirdvideoplayer;

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.widget.Constraints.TAG;

import android.content.Context;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;

import java.util.logging.Handler;
import java.util.logging.LogRecord;


public class EyesTracker extends Tracker<Face> {
    //Threshhold for The Eyes
    private  float THRESHOLD;
//    = 0.75f;
    // Context For Windows
    private Context context;

    //Constructor for EyeTracker Class
    public EyesTracker(Context context) {
        this.context = context;
    }
    //Detecting The Face

    @Override
    public void onUpdate(Detector.Detections<Face> detections, Face face) {
        SharedPreferences preferences=context.getSharedPreferences( "prefs",MODE_PRIVATE);


        THRESHOLD=preferences.getFloat("threshold",0.7F);
        Log.i(TAG, "onUpdate: threshold value"+THRESHOLD);


        if (face.getIsRightEyeOpenProbability() > THRESHOLD || face.getIsRightEyeOpenProbability() > THRESHOLD) {
            Log.i(TAG, "onUpdate: Open Eyes Detected");

            ((videoplayer) context).play();
        } else {
            Log.i(TAG, "onUpdate: Close Eyes Detected");

            ((videoplayer) context).pause();
        }
    }
    // When the Face is not Detected the following function is executed


    @Override
    public void onMissing(Detector.Detections<Face> detections) {
        super.onMissing(detections);
        Log.i(TAG, "onMissing: Face Not Detected!");

        ((videoplayer) context).pause();


    }

    @Override
    public void onDone() {
        super.onDone();
        Log.i(TAG, "onDone: On Done function is called");
        Toast.makeText(context, "OnDone function is called", Toast.LENGTH_SHORT).show();
    }


}