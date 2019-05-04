package com.player.video.grapefruitvideoplayer.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.player.video.grapefruitvideoplayer.model.VideoModel;
import com.player.video.grapefruitvideoplayer.task.GPlayerExecutor;
import com.player.video.grapefruitvideoplayer.task.LoadingTask;

import java.util.List;

/*
*  AndroidViewModel is a subclass of ViewModel. As per documentation 'ViewModel' outlives the lifecycle
*  of an activity or fragment. So it should not hold activity context(it may lead memory leak and hence
*  NPE or other exceptions). But if any operation requires to showing toast or database operation,
*  then 'AndroidViewModel' comes handy. As it requires application context for instantiation.
*  Primarily this is soul purpose for using 'AndroidViewModel' instead of 'ViewModel'
* */

public class GPlayerViewModel extends AndroidViewModel {

    private MutableLiveData<List<VideoModel>> liveData;

    public GPlayerViewModel(@NonNull Application application) {
        super(application);
        liveData = new MutableLiveData<>();
        GPlayerExecutor.getInstance().getDiskIO().execute(new LoadingTask(false,this,application));
    }


    public void setVideoList(List<VideoModel> videoList){
        //Currently there is no chance of getting invoked from ui thread
        liveData.postValue(videoList);
    }

    public MutableLiveData<List<VideoModel>> getLiveData() {
        return liveData;
    }
}
