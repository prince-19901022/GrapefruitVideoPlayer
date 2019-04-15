package com.player.video.grapefruitvideoplayer;

import android.os.Handler;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;

import java.util.concurrent.TimeUnit;

public class GPlayerEventListener extends Player.DefaultEventListener implements SeekBar.OnSeekBarChangeListener{

    private AppCompatSeekBar seekBar;
    private TextView durationProgressedTextView;
    private Handler progressHandler;
    private SimpleExoPlayer player;


    private Runnable updateProgress = new Runnable() {
        @Override
        public void run() {
            seekBar.setProgress((int)player.getCurrentPosition());
            durationProgressedTextView.setText(GPlayerUtil.formatDuration(player.getCurrentPosition()));
            progressHandler.postDelayed(this,1000);
        }
    };


    public GPlayerEventListener(AppCompatSeekBar seekBar, TextView durationProgressedTextView) {
        this.seekBar = seekBar;
        this.durationProgressedTextView = durationProgressedTextView;
        this.seekBar.setOnSeekBarChangeListener(this);
        progressHandler = new Handler();
    }

    public void setPlayer(SimpleExoPlayer player) {
        this.player = player;

        /* If player is not ready then getDuration will return a long negative integer.
           Eventually it means seekBar max is not valid and requires to set again.*/
        seekBar.setMax(0);
    }

    private void handleProgress(boolean playWhenReady){

        if(seekBar.getMax() == 0){
            /*If a video length is 24 hrs( which is impractical) then the total number of milli seconds in
             * video length is 86400000. Which will not cause overflow for integer variable. So, the type
             * conversion and setting the max to video length in seconds are expected to be safe.*/
            seekBar.setMax((int)player.getDuration());
        }
        if(playWhenReady){
            progressHandler.post(updateProgress);
        }else{
            progressHandler.removeCallbacks(updateProgress);
        }
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        super.onPlayerStateChanged(playWhenReady, playbackState);

        switch (playbackState){
            case Player.STATE_READY:
                handleProgress(playWhenReady);
                break;

            case Player.STATE_ENDED:
                progressHandler.removeCallbacks(updateProgress);
                break;
        }
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //When user has just touched the seek bar scrubber. Callback should be removed here.
        player.setPlayWhenReady(false);
        progressHandler.removeCallbacks(updateProgress);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //When user has left seek bar scrubber. Callback should be added here.

        player.seekTo((long)seekBar.getProgress());
        player.setPlayWhenReady(true);
        progressHandler.post(updateProgress);
    }
}
