package com.player.video.grapefruitvideoplayer;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.player.video.grapefruitvideoplayer.database.Thumbnail;
import com.player.video.grapefruitvideoplayer.database.ThumbnailDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoListAdapter  extends RecyclerView.Adapter<VideoListAdapter.VideoViewHolder>{

    private Context context;
    private List<VideoModel> videoModels;

    public VideoListAdapter(Context context) {
        this.context = context;
        videoModels = new ArrayList<>();
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new VideoViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.video_list_item,
                        parent,
                        false));
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        holder.bindDataToView(videoModels.get(position));
    }

    @Override
    public int getItemCount() {
        return videoModels.size();
    }

    public void addData(List<VideoModel> videoModels){
        this.videoModels = videoModels;
        notifyDataSetChanged();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder{

        private ImageView thumbnailImageView;
        private TextView titleTextView;
        private TextView sizeTextView;
        private TextView formatTextView;
        private TextView durationTextView;


        public VideoViewHolder(View itemView) {
            super(itemView);

            thumbnailImageView = itemView.findViewById(R.id.iv_thumbnail);
            sizeTextView =itemView.findViewById(R.id.tv_size);
            formatTextView =itemView.findViewById(R.id.tv_format);
            durationTextView =itemView.findViewById(R.id.tv_duration);
            titleTextView =itemView.findViewById(R.id.tv_title);
        }

        public void bindDataToView(VideoModel model){
            //Thumbnail should set with glide.
            Glide.with(context).load(Uri.fromFile(new File(model.getFilePath())))
                    .apply(model.getRequestOption())
                    .into(thumbnailImageView);

            sizeTextView.setText(model.getFileSize());
            formatTextView.setText(model.getFormat());
            durationTextView.setText(model.getDuration());
            titleTextView.setText(model.getTitle());
        }
    }
}
