package com.gmail.ferretti.valerio.worldbackgrounds;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by ferretti on 29/12/17.
 */

public class WallpaperService extends IntentService {

    private static final String TAG = "WallpaperService";

    public WallpaperService(){
        super(TAG);
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG, "New WallpaperService intent received!");

        //Setting up StorageUtils object
        final StorageUtils storageUtils = new StorageUtils(getApplicationContext());
        storageUtils.setWallpaperSetListener(new StorageUtils.WallpaperSetListener() {
            @Override
            public void onWallpaperSet() {
                //Empty implementation
            }
        });

        //Getting index of a downloaded picture randomly
        ArrayList<String> downloadedPictures = storageUtils.getDownloadedPicturesNames();
        Random random = new Random();
        int pictureIndex = random.nextInt(downloadedPictures.size());
        final String wallpaperName = downloadedPictures.get(pictureIndex);
        Log.i(TAG, "Picture number " + pictureIndex + ", downloadedPicture.size() = " + downloadedPictures.size());

        //Setting selected picture as wallpaper
        Handler uiHandler = new Handler(Looper.getMainLooper());
        uiHandler.post(new Runnable(){
            @Override
            public void run() {
                storageUtils.setWallpaper(wallpaperName);
            }
        });
    }


    public static void setServiceAlarm(Context context, boolean isOn){
        long duration = QueryPreferences.getStoredDuration(context);
        if(duration <= 1000) duration = 1000;
        Intent intent = WallpaperService.newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if(isOn){
            alarmManager.setRepeating(
                    AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(),
                    duration,
                    pendingIntent);
            Toast.makeText(context, R.string.wallpaper_timer_started, Toast.LENGTH_LONG).show();
        }else{
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
            Toast.makeText(context, R.string.wallpaper_timer_ended, Toast.LENGTH_LONG).show();
        }
    }


    public static boolean isServiceAlarmOn(Context context){
        Intent intent = WallpaperService.newIntent(context);
        PendingIntent pendingIntent =
                PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE);
        return pendingIntent != null;
    }


    public static Intent newIntent(Context context){
        return new Intent(context, WallpaperService.class);
    }
}
