package com.player.video.grapefruitvideoplayer;

/*
* List all video or audio file :- https://stackoverflow.com/questions/39461954/list-all-mp3-files-in-android
* Video content provider :- https://developer.android.com/reference/android/provider/MediaStore.Video*/

 // Thunmbnail loading will be done later.

public class VideoModel {

    private String filePath;
    //private String thumbnailPath;
    private String title;
    private long fileSizeInBytes;
    private long durationInMilliSecond;
    private String displayName; //It Will contain the file type

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

//    public String getThumbnailPath() {
//        return thumbnailPath;
//    }
//
//    public void setThumbnailPath(String thumbnailPath) {
//        this.thumbnailPath = thumbnailPath;
//    }

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

    public String getDuration() {
        //milliSecondInAnHour = 60 * 60 * 1000 = 36 00 000
        long hour = durationInMilliSecond / 3600000;
        long minute = durationInMilliSecond % 3600000;
        //millisecondInA?Minute = 60,000
        minute = minute/60000;
        long second = minute % 60000;
        second = second / 1000;
        return String.format("Duration %02d : %02d : %02d", hour, minute, second);
    }

    public void setDurationInMilliSecond(long durationInMilliSecond) {
        this.durationInMilliSecond = durationInMilliSecond;
    }

    public String getFormat(){
        return "Type of file "+displayName.substring(displayName.lastIndexOf("."));
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
