package com.example.thirdvideoplayer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.IOException;

public class videoplayer extends AppCompatActivity {

// variable for eye tracking system
    boolean isOpen = true;
    boolean flag;
    int pauseposition=0;
    boolean isEyeTrackingenabled=false;
    ImageButton eyeTrack;
    CameraSource cameraSource;

// Variable for  other player of player
    String time;

    private TextView videoNameTv, videoTimeTv;
    private View decorview;
    private ImageButton backIb, forwardIB, playpauseIB;
    private SeekBar videoSeekBar;
    public VideoView videoView;
    private RelativeLayout controlsRL, videosRL;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoplayer);
//Initializine the variable for the activity
        videoNameTv = findViewById(R.id.IdTVVideoTitle);
        videoTimeTv = findViewById(R.id.IvVideotime);
        forwardIB = findViewById(R.id.idIBForward);
        backIb = findViewById(R.id.idIBBack);
        videoSeekBar = findViewById(R.id.seekbarprogress);
        videoView = findViewById(R.id.idVideoView);
        controlsRL = findViewById(R.id.controls);
        playpauseIB = findViewById(R.id.idIBPlay);
        videosRL = findViewById(R.id.idRlvideo);
        eyeTrack=findViewById(R.id.eyeTrack);

//Getting a data from MainActivity from Another Activity Through Intent
        Intent i = getIntent();
        String videoPath = String.valueOf(i.getStringExtra("uri"));
        String videoName = i.getStringExtra("title");
        int maxDuration = Integer.parseInt(i.getStringExtra("maxDuration"));

//      Toast.makeText(this, "max duration is " + maxDuration, Toast.LENGTH_SHORT).show();
        //Video player setup

        videoNameTv.setText(videoName);
        videoView.setVideoURI(Uri.parse(videoPath));

        //Checking permission for camera

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            Toast.makeText(this, "Permission not granted!\n Grant permission and restart app", Toast.LENGTH_SHORT).show();
        }
//        else {
//            init();
//            eyeTrack.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    init();
//                }
//
//            });
//        }
        eyeTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(videoplayer.this, "Eye track activity is clicked", Toast.LENGTH_SHORT).show();
                if(!isEyeTrackingenabled){
                    isEyeTrackingenabled=true;
                    Toast.makeText(videoplayer.this, "Eye Tracking enabled"+isEyeTrackingenabled, Toast.LENGTH_SHORT).show();

                    if(isEyeTrackingenabled){
                        eyeTrack.setImageDrawable(getResources().getDrawable(R.drawable.closeeyes));
                        init();
                    }
                }
                else{
                    eyeTrack.setImageDrawable(getResources().getDrawable(R.drawable.eyetrack));
                    isEyeTrackingenabled=false;
                    Toast.makeText(videoplayer.this, "Eye Tracking Disabled"+isEyeTrackingenabled, Toast.LENGTH_SHORT).show();
                }

            }
        });

        //setting up the video player
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoSeekBar.setMax(videoView.getDuration());
                videoView.start();
            }
        });


//  Setting up the backward video button

        backIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.seekTo(videoView.getCurrentPosition() - 10000);

            }
        });

//  Setting up the forward video button
        forwardIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.seekTo(videoView.getCurrentPosition() + 10000);
            }
        });

//  setting up the default button as a pause

        playpauseIB.setImageDrawable(getResources().getDrawable(R.drawable.pause));


