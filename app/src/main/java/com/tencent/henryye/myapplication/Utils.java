package com.tencent.henryye.myapplication;

import android.app.ActivityManager;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.RadioGroup;

/**
 * Created by henryye on 2017/12/13.
 */

public class Utils {
    private static final String TAG = "MicroMsg.Utils";

    public static Uri getUriFromAssets(String name, LoaderType loaderType) {
        switch (loaderType) {
            case Glide:
            case Picasso:
                return Uri.parse("file:///android_asset/" + name);
            case Fresco:
            default:
                return Uri.parse("asset:///" + name);
        }

    }

    public static double getCurrentTotalMemoryInMB(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        assert am != null;
        am.getMemoryInfo(mi);
        double memUsedInKB = (double) (mi.totalMem - mi.availMem) / 1024 / 1024;
//        Log.d(TAG, "hy: memory is " + memUsedInKB);
        return memUsedInKB;
    }

    public static void setSelectEnabled(RadioGroup group, boolean enabled) {
        for (int i = 0; i < group.getChildCount(); i++) {
            (group.getChildAt(i)).setEnabled(enabled);
        }
    }
}
