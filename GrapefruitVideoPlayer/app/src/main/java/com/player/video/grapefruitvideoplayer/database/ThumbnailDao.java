package com.player.video.grapefruitvideoplayer.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;


import java.util.HashMap;
import java.util.List;


@Dao
public interface ThumbnailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertThumbnail(Thumbnail thumb);

    // Room is not able to return records in other structures like map, hash map etc;
    // we need to convert it outside the dao.
    @Query("SELECT * FROM thumbnail")
    List<Thumbnail> loadAllThumbnail();
}
