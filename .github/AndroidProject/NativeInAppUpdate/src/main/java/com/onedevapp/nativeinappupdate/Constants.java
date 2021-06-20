package com.onedevapp.nativeinappupdate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

/**
 * Some Constant values
 */
public class Constants {
    public static final int PLAY_STORE_UPDATE = 0;
    public static final int THIRD_PARTY_UPDATE = 1;

    /**
     * To write library messages to logcat
     */
    public static boolean enableLog = false;

    /**
     * WriteLog to log library messages to logcat
     * Can toggle on/off with enableLog boolean at any time
     *
     * @param message Log Message
     */
    public static void WriteLog(String message) {
        if (enableLog) Log.d("NativePlugin", message);
    }



    /**
     * Check whether any network is available or not
     *
     * @param activity current context
     * @return boolean true if any network available else false
     */
    @SuppressLint("MissingPermission")
    public static boolean IsNetworkAvailable(final Activity activity) {

        ConnectivityManager connMgr = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();
        if (activeNetworkInfo != null) { // connected to the internet
            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                Constants.WriteLog("Active network is wifi");
                return true;
            } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                Constants.WriteLog("Active network is mobile");
                return true;
            } else {
                return false;
            }
        } else {
            Constants.WriteLog("No active network found");
            return false;
        }
    }

    /**
     * Check device build version
     *
     * @return true if build version is over Marshmallow else false
     */
    public static boolean isOverMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * This method checks whether the given permission is already granted or not.
     *
     * @param context    This is context of the current activity
     * @param permission This is the permission we need to check
     * @return boolean     Returns True if already permission granted for this permission else false.
     */
    public static boolean CheckPermission(Context context, String permission) {
        if (!isOverMarshmallow()) {
            return false;
        }
        //Determine whether you have been granted a particular permission.
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

}
