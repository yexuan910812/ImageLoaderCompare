package com.tencent.henryye.myapplication;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

@SuppressLint("LongLogTag")
public class LargeImageViewActivity extends AppCompatActivity implements PickLoaderView.UICallback {
    private static final String TAG = "MicroMsg.LargeImageViewActivity";

    private PickLoaderView mPickLoaderView = null;
    private ImageView mLargeImageView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_large_image_view);

        mPickLoaderView = findViewById(R.id.pick_loader_view);
        mLargeImageView = findViewById(R.id.image_view);

        mPickLoaderView.setUICallback(this);
    }

    @Override
    public void onRequestStart(LoaderType type) {
        LoaderFactory.INSTANCE.getLoader(type).fillingWithUri(this, mLargeImageView, Utils.getUriFromAssets("extremely_large.png"), new LoaderFactory.IOnLoadCallback() {
            @Override
            public void onLoad(boolean isSuc) {
                Log.i(TAG, "hy: is load succ: " + isSuc);
                mPickLoaderView.onLoadedDone();
            }
        });
    }
}
