package com.gmail.ferretti.valerio.worldbackgrounds;

import android.content.Context;

import mobi.upod.timedurationpicker.TimeDurationPicker;
import mobi.upod.timedurationpicker.TimeDurationPickerDialog;

public class WallpaperTimerUtils {

    public static void setupWallpaperTimer(final Context context){

        final boolean shouldStartAlarm = !WallpaperService.isServiceAlarmOn(context);

        if(shouldStartAlarm) {
            long initialDuration = QueryPreferences.getStoredDuration(context);
            if (initialDuration == 0) {
                initialDuration = 1000 * 3600 * 2; //2 hours in ms
            }

            TimeDurationPickerDialog timeDurationPickerDialog = new TimeDurationPickerDialog(
                    context,
                    new TimeDurationPickerDialog.OnDurationSetListener() {
                        @Override
                        public void onDurationSet(TimeDurationPicker view, long duration) {
                            QueryPreferences.setStoredDuration(context, duration);
                            QueryPreferences.setIsAlarmOn(context, shouldStartAlarm);
                            WallpaperService.setServiceAlarm(context, shouldStartAlarm);
                        }
                    },
                    initialDuration);
        }else{
            QueryPreferences.setIsAlarmOn(context, shouldStartAlarm);
            WallpaperService.setServiceAlarm(context, shouldStartAlarm);
        }
    }

}
