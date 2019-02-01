package com.player.video.grapefruitvideoplayer;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class VideoPlayerActivity extends AppCompatActivity {

    private String path;
    private int listIndex;
    private long duration;
    private String displayName;

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
}
