package com.gmail.ferretti.valerio.worldbackgrounds;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by ferretti on 24/10/17.
 * This class reads and writes in a background thread a file in the application sandbox. It stores and
 * retrieves data just like it was a Bundle, with a key-value pair. It is used to store data that is
 * useful in other point of the application like the query string, the last result id and the info
 * about whether the alarm was set and needs to be reset after a reboot or not. All method are static
 * so that we can call them from wherever without an instance of QueryPreferences.
 */

public class QueryPreferences {

    private static final String PREF_SEARCH_QUERY = "SearchQuery";
    private static final String PREF_IS_ALARM_ON = "isAlarmOn";
    private static final String PREF_DURATION = "Duration";
    private static final String PREF_ONE_TIME_SCREEN = "isToBeShown";

    //Function to store and retrieve the query string
    public static String getStoredQuery(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).
                getString(PREF_SEARCH_QUERY, null);
    }

    public static void setStoredString(Context context, String query){
        PreferenceManager.getDefaultSharedPreferences(context).
                edit().putString(PREF_SEARCH_QUERY, query).apply();
    }

    public static long getStoredDuration(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).
                getLong(PREF_DURATION, 0);
    }

    public static void setStoredDuration(Context context, Long duration){
        PreferenceManager.getDefaultSharedPreferences(context).
                edit().putLong(PREF_DURATION, duration).apply();
    }

    public static boolean isAlarmOn(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).
                getBoolean(PREF_IS_ALARM_ON, false);
    }

    public static void setIsAlarmOn(Context context, boolean isOn){
        PreferenceManager.getDefaultSharedPreferences(context).
                edit().putBoolean(PREF_IS_ALARM_ON, isOn).apply();
    }

    public static boolean isOneTimeScreenToBeShown(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).
        getBoolean(PREF_ONE_TIME_SCREEN, true);
    }

    public static void setIsOneTimeScreenToBeShown(Context context, boolean isToBeShown){
        PreferenceManager.getDefaultSharedPreferences(context).
                edit().putBoolean(PREF_ONE_TIME_SCREEN, isToBeShown).apply();
    }

}