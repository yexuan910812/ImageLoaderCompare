package com.tencent.henryye.myapplication;

import android.app.Application;

/**
 * Created by henryye on 2017/12/15.
 */

public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Metronome.INSTANCE.start();
        LoaderFactory.INSTANCE.initAll(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Metronome.INSTANCE.stop();
    }
}
