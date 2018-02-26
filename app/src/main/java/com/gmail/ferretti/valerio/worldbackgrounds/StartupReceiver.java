package com.gmail.ferretti.valerio.worldbackgrounds;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by ferretti on 30/12/17.
 */

public class StartupReceiver extends BroadcastReceiver {

    private static final String TAG = "StartupReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "BOOT_COMPLETED intent received!");
        WallpaperService.setServiceAlarm(context, QueryPreferences.isAlarmOn(context));
    }
}
