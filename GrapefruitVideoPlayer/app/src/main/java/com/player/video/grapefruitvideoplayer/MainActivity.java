package com.player.video.grapefruitvideoplayer;



import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.player.video.grapefruitvideoplayer.task.GPlayerViewModel;
import com.player.video.grapefruitvideoplayer.task.LoadingTask;

import java.util.List;

public class MainActivity extends AppCompatActivity{

    private static final String LOG_TAG = "www.d.com";

    private RecyclerView videoList;
    private VideoListAdapter vlAdapter;

    private GPlayerObserver contentObserver;
    private GPlayerViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle(R.string.main_act_label);

        initViews();

        viewModel = ViewModelProviders.of(this).get(GPlayerViewModel.class);
        viewModel.getLiveData().observe(this, new Observer<List<VideoModel>>() {
            @Override
            public void onChanged(@Nullable List<VideoModel> videoModels) {
                vlAdapter.addData(videoModels);
            }
        });
    }

    private void initViews(){

        videoList = findViewById(R.id.rv_video_list);
        videoList.setLayoutManager(new LinearLayoutManager(this));
        videoList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        vlAdapter = new VideoListAdapter(this);
        videoList.setAdapter(vlAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        contentObserver = new GPlayerObserver(null, viewModel, getApplication());
        getContentResolver().registerContentObserver(LoadingTask.CONTENT_URI, true, contentObserver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG,"Activity Stopped");
        getContentResolver().unregisterContentObserver(contentObserver);
    }
}
