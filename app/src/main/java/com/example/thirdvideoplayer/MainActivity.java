package com.example.thirdvideoplayer;


import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

//    setting up the preference variable
    public static final String SORTBY="SORT";
    public static final String THRESHOLD="threshold";
    private static final String MY_PREF = "prefs";
    Context context=this;

//   Setting up the arrayAdapter

    RecyclerView recyclerView;
    List<Video> videolist = new ArrayList<> ();

    //Api for taking a permission
    ActivityResultLauncher<String[]> mPermissionResultLauncher;
    private boolean isWritePermissionGranted = false;
    private boolean isInternetPermissionGranted = false;
    private boolean isCameraPermissionGranted = false;


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = findViewById(R.id.rcv);
////      Storing the value of the setting with the help of the shared preferences
//      SharedPreferences preferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
//      SharedPreferences.Editor editor = preferences.edit();


        //Requesting Multiple permission
        mPermissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> result) {
                if (result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) != null) {
                    isWritePermissionGranted = result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                if (result.get(Manifest.permission.INTERNET) != null) {
                    isInternetPermissionGranted = result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                if (result.get(Manifest.permission.CAMERA) != null) {
                    isCameraPermissionGranted = result.get(Manifest.permission.CAMERA);
                }
            }
        });

        requestPermission();

            loadData();
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            videoAdapter adapter = new videoAdapter(this, videolist);
            recyclerView.setAdapter(adapter);
    }
//    Setting up the AppBar layout as well as application toolbar in my app


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences preferences=getSharedPreferences(MY_PREF,MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        int id= item.getItemId();
        switch (id){
            case R.id.aboutUs:
            {
                Intent i=new Intent(MainActivity.this,aboutus.class);
                context.startActivity(i);
                return true;
            }
            case R.id.Sort:
            {
                AlertDialog.Builder alertDialog=new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Sort BY");
                alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(context, "All the data is successfully modified", Toast.LENGTH_SHORT).show();

                        finish();
                         startActivity(getIntent());
                         dialog.dismiss();
                    }
                });
                String[] items={"Name (ASC)","Name (By DESC)","Size(Big to small","Date (New to Old"};
                alertDialog.setSingleChoiceItems(items, -2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                     switch (which){
                         case 0:
                             editor.putString("sortBy","ASC");
                             editor.apply();
                             Toast.makeText(context, "Dialog sort by "+preferences.getString("sortBy",""), Toast.LENGTH_SHORT).show();

                             break;
                         case 1:
                             editor.putString("sortBy","DESC");
                             editor.apply();
                             Toast.makeText(context, "Dialog sort by "+preferences.getString("sortBy",""), Toast.LENGTH_SHORT).show();
                              break;
                         case 2:
                             editor.putString("sortBy","DATE");
                             editor.apply();
                             Toast.makeText(context, "Dialog sorts by "+preferences.getString("sortBy",""), Toast.LENGTH_SHORT).show();
                             break;
                         case 3:
                             editor.putString("sortBy","DATE");
                             editor.apply();
                             Toast.makeText(context, "Dialog sort by "+preferences.getString("sortBy",""), Toast.LENGTH_SHORT).show();
                             break;

                     }
                    }
                });
                alertDialog.create().show();
//                Toast.makeText(this, "Sort is clicked", Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.setting:
            {
                Intent intent=new Intent(this,Setting.class);
                startActivity(intent);
                Toast.makeText(this, "Setting is clicked", Toast.LENGTH_SHORT).show();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    //    loading the data of the external storage from here
//    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void loadData(){
//Getting a values from shared preferences
        SharedPreferences preferences=getSharedPreferences(MY_PREF,MODE_PRIVATE);
//        SharedPreferences.Editor editor=preferences.edit();
        String sortvalue=preferences.getString("sortBy","");
        Toast.makeText(context, "sortValue "+sortvalue, Toast.LENGTH_SHORT).show();
        //ContentResolver and contentProvider as well as cursor
        String[] projection = new String[]{
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATE_MODIFIED,
                MediaStore.Audio.Media.DURATION
        };

        String selection = null;
        String[] selectionargs = null;
        String orderBy;
        if(sortvalue=="ASC") {
            orderBy = MediaStore.Video.Media.DISPLAY_NAME + " ASC";
        }
        else if(sortvalue=="DESC"){
      orderBy = MediaStore.Video.Media.DISPLAY_NAME + " DESC";

        }
        else if(sortvalue=="DATE"){
             orderBy = MediaStore.Video.Media.DATE_MODIFIED + " ASC";
        }
        else{
            orderBy = MediaStore.Video.Media.SIZE + " ASC";

        }
        Uri content_uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = getContentResolver().query(content_uri, projection, selection, selectionargs, orderBy);
        if (cursor != null) {
            cursor.moveToPosition(0);
        }
        while (true) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
            Uri VideoUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
            Log.d("uri", "onCreate: video path " + VideoUri);
            String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
            long size = (long) cursor.getFloat(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
            int maxDuration=cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
            String date=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED));

            // Loading a thumbnail from the content resolver
            // Load thumbnail of a specific media item.

            Bitmap thumbnail = null;
            try {
                thumbnail = getApplicationContext().getContentResolver().loadThumbnail(VideoUri, new Size(200, 200), null);
                Log.d("thumbnail", "onCreate: Lodaing a thumbnail");
            } catch (IOException e) {
                Log.d("thumbnail", "onCreate: Showing Error on thumbnail");
                e.printStackTrace();
            }
            videolist.add(new Video(thumbnail, VideoUri, title, size,maxDuration,date));
            if (!cursor.isLast()) {
                cursor.moveToNext();
            } else {
                Log.d("lastItem", "onCreate: last uri is encountered");
                break;
            }
        }
        cursor.close();
    }

//requesting for permisssion list
    private void requestPermission() {
        isInternetPermissionGranted = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        isWritePermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        isCameraPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        List<String> PermissionRequest = new ArrayList<>();
        if (!isWritePermissionGranted) {
            PermissionRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!isInternetPermissionGranted) {
            PermissionRequest.add(Manifest.permission.INTERNET);
        }
        if (!isCameraPermissionGranted) {
            PermissionRequest.add(Manifest.permission.CAMERA);
        }
        if (!PermissionRequest.isEmpty()) {
            mPermissionResultLauncher.launch(PermissionRequest.toArray(new String[0]));
        }
    }
//Implementing the setting option here in this section in android studio
    public void userSettings(){
        SharedPreferences preferences=getSharedPreferences("com.example.thirdvideoplayer.pref",MODE_PRIVATE);
        SharedPreferences.Editor myedit=preferences.edit();
        myedit.putString("SortBy","ASC");
        myedit.putFloat("eyeThreshold",0.90F);
        myedit.putString("search","");


    }

    @Override
    public void onBackPressed() {
        Toast.makeText(context, "backpressed button is pressed", Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        MainActivity.this.onSuperBackPressed();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    public void onSuperBackPressed(){
        super.onBackPressed();
    }


}