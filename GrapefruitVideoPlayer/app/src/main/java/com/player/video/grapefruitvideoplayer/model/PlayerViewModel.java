package com.player.video.grapefruitvideoplayer.model;

import android.arch.lifecycle.ViewModel;

public class PlayerViewModel extends ViewModel {

    private int lastPlayingPosition = 0;
    private byte lastPlayingState = -1;

    public int getLastPlayingPosition() {
        return lastPlayingPosition;
    }

    public void setLastPlayingPosition(int lastPlayingPosition) {
        this.lastPlayingPosition = lastPlayingPosition;
    }

    public byte getLastPlayingState() {
        return lastPlayingState;
    }

    public void setLastPlayingState(byte lastPlayingState) {
        this.lastPlayingState = lastPlayingState;
    }
}
