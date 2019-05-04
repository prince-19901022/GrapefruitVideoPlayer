package com.player.video.grapefruitvideoplayer.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.player.video.grapefruitvideoplayer.R;
import com.player.video.grapefruitvideoplayer.callbacks.OnPlayerReadyListener;
import com.player.video.grapefruitvideoplayer.callbacks.SeekTimeSelectionListener;
import com.player.video.grapefruitvideoplayer.util.PlayerManager;
import com.player.video.grapefruitvideoplayer.util.GPlayerUtil;
import com.player.video.grapefruitvideoplayer.util.SharedDataSource;

import java.io.IOException;

public class VideoPlayerActivity extends AppCompatActivity implements View.OnClickListener, 
        SurfaceHolder.Callback,
        OnPlayerReadyListener {

    private ImageButton nextImageButton;
    private ImageButton previousImageButton;
    private ImageButton fastForwardImageButton;
    private ImageButton fastRewindImageButton;
    private ImageButton playPauseImageButton;

    private SurfaceView surfaceView;
    private PlayerManager playerManager;

    private int seekBy;
    private int listIndex;
    private boolean isSurfaceCreated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listIndex= getIntent().getExtras().getInt(getString(R.string.list_index));

        getSupportActionBar().setTitle(SharedDataSource.getInstance().get(listIndex).getTitle());
        initialise();
        disablePlayerControls();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_item_settings :
                GPlayerUtil.showSettingsDialog(this,
                        new SeekTimeSelectionListener() {
                            @Override
                            public void onSeekTimeSelected(int itemIndex) {
                                seekBy= getResources().getIntArray(R.array.milliseconds)[itemIndex];
                                SharedPreferences pref= VideoPlayerActivity.this.getPreferences(Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor= pref.edit();
                                editor.putInt(getString(R.string.seek_time_id), itemIndex);
                                editor.apply();
                            }
                        });
                return true;

            case android.R.id.home :
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.menu_item_select_as_thumbnail:
                Toast.makeText(this,"Feature will be added soon", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initialise(){
        surfaceView = findViewById(R.id.surfaceView);
        nextImageButton = findViewById(R.id.btn_next);
        previousImageButton = findViewById(R.id.btn_prev);

        fastForwardImageButton = findViewById(R.id.btn_fast_forward);
        fastRewindImageButton = findViewById(R.id.btn_fast_rewind);

        playPauseImageButton = findViewById(R.id.btn_play_pause);

        final TextView totalDurationTextView = findViewById(R.id.tv_total_progress);
        totalDurationTextView.setText(GPlayerUtil.formatDuration(SharedDataSource.getInstance().
                get(listIndex).
                getDurationInMilliSecond()));

        surfaceView.getHolder().addCallback(this);

        playerManager= new PlayerManager(this,
                (AppCompatSeekBar) findViewById(R.id.seek_bar_portrait), 
                (TextView) findViewById(R.id.tv_progress_time));

        nextImageButton.setOnClickListener(this);
        previousImageButton.setOnClickListener(this);
        fastForwardImageButton.setOnClickListener(this);
        fastRewindImageButton.setOnClickListener(this);
        playPauseImageButton.setOnClickListener(this);

        seekBy= getResources().getIntArray(R.array.milliseconds)[
                getPreferences(Context.MODE_PRIVATE).
                        getInt(getString(R.string.seek_time_id), 0)];
    }

    private void disablePlayerControls(){
        fastRewindImageButton.setVisibility(View.INVISIBLE);
        fastForwardImageButton.setVisibility(View.INVISIBLE);
        nextImageButton.setVisibility(View.INVISIBLE);
        previousImageButton.setVisibility(View.INVISIBLE);
    }

    private void enablePlayerControls(){
        fastRewindImageButton.setVisibility(View.VISIBLE);
        fastForwardImageButton.setVisibility(View.VISIBLE);
        nextImageButton.setVisibility(View.VISIBLE);
        previousImageButton.setVisibility(View.VISIBLE);
    }

    private void initPlayer(SurfaceHolder holder){
        try{
            playerManager.initPlayer(holder, SharedDataSource.getInstance().get(listIndex).getFilePath());
            isSurfaceCreated = true;
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    private void changeCurrentVideo(){
        getSupportActionBar().setTitle(SharedDataSource.getInstance().get(listIndex).getTitle());
        try {
            playerManager.changeVideoTo(listIndex);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Unable to play this video", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.btn_play_pause :
                GPlayerUtil.showParticleAnimationOn(playPauseImageButton, this);
                onClickPlayPause();
                break;

            case R.id.btn_fast_forward :
                GPlayerUtil.showParticleAnimationOn(fastForwardImageButton, this);
                playerManager.handleFastForward(seekBy);
                break;

            case R.id.btn_fast_rewind :
                GPlayerUtil.showParticleAnimationOn(fastRewindImageButton, this);
                playerManager.handleFastRewind(seekBy);
                break;

            case R.id.btn_next:
                GPlayerUtil.showParticleAnimationOn(nextImageButton, this);
                listIndex = (listIndex + 1) == SharedDataSource.getInstance().dataSourceSize() ?
                        0 : (listIndex + 1);
                changeCurrentVideo();
                break;

            case R.id.btn_prev:
                GPlayerUtil.showParticleAnimationOn(previousImageButton, this);
                listIndex = (listIndex - 1) < 0 ?
                        SharedDataSource.getInstance().dataSourceSize() - 1 :
                        (listIndex - 1);
                changeCurrentVideo();
                break;
        }
    }

    private void onClickPlayPause() {
        boolean isPlaying= playerManager.handlePlayPause();
        if(isPlaying){
            playPauseImageButton.setImageResource(R.drawable.ic_pause);
        }else{
            playPauseImageButton.setImageResource(R.drawable.ic_play);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isSurfaceCreated && playerManager.isMediaPlayerNull()){
            initPlayer(surfaceView.getHolder());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(Build.VERSION.SDK_INT <= 23){
            playerManager.releasePlayer();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(Build.VERSION.SDK_INT > 23){
            playerManager.releasePlayer();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        initPlayer(holder);
        isSurfaceCreated= true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isSurfaceCreated= false;
    }

    @Override
    public void onPlayerReady() {
        enablePlayerControls();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("lastPlayingPosition", playerManager.getLastPlayingPosition());
        outState.putByte("lastState",playerManager.getLastState());

        Log.d("www.d.com","saving state");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        playerManager.setLastPlayingPosition(savedInstanceState.getInt("lastPlayingPosition"));
        playerManager.setLastState(savedInstanceState.getByte("lastState"));

        switch (playerManager.getLastState()) {

            case PlayerManager.STATE_PAUSE:
                playPauseImageButton.setImageResource(R.drawable.ic_pause);
                break;
            case PlayerManager.STATE_PLAY:
                playPauseImageButton.setImageResource(R.drawable.ic_play);
                break;
        }
    }
}
