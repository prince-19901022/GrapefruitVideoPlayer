package com.player.video.grapefruitvideoplayer.task;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GPlayerExecutor {

    private static final Object LOCK = new Object();
    private Executor diskIO;

    private static GPlayerExecutor sInstance;

    private GPlayerExecutor(Executor diskIO) {
        this.diskIO = diskIO;
    }

    public static GPlayerExecutor getInstance(){

        if (sInstance == null){

            synchronized (LOCK){
                sInstance = new GPlayerExecutor(Executors.newSingleThreadExecutor());
            }
        }
        return sInstance;
    }

    public Executor getDiskIO() {
        return diskIO;
    }
}
