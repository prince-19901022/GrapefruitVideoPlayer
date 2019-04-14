package com.player.video.grapefruitvideoplayer;

import java.util.concurrent.TimeUnit;

public class GPlayerUtil {

    public static String formatDuration(long duration){

        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        long minutes = hours > 0 ?
                TimeUnit.MILLISECONDS.toMinutes(duration % TimeUnit.HOURS.toMillis(1)) :
                TimeUnit.MILLISECONDS.toMinutes(duration);

        long seconds = minutes > 0 ?
                TimeUnit.MILLISECONDS.toSeconds(duration % TimeUnit.MINUTES.toMillis(1)) :
                TimeUnit.MILLISECONDS.toSeconds(duration);

        if(hours == 0){
            return String.format("%02d : %02d", minutes, seconds);
        }
        return String.format("%02d : %02d : %02d", hours, minutes, seconds);
    }
}
