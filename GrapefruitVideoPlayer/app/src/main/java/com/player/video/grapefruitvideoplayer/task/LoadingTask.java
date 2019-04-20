package com.player.video.grapefruitvideoplayer.task;

import android.app.Application;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.player.video.grapefruitvideoplayer.util.VideoModel;
import com.player.video.grapefruitvideoplayer.database.Thumbnail;
import com.player.video.grapefruitvideoplayer.database.ThumbnailDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadingTask implements Runnable{

    private boolean isContentProviderChangeEvent;
    private GPlayerViewModel viewModel;
    private Application application;

    //public access of this field will facilitate registering content observer
    public static final Uri CONTENT_URI = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

    private static final String[] VIDEO_PROJECTION = new String[] {
            MediaStore.Video.Media.DATA, //file path string
            MediaStore.Video.Media.DURATION, // duration in millisecond
            MediaStore.Video.Media.TITLE, // Title of the file
            MediaStore.Video.Media.SIZE, //Size of the file in bytes
            MediaStore.Video.Media.DISPLAY_NAME, //Title of the file with type extension
            MediaStore.Video.Media._ID
    };

    public LoadingTask(boolean isContentProviderChangeEvent, GPlayerViewModel viewModel, Application application) {
        this.isContentProviderChangeEvent = isContentProviderChangeEvent;
        this.viewModel = viewModel;
        this.application = application;
    }

    private List<VideoModel> transformIntoList(Cursor cursor){
        List<VideoModel> list = new ArrayList<>();

        if (cursor != null && cursor.getCount() > 0){

            cursor.moveToFirst();
            VideoModel model;

            while (!cursor.isAfterLast()){

                model = new VideoModel();
                model.setTitle(cursor.getString(2));
                model.setDurationInMilliSecond(cursor.getLong(1));
                model.setFilePath(cursor.getString(0));
                model.setFileSizeInBytes(cursor.getLong(3));
                model.setDisplayName(cursor.getString(4));
                model.setVideoId(cursor.getLong(5));
                list.add(model);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return list;
    }

    //Consider Using sparse array.
    private List<VideoModel> transformIntoList(Cursor cursor, Map<Long,Integer> videoIdMap){

        List<VideoModel> list = new ArrayList<>();

        if (cursor != null && cursor.getCount() > 0){

            cursor.moveToFirst();
            VideoModel model;

            while (!cursor.isAfterLast()){

                model = new VideoModel();
                model.setTitle(cursor.getString(2));
                model.setDurationInMilliSecond(cursor.getLong(1));
                model.setFilePath(cursor.getString(0));
                model.setFileSizeInBytes(cursor.getLong(3));
                model.setDisplayName(cursor.getString(4));
                model.setVideoId(cursor.getLong(5));
                list.add(model);

                videoIdMap.put(new Long(model.getVideoId()), new Integer(list.size()-1));

                cursor.moveToNext();
            }
        }
        cursor.close();
        return list;
    }

    @Override
    public void run() {

        //Cursor need not to be closed here because, it will be closed either one of the transport method.
        Log.d("www.d.com","Querying database");
        Cursor cursor = application.getContentResolver().query(CONTENT_URI,
                VIDEO_PROJECTION,
                null,
                null,
                null);
        List<VideoModel> videoList;
        if(isContentProviderChangeEvent){
            videoList =  transformIntoList(cursor);
            viewModel.setVideoList(videoList);
        }else{

            Map<Long, Integer> videoIdMap = new HashMap<>();
            videoList = transformIntoList(cursor, videoIdMap);

            List<Thumbnail> thumbnailList = ThumbnailDatabase
                    .getInstance(application.getApplicationContext())
                    .thumbnailDao()
                    .loadAllThumbnail();

            for (Thumbnail thumbnail : thumbnailList){

                Long videoId = new Long(thumbnail.getVideoId());
                Integer index = videoIdMap.get(videoId);

                if( index!= null){
                    videoList.get(index.intValue()).setFrameTimeInMicrosecond(thumbnail.getFrameAtMicroSecond());
                    videoIdMap.remove(videoId);
                }
            }
            viewModel.setVideoList(videoList);
            if(!videoIdMap.isEmpty()){
                //TODO : Remove records from thumbnail database where database id matches map id.
            }
        }
    }
}
