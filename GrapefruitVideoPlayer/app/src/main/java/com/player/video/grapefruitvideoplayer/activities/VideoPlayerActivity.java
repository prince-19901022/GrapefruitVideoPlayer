package com.player.video.grapefruitvideoplayer.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.transition.TransitionManager;
import android.util.TypedValue;
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
import com.player.video.grapefruitvideoplayer.model.PlayerViewModel;
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
    private ImageButton closeImageButton;
    private View landScapeControlView;
    private TextView timeElapsedTextView;
    private TextView totalDurationTextView;
    private AppCompatSeekBar seekBar;

    private SurfaceView surfaceView;
    private PlayerManager playerManager;

    private int seekBy;
    private int listIndex;
    private boolean isSurfaceCreated = false;
    
    private PlayerViewModel playerViewModel;
    private ConstraintSet portraitSet;
    private ConstraintSet landscapeSet;
    private ConstraintLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listIndex= getIntent().getExtras().getInt(getString(R.string.list_index));
        playerViewModel= ViewModelProviders.of(this).get(PlayerViewModel.class);

        getSupportActionBar().setTitle(SharedDataSource.getInstance().get(listIndex).getTitle());
        initialise();
        disablePlayerControls();

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            configLayoutForLandscapeMode();
        }
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
        rootLayout = findViewById(R.id.cl_layout_root);
        landScapeControlView= findViewById(R.id.control_view_landscape);
        surfaceView = findViewById(R.id.surfaceView);
        nextImageButton = findViewById(R.id.btn_next);
        previousImageButton = findViewById(R.id.btn_prev);
        closeImageButton= findViewById(R.id.btn_close);

        fastForwardImageButton = findViewById(R.id.btn_fast_forward);
        fastRewindImageButton = findViewById(R.id.btn_fast_rewind);

        playPauseImageButton = findViewById(R.id.btn_play_pause);
        totalDurationTextView = findViewById(R.id.tv_total_duration);
        timeElapsedTextView= findViewById(R.id.tv_time_elapsed);
        seekBar= findViewById(R.id.seek_bar_portrait);
        totalDurationTextView.setText(GPlayerUtil.formatDuration(SharedDataSource.getInstance().
                get(listIndex).
                getDurationInMilliSecond()));

        surfaceView.getHolder().addCallback(this);

        playerManager= new PlayerManager(this, seekBar, timeElapsedTextView);

        nextImageButton.setOnClickListener(this);
        previousImageButton.setOnClickListener(this);
        fastForwardImageButton.setOnClickListener(this);
        fastRewindImageButton.setOnClickListener(this);
        playPauseImageButton.setOnClickListener(this);
        closeImageButton.setOnClickListener(this);

        seekBy= getResources().getIntArray(R.array.milliseconds)[
                getPreferences(Context.MODE_PRIVATE).
                        getInt(getString(R.string.seek_time_id), 0)];

        portraitSet= new ConstraintSet();
        portraitSet.clone(rootLayout);

        landscapeSet= new ConstraintSet();
        landscapeSet.clone(rootLayout);
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
        landScapeControlView.setVisibility(View.VISIBLE);
    }

    private void initPlayer(SurfaceHolder holder){
        try{
            playerManager.setLastPlayingPosition(playerViewModel.getLastPlayingPosition());
            playerManager.setLastState(playerViewModel.getLastPlayingState());
            playerManager.initPlayer(holder, SharedDataSource.getInstance().get(listIndex).getFilePath());
            isSurfaceCreated = true;
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    private void releasePlayer(){
        playerViewModel.setLastPlayingPosition(playerManager.getLastPlayingPosition());
        playerViewModel.setLastPlayingState(playerManager.getLastState());
        playerManager.releasePlayer();
    }

    private void changeCurrentVideo(){
        getSupportActionBar().setTitle(SharedDataSource.getInstance().get(listIndex).getTitle());
        try {
            playerManager.setLastState((byte) -1);
            playerManager.setLastPlayingPosition(0);
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
            case R.id.surfaceView:
                showControlView();
                break;
            case R.id.btn_close:
                closeControlView();
                Toast.makeText(this, "Close button is working", Toast.LENGTH_SHORT).show();
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

    private void configLayoutForLandscapeMode(){
        //This will hide action bar and status bar and make the activity fullscreen.
        getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
//Expanding video to full screen.
        landscapeSet.setDimensionRatio(surfaceView.getId(), "0:0");
        landscapeSet.applyTo(rootLayout);
    }

    private void configLayoutForPortraitMode(){
        //This will show system ui means action bar and status bar.
        getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
        TransitionManager.beginDelayedTransition(rootLayout);
        portraitSet.setDimensionRatio(surfaceView.getId(), "4:3");
        portraitSet.applyTo(rootLayout);
    }

    private void showControlView(){
        //Expanding control view to the full screen.
        TransitionManager.beginDelayedTransition(rootLayout);
        landscapeSet.connect(landScapeControlView.getId(),
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END,
                0);

        landscapeSet.connect(landScapeControlView.getId(),
                ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID,
                ConstraintSet.BOTTOM,
                0);

//Positioning prev button
        landscapeSet.clear(previousImageButton.getId(), ConstraintSet.START);
        landscapeSet.clear(previousImageButton.getId(), ConstraintSet.TOP);
        landscapeSet.clear(previousImageButton.getId(), ConstraintSet.BOTTOM);

        landscapeSet.connect(previousImageButton.getId(),
                ConstraintSet.START,
                landScapeControlView.getId(),
                ConstraintSet.START,
                0);

        landscapeSet.connect(previousImageButton.getId(),
                ConstraintSet.TOP,
                landScapeControlView.getId(),
                ConstraintSet.TOP,
                dpToPx(8.0f));
        landscapeSet.setVisibility(previousImageButton.getId(), ConstraintSet.VISIBLE);
// Positioning close button 
        landscapeSet.clear(closeImageButton.getId(), ConstraintSet.END);
        landscapeSet.clear(closeImageButton.getId(), ConstraintSet.TOP);
        landscapeSet.clear(closeImageButton.getId(), ConstraintSet.BOTTOM);

        landscapeSet.connect(closeImageButton.getId(),
                ConstraintSet.END,
                landScapeControlView.getId(),
                ConstraintSet.END,
                0);

        landscapeSet.connect(closeImageButton.getId(),
                ConstraintSet.TOP,
                landScapeControlView.getId(),
                ConstraintSet.TOP,
                dpToPx(8.0f));

        landscapeSet.setVisibility(closeImageButton.getId(), ConstraintSet.VISIBLE);
        
//Positioning next button
        landscapeSet.clear(nextImageButton.getId(), ConstraintSet.END);
        landscapeSet.clear(nextImageButton.getId(), ConstraintSet.TOP);
        landscapeSet.clear(nextImageButton.getId(), ConstraintSet.BOTTOM);

        landscapeSet.connect(nextImageButton.getId(),
                ConstraintSet.END,
                closeImageButton.getId(),
                ConstraintSet.START,
                0);

        landscapeSet.connect(nextImageButton.getId(),
                ConstraintSet.TOP,
                landScapeControlView.getId(),
                ConstraintSet.TOP,
                dpToPx(8.0f));
        landscapeSet.setVisibility(nextImageButton.getId(), ConstraintSet.VISIBLE);
// Positioning fast rewind button.
        landscapeSet.clear(fastRewindImageButton.getId(),ConstraintSet.START);
        landscapeSet.clear(fastRewindImageButton.getId(),ConstraintSet.BOTTOM);
        landscapeSet.clear(fastRewindImageButton.getId(),ConstraintSet.TOP);
        landscapeSet.connect(fastRewindImageButton.getId(),
                ConstraintSet.START,
                landScapeControlView.getId(),
                ConstraintSet.START,
                0);

        landscapeSet.connect(fastRewindImageButton.getId(),
                ConstraintSet.TOP,
                landScapeControlView.getId(),
                ConstraintSet.TOP,
                0);

        landscapeSet.connect(fastRewindImageButton.getId(),
                ConstraintSet.BOTTOM,
                landScapeControlView.getId(),
                ConstraintSet.BOTTOM,
                0);
        landscapeSet.setVisibility(fastRewindImageButton.getId(), ConstraintSet.VISIBLE);
// Positioning fast forward button.
        landscapeSet.clear(fastForwardImageButton.getId(), ConstraintSet.END);
        landscapeSet.clear(fastForwardImageButton.getId(), ConstraintSet.BOTTOM);
        landscapeSet.clear(fastForwardImageButton.getId(), ConstraintSet.TOP);
        landscapeSet.connect(fastForwardImageButton.getId(),
                ConstraintSet.END,
                landScapeControlView.getId(),
                ConstraintSet.END,
                0);

        landscapeSet.connect(fastForwardImageButton.getId(),
                ConstraintSet.TOP,
                landScapeControlView.getId(),
                ConstraintSet.TOP,
                0);

        landscapeSet.connect(fastForwardImageButton.getId(),
                ConstraintSet.BOTTOM,
                landScapeControlView.getId(),
                ConstraintSet.BOTTOM,
                0);
        landscapeSet.setVisibility(fastForwardImageButton.getId(), ConstraintSet.VISIBLE);
//Positioning play-pause button
        landscapeSet.clear(playPauseImageButton.getId(), ConstraintSet.START);
        landscapeSet.clear(playPauseImageButton.getId(), ConstraintSet.END);
        landscapeSet.clear(playPauseImageButton.getId(), ConstraintSet.TOP);
        landscapeSet.clear(playPauseImageButton.getId(), ConstraintSet.BOTTOM);

        landscapeSet.connect(playPauseImageButton.getId(),
                ConstraintSet.START,
                fastRewindImageButton.getId(),
                ConstraintSet.END,
                0);

        landscapeSet.connect(playPauseImageButton.getId(),
                ConstraintSet.END,
                fastForwardImageButton.getId(),
                ConstraintSet.START,
                0);

        landscapeSet.connect(playPauseImageButton.getId(),
                ConstraintSet.TOP,
                landScapeControlView.getId(),
                ConstraintSet.TOP,
                0);

        landscapeSet.connect(playPauseImageButton.getId(),
                ConstraintSet.BOTTOM,
                landScapeControlView.getId(),
                ConstraintSet.BOTTOM,
                0);
        landscapeSet.setVisibility(playPauseImageButton.getId(), ConstraintSet.VISIBLE);
// Positioning time elapsed text view
        landscapeSet.clear(timeElapsedTextView.getId(), ConstraintSet.START);
        landscapeSet.clear(timeElapsedTextView.getId(), ConstraintSet.TOP);
        landscapeSet.clear(timeElapsedTextView.getId(), ConstraintSet.BOTTOM);

        landscapeSet.connect(timeElapsedTextView.getId(),
                ConstraintSet.START,
                landScapeControlView.getId(),
                ConstraintSet.START,
                0);

        landscapeSet.connect(timeElapsedTextView.getId(),
                ConstraintSet.TOP,
                seekBar.getId(),
                ConstraintSet.TOP,
                0);

        landscapeSet.connect(timeElapsedTextView.getId(),
                ConstraintSet.BOTTOM,
                landScapeControlView.getId(),
                ConstraintSet.BOTTOM,
                0);
        landscapeSet.setVisibility(timeElapsedTextView.getId(), ConstraintSet.VISIBLE);
// Positioning total duration text view
        landscapeSet.clear(totalDurationTextView.getId(), ConstraintSet.END);
        landscapeSet.clear(totalDurationTextView.getId(), ConstraintSet.TOP);
        landscapeSet.clear(totalDurationTextView.getId(), ConstraintSet.BOTTOM);

        landscapeSet.connect(totalDurationTextView.getId(),
                ConstraintSet.END,
                landScapeControlView.getId(),
                ConstraintSet.END,
                dpToPx(8.0f));

        landscapeSet.connect(totalDurationTextView.getId(),
                ConstraintSet.BOTTOM,
                landScapeControlView.getId(),
                ConstraintSet.BOTTOM,
                0);

        landscapeSet.connect(totalDurationTextView.getId(),
                ConstraintSet.TOP,
                seekBar.getId(),
                ConstraintSet.TOP,
                0);
        landscapeSet.setVisibility(totalDurationTextView.getId(), ConstraintSet.VISIBLE);
// Positioning Seek Bar
        landscapeSet.clear(seekBar.getId(), ConstraintSet.LEFT);
        landscapeSet.clear(seekBar.getId(), ConstraintSet.RIGHT);
        landscapeSet.clear(seekBar.getId(), ConstraintSet.TOP);

        landscapeSet.connect(seekBar.getId(),
                ConstraintSet.BOTTOM,
                landScapeControlView.getId(),
                ConstraintSet.BOTTOM,
                0);

        landscapeSet.connect(seekBar.getId(),
                ConstraintSet.START,
                timeElapsedTextView.getId(),
                ConstraintSet.END,
                0);

        landscapeSet.connect(seekBar.getId(),
                ConstraintSet.END,
                totalDurationTextView.getId(),
                ConstraintSet.START,
                0);
        landscapeSet.setVisibility(seekBar.getId(), ConstraintSet.VISIBLE);
        landscapeSet.applyTo(rootLayout);
    }

    private void resetControlView(){
        landscapeSet.clear(fastForwardImageButton.getId());
        landscapeSet.clear(fastRewindImageButton.getId());
        landscapeSet.clear(nextImageButton.getId());
        landscapeSet.clear(previousImageButton.getId());
        landscapeSet.clear(playPauseImageButton.getId());
        landscapeSet.clear(seekBar.getId());
        landscapeSet.clear(totalDurationTextView.getId());
        landscapeSet.clear(timeElapsedTextView.getId());
        landscapeSet.clear(landScapeControlView.getId());

        landscapeSet.clone(portraitSet);
    }

    private  void closeControlView(){

        TransitionManager.beginDelayedTransition(rootLayout);
        landscapeSet.setVisibility(R.id.btn_close, ConstraintSet.GONE);
        landscapeSet.setVisibility(R.id.btn_next, ConstraintSet.GONE);
        landscapeSet.setVisibility(R.id.btn_prev, ConstraintSet.GONE);
        landscapeSet.setVisibility(R.id.btn_fast_forward, ConstraintSet.GONE);
        landscapeSet.setVisibility(R.id.btn_fast_rewind, ConstraintSet.GONE);
        landscapeSet.setVisibility(R.id.btn_play_pause, ConstraintSet.GONE);
        landscapeSet.setVisibility(R.id.tv_duration, ConstraintSet.GONE);
        landscapeSet.setVisibility(R.id.tv_time_elapsed, ConstraintSet.GONE);
        landscapeSet.setVisibility(R.id.seek_bar_portrait, ConstraintSet.GONE);

        landscapeSet.clear(landScapeControlView.getId(), ConstraintSet.BOTTOM);
        landscapeSet.clear(landScapeControlView.getId(), ConstraintSet.END);

        landscapeSet.applyTo(rootLayout);
    }
    private int dpToPx(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
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
            releasePlayer();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(Build.VERSION.SDK_INT > 23){
            releasePlayer();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            configLayoutForLandscapeMode();
            disablePlayerControls();
            surfaceView.setOnClickListener(this);
        }else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            configLayoutForPortraitMode();
            enablePlayerControls();
            surfaceView.setOnClickListener(null);
            resetControlView();
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
}