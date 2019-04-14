package com.player.video.grapefruitvideoplayer;

import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class VideoPlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private String path;
    private int listIndex;
    private long duration;
    private String displayName;

    private ImageButton nextImageButton;
    private ImageButton previousImageButton;
    private ImageButton fastForwardImageButton;
    private ImageButton fastRewindImageButton;
    private ImageButton playPauseImageButton;
    private TextView progressTimeTextView;
    private TextView totalDurationTextView;
    private AppCompatSeekBar seekBar;

    private SimpleExoPlayer sePlayer;
    private PlayerView playerView;

    private long seekBy = 5000; //time in millisecond

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

        playerView = findViewById(R.id.playerView);
        nextImageButton = findViewById(R.id.btn_next);
        previousImageButton = findViewById(R.id.btn_prev);

        fastForwardImageButton = findViewById(R.id.btn_fast_forward);
        fastRewindImageButton = findViewById(R.id.btn_fast_rewind);

        playPauseImageButton = findViewById(R.id.btn_play_pause);

        seekBar = findViewById(R.id.seek_bar_portrait);

        progressTimeTextView = findViewById(R.id.tv_progress_time);

        totalDurationTextView = findViewById(R.id.tv_total_progress);
        totalDurationTextView.setText(GPlayerUtil.formatDuration(duration));

        nextImageButton.setOnClickListener(this);

        previousImageButton.setOnClickListener(this);
        fastForwardImageButton.setOnClickListener(this);
        fastRewindImageButton.setOnClickListener(this);
        playPauseImageButton.setOnClickListener(this);

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

        sePlayer.seekTo(sePlayer.getCurrentPosition()+seekBy);

    }

    private void handlePlayPause() {

        if(sePlayer.getPlayWhenReady()){
            //Media is already playing. So, pause it
            sePlayer.setPlayWhenReady(false);
            playPauseImageButton.setImageResource(R.drawable.ic_pause);
        }else{
            //Media is in paused state. So resume playing.
            sePlayer.setPlayWhenReady(true);
            playPauseImageButton.setImageResource(R.drawable.ic_play);
        }
    }

    private void initPlayer(){

        playerView.requestFocus();

        sePlayer = ExoPlayerFactory.newSimpleInstance(this,new DefaultTrackSelector());
        playerView.setPlayer(sePlayer);
        sePlayer.setPlayWhenReady(true);
        sePlayer.seekTo(0,0);

        sePlayer.addListener(new GPlayerEventListener());
        MediaSource mediaSource = buildMediaSource(Uri.parse(path));
        sePlayer.prepare(mediaSource,true,false);
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(new DefaultDataSourceFactory(this,
                Util.getUserAgent(this,getString(R.string.app_name)))).
                createMediaSource(uri);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initPlayer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if ((Util.SDK_INT <= 23 || sePlayer == null)) {
            initPlayer();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
