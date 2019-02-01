package com.player.video.grapefruitvideoplayer.callbacks;

import com.player.video.grapefruitvideoplayer.VideoModel;

public interface VideoItemClickListener {

    void onVideoItemClick(int listIndex, String filePath, long duration, String display);
}
