package com.gmail.ferretti.valerio.worldbackgrounds;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by ferretti on 16/03/18.
 */

public class NetworkUtils {

    public static boolean isNetworkUpAndRunning(Context context){
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = (activeNetwork != null && activeNetwork.isConnectedOrConnecting());

        return isConnected;
    }
}
