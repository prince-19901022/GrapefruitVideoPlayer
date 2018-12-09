package com.player.video.grapefruitvideoplayer;


import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class VideoControllerFragment extends Fragment {


    public VideoControllerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video_controller, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* All working views of this fragment has been defined in landscape variant of this
        * fragment. During config change when system assigns portrait variant, views do not belong
        * to portrait variant become null and fragment gets removed automatically. And this fragment is
        * intended to use only in landscape mode, so view initialization and manipulation is done
        * only in landscape mode*/

        if(isLandscape()){
            final ImageView play = view.findViewById(R.id.iv_play);
            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getFragmentManager().beginTransaction().remove(VideoControllerFragment.this).commit();
                }
            });
        }
    }

    private boolean isLandscape(){
        int currentOrientation = getResources().getConfiguration().orientation;
        return  currentOrientation == Configuration.ORIENTATION_LANDSCAPE;
    }
}
