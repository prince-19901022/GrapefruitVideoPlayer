package com.player.video.grapefruitvideoplayer.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.plattysoft.leonids.ParticleSystem;
import com.player.video.grapefruitvideoplayer.R;
import com.player.video.grapefruitvideoplayer.callbacks.SeekTimeSelectionListener;

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

    public static void showSettingsDialog(Activity activity, final SeekTimeSelectionListener selectionListener){

        AlertDialog.Builder alertBuilder= new AlertDialog.Builder(activity, R.style.SettingsDialog);
        alertBuilder.setTitle(R.string.dialog_title);

        int checkedItem= activity.getPreferences(Context.MODE_PRIVATE).
                getInt(activity.getString(R.string.seek_time_id), 0);

        alertBuilder.setSingleChoiceItems(R.array.seek_times, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        alertBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectionListener.onSeekTimeSelected(((AlertDialog)dialog).getListView().getCheckedItemPosition());
                dialog.dismiss();
            }
        });

        alertBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertBuilder.create().show();
    }

    public static void showParticleAnimationOn(View view, Activity activity){
        ParticleSystem ps= new ParticleSystem(activity, 100, R.drawable.star_pink,550);
        ps.setSpeedRange(0.1f, 0.25f);
        ps.oneShot(view, 100);
    }
}
