package com.player.video.grapefruitvideoplayer;

import android.media.MediaPlayer;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoPlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private String path;
    private int listIndex;
    private long duration;
    private String displayName;

    private VideoView videoView;
    private ImageButton nextImageButton;
    private ImageButton previousImageButton;
    private ImageButton fastForwardImageButton;
    private ImageButton fastRewindImageButton;
    private ImageButton playPauseImageButton;
    private TextView progressTimeTextView;
    private TextView totalProgressTimeTextView;
    private AppCompatSeekBar seekBar;

    private MediaPlayer mMediaPlayer;

    private int seekBy = 5000; //time in millisecond

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        path = getIntent().getExtras().getString(MainActivity.PATH);
        listIndex = getIntent().getExtras().getInt(MainActivity.LIST_INDEX);
        duration = getIntent().getExtras().getLong(MainActivity.DURATION);
        displayName = getIntent().getExtras().getString(MainActivity.DISPLAY_NAME);

        getSupportActionBar().setTitle(displayName);

        initViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_item_settings :
                Toast.makeText(this,"Settings will be added soon", Toast.LENGTH_SHORT).show();
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

    private void initViews(){
        videoView = findViewById(R.id.videoView);

        nextImageButton = findViewById(R.id.btn_next);
        previousImageButton = findViewById(R.id.btn_prev);
        
        fastForwardImageButton = findViewById(R.id.btn_fast_forward);
        fastRewindImageButton = findViewById(R.id.btn_fast_rewind);
        
        playPauseImageButton = findViewById(R.id.btn_play_pause);
        
        seekBar = findViewById(R.id.seek_bar_portrait);
        
        progressTimeTextView = findViewById(R.id.tv_progress_time);
        totalProgressTimeTextView = findViewById(R.id.tv_total_progress);
        
        nextImageButton.setOnClickListener(this);

        previousImageButton.setOnClickListener(this);
        fastForwardImageButton.setOnClickListener(this);
        fastRewindImageButton.setOnClickListener(this);
        playPauseImageButton.setOnClickListener(this);

        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handlePlayPause();
                return false;
            }
        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mMediaPlayer = mp;
                seekBar.setMax(mMediaPlayer.getDuration());
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.stopPlayback();
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.btn_play_pause : handlePlayPause();break;
            case R.id.btn_fast_forward : seekWith(1);break;
            case R.id.btn_fast_rewind : seekWith(-1);break;
        }
    }

    private void seekWith(int mulFactor) {
        //Fast forwarding by 5s.
        //Not Working
        if(mMediaPlayer != null){
           handlePlayPause();
            int msToSeek = videoView.getCurrentPosition() + (mulFactor * seekBy);
            if(msToSeek >= 0 && msToSeek <= mMediaPlayer.getDuration()){

                //seekBar.setProgress(msToSeek);
                videoView.seekTo(msToSeek);
            }
        }
    }

    private void handlePlayPause() {
        //If videoView is in playing, then it can be assumed that media player object is not null
        //Here mediaPlayer object is used directly because videoView resume method do not work.
        if(videoView.isPlaying()){
            mMediaPlayer.pause();
            playPauseImageButton.setImageResource(R.drawable.ic_pause);
        }else{
            mMediaPlayer.start();
            playPauseImageButton.setImageResource(R.drawable.ic_play);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        videoView.setVideoPath(path);
        videoView.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        videoView.stopPlayback();
    }
}
