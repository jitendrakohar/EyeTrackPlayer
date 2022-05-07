package com.example.thirdvideoplayer;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class videoAdapter extends RecyclerView.Adapter<videoAdapter.viewHolder> {
  List<Video> videoList=new ArrayList<>();
  Context context;

    public videoAdapter(Context context,List<Video> videoList) {
        this.videoList = videoList;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.listview,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
         holder.title.setText(videoList.get(position).getTitle());
         if(videoList.get(position).getSize()/(1024*1024)==0) {
             holder.size.setText(new String(String.valueOf(videoList.get(position).getSize()/(1024)+"KB")));
         }else
         holder.size.setText(new String(String.valueOf(videoList.get(position).getSize()/(1024*1024)+"MB")));
         holder.thumbnail.setImageBitmap(videoList.get(position).getThumbnail());

         holder.itemView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Toast.makeText(context, "ON video is clicked "+videoList.get(position).getDate(), Toast.LENGTH_SHORT).show();
                 Intent i=new Intent(context,videoplayer.class);
//                 Toast.makeText(context, "the URI is "+videoList.get(position).getVideoUri(), Toast.LENGTH_SHORT).show();
                 i.putExtra("title",videoList.get(position).getTitle());
                 i.putExtra("uri",(videoList.get(position).getVideoUri()).toString());

                 i.putExtra("maxDuration",videoList.get(position).getMaxDuration()+"");
                 context.startActivity(i);
             }
         });


    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }


    public class viewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView size;
        ImageView thumbnail;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
        title=itemView.findViewById(R.id.title);
        size=itemView.findViewById(R.id.size);
        thumbnail=itemView.findViewById(R.id.idIIView);

        }
    }
}
