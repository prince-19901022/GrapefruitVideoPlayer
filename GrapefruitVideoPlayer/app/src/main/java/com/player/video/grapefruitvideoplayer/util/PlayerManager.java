package com.player.video.grapefruitvideoplayer.util;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.player.video.grapefruitvideoplayer.callbacks.OnPlayerReadyListener;

import java.io.IOException;

public class PlayerManager implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        SeekBar.OnSeekBarChangeListener {

    public static final byte STATE_PLAY = 0;
    public static final byte STATE_PAUSE =1;
    public static final byte STATE_STOP = 2;
    
    private AppCompatSeekBar seekBar;
    private TextView durationProgressedTextView;
    private Handler progressHandler;
    private MediaPlayer player;
    private OnPlayerReadyListener readyListener;

    private int lastPlayingPosition;
    private byte lastState;

    private Runnable updateProgress = new Runnable() {
        @Override
        public void run() {
            seekBar.setProgress(player.getCurrentPosition());
            durationProgressedTextView.setText(GPlayerUtil.formatDuration(player.getCurrentPosition()));
            progressHandler.postDelayed(this,1000);
        }
    };

    public PlayerManager(OnPlayerReadyListener readyListener, AppCompatSeekBar seekBar, TextView durationProgressedTextView) {
        this.seekBar = seekBar;
        this.durationProgressedTextView = durationProgressedTextView;
        this.readyListener= readyListener;
        progressHandler = new Handler();
        this.seekBar.setOnSeekBarChangeListener(this);
    }

    public void initPlayer(SurfaceHolder holder, String path) throws IOException {
        player= new MediaPlayer();
        player.setDisplay(holder);
        player.setDataSource(path);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.prepare();
    }

    private void initPlayer(String path) throws IOException {
        player.setDataSource(path);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.prepare();
    }
    
    public void releasePlayer(){
        if(player != null){
            progressHandler.removeCallbacks(updateProgress);
            player.stop();
            player.release();
            player = null;
        }
    }

    public boolean handlePlayPause(){
        if(player.isPlaying()){
            player.pause();
            progressHandler.removeCallbacks(updateProgress);
            lastState= STATE_PAUSE;
        }else {
            player.start();
            progressHandler.post(updateProgress);
            lastState= STATE_PLAY;
        }
        return player.isPlaying();
    }

    public void handleFastForward(int forwardBy){
        int newPos = player.getCurrentPosition() + forwardBy;
        player.seekTo(newPos > player.getDuration()? player.getDuration() : newPos);
    }

    public void handleFastRewind(int rewindBy){
        int newPos = player.getCurrentPosition() - rewindBy;
        player.seekTo(newPos < 0? 0 : newPos);
    }

    public void changeVideoTo(int listIndex) throws IOException {
        progressHandler.removeCallbacks(updateProgress);
        seekBar.setProgress(0);
        durationProgressedTextView.setText("00:00");

        player.stop();
        player.reset();
        initPlayer(SharedDataSource.getInstance().get(listIndex).getFilePath());
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

        seekBar.setMax(player.getDuration());
        seekBar.setProgress(lastPlayingPosition);
        readyListener.onPlayerReady();
        player.seekTo(lastPlayingPosition);

        if(lastState != STATE_PAUSE || lastState != STATE_STOP){
            progressHandler.post(updateProgress);
            player.start();
            lastState= STATE_PLAY;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        // if auto play is on then call change video
        // else do something so that user can press play to restart playing.
        lastState= STATE_STOP;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        progressHandler.removeCallbacks(updateProgress);
        GPlayerUtil.showParticleAnimationOn(seekBar, (Activity) readyListener);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        player.seekTo(seekBar.getProgress());
        progressHandler.post(updateProgress);
        GPlayerUtil.showParticleAnimationOn(seekBar, (Activity) readyListener);
    }

    public boolean isMediaPlayerNull(){
        return player == null;
    }

    /*If player is null, it will produce NPE. Which means this method is invoked from an
    inappropriate scope. So, this method must be called before releasing the player. */
    public int getLastPlayingPosition() {
        return player.getCurrentPosition();
    }

    public void setLastPlayingPosition(int lastPlayingPosition) {
        this.lastPlayingPosition = lastPlayingPosition;
    }

    public byte getLastState() {
        return lastState;
    }

    public void setLastState(byte lastState) {
        this.lastState = lastState;
    }
}
