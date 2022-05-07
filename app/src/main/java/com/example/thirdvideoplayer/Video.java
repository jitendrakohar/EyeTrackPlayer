package com.example.thirdvideoplayer;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Video {

    Uri videoUri;
    float size;
    String title;
    Bitmap thumbnail;
    int MaxDuration;
    String date;
    public Video(Bitmap thumbnail,Uri videoUri, String title, long size,int maxDuration,String date) {
        this.thumbnail=thumbnail;
        this.date=date;
        this.videoUri = videoUri;
        this.MaxDuration=maxDuration;
        this.size = size;
        this.title = title;
    }

    public Uri getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(Uri videoUri) {
        this.videoUri = videoUri;
    }

    public long getSize() {
        return  (long)size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getMaxDuration() {
        return MaxDuration;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setMaxDuration(int maxDuration) {
        MaxDuration = maxDuration;
    }
}
