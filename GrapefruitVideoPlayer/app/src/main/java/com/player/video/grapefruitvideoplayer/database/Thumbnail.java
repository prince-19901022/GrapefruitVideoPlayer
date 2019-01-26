package com.player.video.grapefruitvideoplayer.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "thumbnail")
public class Thumbnail {

    @PrimaryKey
    @ColumnInfo(name = "video_id")
    private long videoId;

    @ColumnInfo(name = "frame_at_micro_second")
    private long frameAtMicroSecond; //Will be used as thumbnail

    @Ignore
    public Thumbnail(){

    }

    public Thumbnail(long videoId, long frameAtMicroSecond) {
        this.videoId = videoId;
        this.frameAtMicroSecond = frameAtMicroSecond;
    }

    public long getVideoId() {
        return videoId;
    }

    public void setVideoId(long videoId) {
        this.videoId = videoId;
    }

    public long getFrameAtMicroSecond() {
        return frameAtMicroSecond;
    }

    public void setFrameAtMicroSecond(long frameAtMicroSecond) {
        this.frameAtMicroSecond = frameAtMicroSecond;
    }
}
