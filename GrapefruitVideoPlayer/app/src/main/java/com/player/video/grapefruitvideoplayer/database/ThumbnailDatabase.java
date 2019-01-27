package com.player.video.grapefruitvideoplayer.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Thumbnail.class}, version = 1, exportSchema = false)
public abstract class ThumbnailDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "thumbnail_database";
    private static final Object LOCK = new Object();
    private static ThumbnailDatabase sInstance;

    public static ThumbnailDatabase getInstance(Context appContext) {

        if (sInstance == null) {
            synchronized (LOCK){
                sInstance = Room.databaseBuilder(appContext,
                        ThumbnailDatabase.class,
                        ThumbnailDatabase.DATABASE_NAME)
                        .allowMainThreadQueries()
                        .build();
            }
        }

        return sInstance;
    }

    public abstract ThumbnailDao thumbnailDao();
}