//  changing the play and pause button when the button is clicked
        playpauseIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                    playpauseIB.setImageDrawable(getResources().getDrawable(R.drawable.play));
                } else {
                    videoView.start();
                    playpauseIB.setImageDrawable(getResources().getDrawable(R.drawable.pause));
                }
            }
        });

        videosRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOpen) {
                    hideControl();
                    isOpen = false;
                } else {
                    showControl();
                    isOpen = true;
                }
            }
        });

        // Hinding the System Status bar in android

        decorview = getWindow().getDecorView();
        decorview.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0)
                    decorview.setSystemUiVisibility(hideSystemBars());
            }
        });
        initializeSeekBar();
        setHandler();


    }

    //Setting up the resources

    private void init(){
        flag=true;
        initCameraSource();
    }
    //Method to create camera source from Face Factory Daemon class
    private void initCameraSource(){
        FaceDetector detector = new FaceDetector.Builder(this)
                .setTrackingEnabled(true)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setMode(FaceDetector.FAST_MODE)
                .build();
        detector.setProcessor(new MultiProcessor.Builder(new FaceTrackerDaemon(videoplayer.this)).build());

        cameraSource = new CameraSource.Builder(this, detector)
                .setRequestedPreviewSize(1024, 768)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build();

        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                return;
            }
            cameraSource.start();
        }
        catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    //  onResume class when the activity is resumed the video player is paused
    @Override
    protected void onResume() {
        super.onResume();
        if (cameraSource != null) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                cameraSource.start();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    //    calling the onPause method when the app
    @Override
    protected void onPause() {
        super.onPause();
        if(cameraSource!=null){
            cameraSource.release();
        }
    }

    //    Calling the onDestroy Methdod for this activity to destroy && Bug is there i have to correct this Bug
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if(cameraSource!=null){
//            cameraSource.release();
//        }
//    }

    //  This the main function where i am going execute the function when the method is called
    public void updateMainView(Condition condition) {


        switch (condition) {
            case USER_EYES_OPEN: {
                if(!videoView.isPlaying()) {
                    videoView.resume();
                }
                videoView.start();
                Toast.makeText(this, "Eyes are opened", Toast.LENGTH_SHORT).show();
                Log.i("tag", "updateMainView: Eyes are opened");
                break;
            }
            case USER_EYES_CLOSED: {
                videoView.pause();
                Toast.makeText(this, "Eyes are closed", Toast.LENGTH_SHORT).show();
                Log.i("tag", "updateMainView: Eyes are closed ");
                break;
            }
            case FACE_NOT_FOUND: {
                videoView.pause();
                Toast.makeText(this, "Face are not Found", Toast.LENGTH_SHORT).show();
                Log.i("tag", "updateMainView: Face not Found ");
                break;
            }
            default: {

                Toast.makeText(this, "Default case is handled", Toast.LENGTH_SHORT).show();
                Log.i("tag", "updateMainView: Default function is executed");
                break;
            }
        }
    }

        @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            decorview.setSystemUiVisibility(hideSystemBars());

        }
    }

    public int hideSystemBars() {
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
    }

    private void initializeSeekBar() {
        videoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (videoSeekBar.getId() == R.id.seekbarprogress) {
                    if (fromUser) {
                        videoView.seekTo(progress);
                        videoView.start();
                        int curpos = videoView.getCurrentPosition();
                        videoTimeTv.setText(convertTime(videoView.getDuration() - curpos));
                    }
                }
            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    //    Setting up a seekprogressbar as well as current playing time  in this video player

    private void setHandler() {
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (videoView.getDuration() > 0) {
                    int curpos = videoView.getCurrentPosition();
                    videoSeekBar.setProgress(curpos);
                    videoTimeTv.setText("" + convertTime(videoView.getDuration() - curpos));
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable);
    }

    private String convertTime(int ms) {
        int x, seconds, minutes, hours;
        x = ms / 1000;
        seconds = x % 60;
        x /= 60;
        minutes = x % 60;
        x /= 60;
        hours = x % 24;
        if (hours != 0) {
            time = String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
        } else {
            time = String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
        }
        return time;
    }

    private void showControl() {

        controlsRL.setVisibility(View.VISIBLE);
        final Window window = this.getWindow();
        if (window == null) {
            return;
        }

        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        View decorview = window.getDecorView();

        if (decorview != null) {
            int uiOption = decorview.getSystemUiVisibility();
            if (Build.VERSION.SDK_INT >= 14) {
                uiOption &= ~View.SYSTEM_UI_FLAG_LOW_PROFILE;
            }
            if (Build.VERSION.SDK_INT >= 16) {
                uiOption &= ~View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }
            if (Build.VERSION.SDK_INT >= 19) {
                uiOption &= ~View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }
            decorview.setSystemUiVisibility(uiOption);
        }

    }

    private void hideControl() {

        controlsRL.setVisibility(View.GONE);
        final Window window = this.getWindow();
        if (window == null) {
            return;
        }

        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        View decorview = window.getDecorView();
        if (decorview != null) {
            int uiOption = decorview.getSystemUiVisibility();
            if (Build.VERSION.SDK_INT >= 14) {
                uiOption |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
            }
            if (Build.VERSION.SDK_INT >= 16) {
                uiOption |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }
            if (Build.VERSION.SDK_INT >= 19) {
                uiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }
            decorview.setSystemUiVisibility(uiOption);
        }
    }

public void pause(){
    if (videoView.isPlaying()) {
        videoView.pause();
        pauseposition=videoView.getCurrentPosition();
        playpauseIB.setImageDrawable(getResources().getDrawable(R.drawable.play));
    }
}
public  void play(){
if(!videoView.isPlaying()){
        videoView.resume();
        videoView.seekTo(pauseposition);
        playpauseIB.setImageDrawable(getResources().getDrawable(R.drawable.pause));
    }
}

public void counterTime(){
        Handler handler=new Handler();
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this,5000);

            }
        };
        handler.post(runnable);
}

}

