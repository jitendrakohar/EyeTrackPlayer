package com.example.thirdvideoplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class splashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
   new Handler().postDelayed(new Runnable() {
       @Override
       public void run(){
           startActivity(new Intent(splashActivity.this,MainActivity.class));
           finish();
       }
   },2000); }
}