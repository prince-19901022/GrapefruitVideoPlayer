package com.player.video.grapefruitvideoplayer.util;

import android.app.Application;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.widget.Toast;

import com.player.video.grapefruitvideoplayer.task.GPlayerExecutor;
import com.player.video.grapefruitvideoplayer.task.GPlayerViewModel;
import com.player.video.grapefruitvideoplayer.task.LoadingTask;

public class GPlayerObserver extends ContentObserver {
    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */

    private GPlayerViewModel viewModel;
    private Application application;

    public GPlayerObserver(Handler handler, GPlayerViewModel viewModel, Application application) {
        super(handler);

        this.application = application;
        this.viewModel = viewModel;
    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Toast.makeText(application, "Video Files On Device Changed", Toast.LENGTH_SHORT).show();
        GPlayerExecutor.getInstance().getDiskIO().execute(new LoadingTask(true,viewModel,application));
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
    }
}
