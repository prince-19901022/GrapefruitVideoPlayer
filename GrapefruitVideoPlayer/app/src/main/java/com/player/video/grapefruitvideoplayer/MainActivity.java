package com.player.video.grapefruitvideoplayer;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "www.d.com";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(isLandscape()){
            setTheme(R.style.FullScreenTheme);
        }else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView msg = findViewById(R.id.msg);

        msg.setText( isLandscape() ? "Orientation : Landscape" : "Orientation : Portrait");
    }

    private boolean isLandscape(){
        int currentOrientation = getResources().getConfiguration().orientation;
        return  currentOrientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.menu_item_settings){
            Toast.makeText(this, "A dialog will appear", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
