package com.example.thirdvideoplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Modifier;

public class Setting extends AppCompatActivity {
    TextView thresholdvalue;
    LinearLayout LLaboutus;
    ImageButton linkedin,twitter,github,gmail;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
       thresholdvalue=findViewById(R.id.thresholdValue);
        LLaboutus=findViewById(R.id.LLAboutus);
       linkedin=findViewById(R.id.linkdin);
       twitter=findViewById(R.id.twitter);
       github=findViewById(R.id.github);
       gmail=findViewById(R.id.gmail);
       LLaboutus.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent i=new Intent(Setting.this,aboutus.class);
               context.startActivity(i);
           }
       });
       linkedin.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(Intent.ACTION_VIEW,
                       Uri.parse("https://linkedin.com/in/jitendra-kohar-1345ba1a3/")));
           }
       });
       github.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://github.com/jitendrakohar")));
           }
       });
       twitter.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://twitter.com/JitendraKohar34")));
           }
       });
       gmail.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               // ACTION_SENDTO filters for email apps (discard bluetooth and others)
               String uriText =
                       "mailto:jitendrakohar05@gmail.com" +
                               "?subject=" + Uri.encode("some subject text here") +
                               "&body=" + Uri.encode("some text here");

               Uri uri = Uri.parse(uriText);

               Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
               sendIntent.setData(uri);
               startActivity(Intent.createChooser(sendIntent, "Send email"));
                }
       });

        SharedPreferences preferences= getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        thresholdvalue.setText(""+preferences.getFloat("threshold",0));
 thresholdvalue.setOnClickListener(new View.OnClickListener() {
     @Override
     public void onClick(View v) {
         Toast.makeText(Setting.this, "item is clicked", Toast.LENGTH_SHORT).show();
         PopupMenu popupMenu=new PopupMenu(Setting.this,thresholdvalue);
         popupMenu.getMenuInflater().inflate(R.menu.popupmenu,popupMenu.getMenu());
         popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
             @Override
             public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.verysmall:
                        editor.putFloat("threshold", 0.30F);
                        editor.apply();
                        thresholdvalue.setText(""+preferences.getFloat("threshold",0));
                        Toast.makeText(Setting.this, "Threshold value is "+preferences.getFloat("threshold",0), Toast.LENGTH_SHORT).show();

                        return true;
                    case R.id.smalleye:
                        editor.putFloat("threshold", 0.5F);
                        editor.apply();
                        thresholdvalue.setText(""+preferences.getFloat("threshold",0));
                        Toast.makeText(Setting.this, "Threshold value is "+preferences.getFloat("threshold",0), Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.mediumeye:
                        editor.putFloat("threshold",0.70f);
                        editor.apply();
                        thresholdvalue.setText(""+preferences.getFloat("threshold",0));
                        Toast.makeText(Setting.this, "Threshold value is "+preferences.getFloat("threshold",0), Toast.LENGTH_SHORT).show();
                        return true;
                }
                 return false;
             }
         });
         popupMenu.show();
     }
 }); }
}