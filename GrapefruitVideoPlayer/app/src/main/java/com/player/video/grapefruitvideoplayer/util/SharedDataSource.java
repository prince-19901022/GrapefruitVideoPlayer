package com.player.video.grapefruitvideoplayer.util;

import java.util.List;

public class SharedDataSource {

    private List<VideoModel> dataSource;
    private static SharedDataSource sInstance;
    private int dataSrcSize;

    private SharedDataSource(){
    }

    public static SharedDataSource getInstance(){
        if(sInstance == null){
            sInstance= new SharedDataSource();
            sInstance.dataSrcSize= 0;
        }
        return sInstance;
    }

    public void setDataSource(List<VideoModel> dataSource){
        this.dataSource= dataSource;
        dataSrcSize= dataSource.size();
    }

    public VideoModel get(int index){
        return dataSource.get(index);
    }

    public int dataSourceSize(){
        return dataSrcSize;
    }
}
