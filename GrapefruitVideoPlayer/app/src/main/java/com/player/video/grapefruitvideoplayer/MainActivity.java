package com.player.video.grapefruitvideoplayer;


import android.content.res.Configuration;
import android.database.Cursor;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


/*
* This app targets only api level 22 that runs on my personal android phone.
* In spite of loader being deprecated in 'Android P (API 28)', i am using it because i have no
 * intention to provide any compatibility to other api level.
 * */

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = "www.d.com";
    private static final int LOADER_ID = 2210;

    private RecyclerView videoList;
    private VideoListAdapter vlAdapter;

    private static final String[] VIDEO_PROJECTION = new String[] {
            MediaStore.Video.Media.DATA, //file path string
            MediaStore.Video.Media.DURATION, // duration in millisecond
            MediaStore.Video.Media.TITLE, // Title of the file
            MediaStore.Video.Media.SIZE, //Size of the file in bytes
            MediaStore.Video.Media.DISPLAY_NAME //Title of the file with type extension
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportLoaderManager().initLoader(LOADER_ID,null, this);
        initViews();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Uri baseUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        Log.d(LOG_TAG,"Loader Created");

        return new CursorLoader(this, baseUri,
                VIDEO_PROJECTION, null, null,
                null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

        data.moveToFirst();
        List<VideoModel> list = new ArrayList<>();
        VideoModel model;

        while (!data.isAfterLast()){

            model = new VideoModel();
            model.setTitle(data.getString(2));
            model.setDurationInMilliSecond(data.getLong(1));
            model.setFilePath(data.getString(0));
            model.setFileSizeInBytes(data.getLong(3));
            model.setDisplayName(data.getString(4));
            list.add(model);
            data.moveToNext();
        }
        vlAdapter.addData(list);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.d(LOG_TAG,"Loader Reset");
    }

    private void initViews(){

        videoList = findViewById(R.id.rv_video_list);
        videoList.setLayoutManager(new LinearLayoutManager(this));
        videoList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        vlAdapter = new VideoListAdapter(this);
        videoList.setAdapter(vlAdapter);
    }
}
