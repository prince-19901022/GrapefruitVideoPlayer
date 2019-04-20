package com.player.video.grapefruitvideoplayer.util;

/*
* List all video or audio file :- https://stackoverflow.com/questions/39461954/list-all-mp3-files-in-android
* Video content provider :- https://developer.android.com/reference/android/provider/MediaStore.Video*/

 // Thunmbnail loading will be done later.

import com.bumptech.glide.request.RequestOptions;

public class VideoModel {

    private String filePath;
    private String title;
    private long fileSizeInBytes;
    private long durationInMilliSecond;
    private String displayName; //It Will contain the file type
    //The micro second of which frame is intended to be used as thumbnail.
    private long frameTimeInMicrosecond = 6000000;

    private long videoId;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileSize() {
        // bytesInOneMB = 1048576
        return String.format("Size %1.2f MB", fileSizeInBytes/1048576.0);
    }

    public void setFileSizeInBytes(long fileSizeInBytes) {
        this.fileSizeInBytes = fileSizeInBytes;
    }

    public long getDurationInMilliSecond(){
        return durationInMilliSecond;
    }

    public void setDurationInMilliSecond(long durationInMilliSecond) {
        this.durationInMilliSecond = durationInMilliSecond;
    }

    public String getFormat(){
        return "Type "+displayName.substring(displayName.lastIndexOf("."));
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setFrameTimeInMicrosecond(long frameTimeInMicrosecond) {
        this.frameTimeInMicrosecond = frameTimeInMicrosecond;
    }

    public RequestOptions getRequestOption(){
        return new RequestOptions().frame(frameTimeInMicrosecond);
    }

    public long getVideoId() {
        return videoId;
    }

    public void setVideoId(long videoId) {
        this.videoId = videoId;
    }
}
